package sct.carpetsctadditon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import sct.carpetsctadditon.SCTSettings;
import sct.carpetsctadditon.explosion.ExplosionProtectionManager;

public class ExplosionProtectionCommand {
    private static final SuggestionProvider<ServerCommandSource> POSITION_SUGGESTIONS = (context, builder) -> {
        BlockPos blockPos = context.getSource().getPlayer().getBlockPos();
        return builder.suggest(blockPos.getX()).suggest(blockPos.getY()).suggest(blockPos.getZ()).buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("explosionprotection")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("radius")
                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    if (!SCTSettings.explosionProtectionAreas) {
                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("carpetsctaddition.command.explosionprotection.not_enabled"),
                                                true
                                        );
                                        return 0;
                                    }

                                    int radius = IntegerArgumentType.getInteger(context, "radius");
                                    BlockPos playerPos = context.getSource().getPlayer().getBlockPos();

                                    // 创建从世界最低点到最高点的保护区域
                                    BlockPos pos1 = new BlockPos(playerPos.getX() - radius, context.getSource().getWorld().getBottomY(), playerPos.getZ() - radius);
                                    BlockPos pos2 = new BlockPos(playerPos.getX() + radius, context.getSource().getWorld().getTopY() - 1, playerPos.getZ() + radius);

                                    ExplosionProtectionManager.addProtectionArea(BlockBox.create(pos1, pos2));

                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("carpetsctaddition.command.explosionprotection.radius.added", radius),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("area")
                        .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
                                        .executes(context -> {
                                            if (!SCTSettings.explosionProtectionAreas) {
                                                context.getSource().sendFeedback(
                                                        () -> Text.translatable("carpetsctaddition.command.explosionprotection.not_enabled"),
                                                        true
                                                );
                                                return 0;
                                            }

                                            BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                                            BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");

                                            ExplosionProtectionManager.addProtectionArea(BlockBox.create(pos1, pos2));

                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("carpetsctaddition.command.explosionprotection.area.added",
                                                            pos1.toShortString(), pos2.toShortString()),
                                                    true
                                            );
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("list")
                        .executes(context -> {
                            if (!SCTSettings.explosionProtectionAreas) {
                                context.getSource().sendFeedback(
                                        () -> Text.translatable("carpetsctaddition.command.explosionprotection.not_enabled"),
                                        true
                                );
                                return 0;
                            }

                            var areas = ExplosionProtectionManager.getProtectionAreas();
                            if (areas.isEmpty()) {
                                context.getSource().sendFeedback(
                                        () -> Text.translatable("carpetsctaddition.command.explosionprotection.list.empty"),
                                        true
                                );
                            } else {
                                context.getSource().sendFeedback(
                                        () -> Text.translatable("carpetsctaddition.command.explosionprotection.list.count", areas.size()),
                                        true
                                );

                                for (int i = 0; i < areas.size(); i++) {
                                    final int displayIndex = i + 1;
                                    var area = areas.get(i);
                                    BlockPos minPos = new BlockPos(area.getMinX(), area.getMinY(), area.getMinZ());
                                    BlockPos maxPos = new BlockPos(area.getMaxX(), area.getMaxY(), area.getMaxZ());
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("carpetsctaddition.command.explosionprotection.list.entry",
                                                    displayIndex,
                                                    minPos.toShortString(),
                                                    maxPos.toShortString()),
                                            false
                                    );
                                }
                            }
                            return 1;
                        })
                )
                .then(CommandManager.literal("del")
                        .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    if (!SCTSettings.explosionProtectionAreas) {
                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("carpetsctaddition.command.explosionprotection.not_enabled"),
                                                true
                                        );
                                        return 0;
                                    }

                                    int index = IntegerArgumentType.getInteger(context, "index");
                                    var areas = ExplosionProtectionManager.getProtectionAreas();

                                    if (index >= areas.size()) {
                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("carpetsctaddition.command.explosionprotection.del.invalid_index", areas.size()),
                                                true
                                        );
                                        return 0;
                                    }

                                    ExplosionProtectionManager.removeProtectionArea(index);
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("carpetsctaddition.command.explosionprotection.del.success", index),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("clear")
                        .executes(context -> {
                            if (!SCTSettings.explosionProtectionAreas) {
                                context.getSource().sendFeedback(
                                        () -> Text.translatable("carpetsctaddition.command.explosionprotection.not_enabled"),
                                        true
                                );
                                return 0;
                            }

                            ExplosionProtectionManager.clearProtectionAreas();
                            context.getSource().sendFeedback(
                                    () -> Text.translatable("carpetsctaddition.command.explosionprotection.clear.success"),
                                    true
                            );
                            return 1;
                        })
                )
        );
    }
}