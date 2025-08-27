package sct.carpetsctadditon.mixin;

import sct.carpetsctadditon.dropsintoshulker.DropsConfig;
import sct.carpetsctadditon.dropsintoshulker.ShulkerInventoryWrapper;
import sct.carpetsctadditon.SCTSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
    @Shadow
    @Final
    public static int OFF_HAND_SLOT;

    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Inject(method = "insertStack", at = @At("HEAD"), cancellable = true)
    private void insertStack(ItemStack collected, CallbackInfoReturnable<Boolean> cir) {
        // 检查Carpet规则是否启用
        if (SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.FALSE) {
            return;
        }
        
        // 检查功能是否对当前玩家启用
        if (!DropsConfig.isPlayerEnabled(player)) {
            return;
        }

        // 收集所有潜影盒
        List<Integer> shulkerSlots = new ArrayList<>();
        List<ItemStack> shulkerItems = new ArrayList<>();
        List<Block> shulkerBlocks = new ArrayList<>();

        ItemStack offHandStack = getStack(OFF_HAND_SLOT);
        Block offHandBlock = Block.getBlockFromItem(offHandStack.getItem());

        // 根据模式检查副手潜影盒
        if ((SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.ALL || 
             SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.OFFHAND) &&
            offHandBlock instanceof ShulkerBoxBlock) {
            shulkerSlots.add(OFF_HAND_SLOT);
            shulkerItems.add(offHandStack);
            shulkerBlocks.add(offHandBlock);
        }

        // 根据模式检查快捷栏中的潜影盒
        if (SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.ALL || 
            SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.HOTBAR) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = getStack(i);
                Block block = Block.getBlockFromItem(stack.getItem());
                if (block instanceof ShulkerBoxBlock) {
                    shulkerSlots.add(i);
                    shulkerItems.add(stack);
                    shulkerBlocks.add(block);
                }
            }
        }

        // 根据模式检查背包中的潜影盒
        if (SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.ALL || 
            SCTSettings.dropsIntoShulker == SCTSettings.DropsIntoShulkerMode.INVENTORY) {
            for (int i = 9; i < 36; i++) {
                ItemStack stack = getStack(i);
                Block block = Block.getBlockFromItem(stack.getItem());
                if (block instanceof ShulkerBoxBlock) {
                    shulkerSlots.add(i);
                    shulkerItems.add(stack);
                    shulkerBlocks.add(block);
                }
            }
        }

        // 如果没有找到潜影盒，直接返回
        if (shulkerSlots.isEmpty()) {
            return;
        }

        ItemStack originalStack = collected.copy();
        // 遍历所有潜影盒尝试放入物品
        for (int i = 0; i < shulkerSlots.size(); i++) {
            int shulkerSlot = shulkerSlots.get(i);
            ItemStack shulkerItem = shulkerItems.get(i);
            Block shulkerBlock = shulkerBlocks.get(i);

            // 检查潜影盒是否堆叠
            if (shulkerItem.getCount() > 1) {
                // 发送错误消息给玩家
                if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("Drops-Into-Shulker only works with non-stacked shulker boxes.").copy().withColor(Formatting.RED.getColorValue())));
                }
                continue;
            }

            ShulkerBoxBlockEntity sEntity = (ShulkerBoxBlockEntity) ((ShulkerBoxBlock) shulkerBlock).createBlockEntity(BlockPos.ORIGIN, ((ShulkerBoxBlock) shulkerBlock).getDefaultState());
            // 重新读取组件数据
            sEntity.readComponents(shulkerItem);

            ShulkerInventoryWrapper wrapper = new ShulkerInventoryWrapper(null, BlockPos.ORIGIN, ((ShulkerBoxBlock) shulkerBlock).getDefaultState(), sEntity);
            ItemStack remainder = wrapper.addStack(collected);

            // 如果物品有变动，更新潜影盒
            if (remainder.getCount() != collected.getCount()) {
                shulkerItem.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(((sct.carpetsctadditon.dropsintoshulker.mixin.ShulkerBoxBlockEntityAccessor) sEntity).getInventory()));
                // 触发成就
                if (player instanceof ServerPlayerEntity) {
                    Criteria.INVENTORY_CHANGED.trigger((ServerPlayerEntity) player, player.getInventory(), collected);
                }
                collected.setCount(remainder.getCount());
                
                // 如果物品已经完全放入，返回成功
                if (remainder.isEmpty()) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        // 如果所有潜影盒都满了且无法放入物品，则显示提示信息
        if (collected.getCount() == originalStack.getCount() && !collected.isEmpty() && player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("无法放入潜影盒").copy().withColor(Formatting.RED.getColorValue())));
        }

        // 如果物品已经完全放入，返回成功
        // 否则返回false，让原版逻辑处理剩余物品
        if (collected.isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}