package sct.carpetsctadditon.mixin;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sct.carpetsctadditon.SCTSettings;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Shadow private int itemAge;

    @Inject(method = "tick", at = @At("TAIL"))
    private void customTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;

        if (SCTSettings.CustomItemDiscardAge != 6000 ) {
            if (SCTSettings.CustomItemDiscardAge == -1) {
                if (itemAge >= 5000){
                    itemAge = 0;
                }
            } else if (itemEntity.age >= SCTSettings.CustomItemDiscardAge)
                itemEntity.discard();
        }
    }
}
