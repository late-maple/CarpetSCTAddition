package sct.carpetsctadditon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import sct.carpetsctadditon.dropsintoshulker.DropsConfig;

public class DropsIntoShulkerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dropsintoshulker")
            .then(CommandManager.literal("toggle")
                .executes(context -> {
                    boolean newState = !DropsConfig.isPlayerEnabled(context.getSource().getPlayer());
                    DropsConfig.setPlayerEnabled(context.getSource().getPlayer(), newState);
                    context.getSource().sendFeedback(
                        () -> Text.literal("DropsIntoShulker for you is now " + (newState ? "enabled" : "disabled")), 
                        true);
                    return 1;
                })
            )
            .then(CommandManager.literal("on")
                .executes(context -> {
                    DropsConfig.setPlayerEnabled(context.getSource().getPlayer(), true);
                    context.getSource().sendFeedback(
                        () -> Text.literal("DropsIntoShulker for you is now enabled"), 
                        true);
                    return 1;
                })
            )
            .then(CommandManager.literal("off")
                .executes(context -> {
                    DropsConfig.setPlayerEnabled(context.getSource().getPlayer(), false);
                    context.getSource().sendFeedback(
                        () -> Text.literal("DropsIntoShulker for you is now disabled"), 
                        true);
                    return 1;
                })
            )
        );
    }
}