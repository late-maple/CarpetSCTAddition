package sct.carpetsctadditon.dropsintoshulker;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class DropsConfig {
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "drops_into_shulker.properties");
    private static boolean globalEnabled = true;
    private static final Map<UUID, Boolean> playerStates = new HashMap<>();

    public static void loadConfig() {
        Properties props = new Properties();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                props.load(reader);
                globalEnabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            } catch (IOException e) {
                sct.carpetsctadditon.CarpetSCTAddition.LOGGER.error("Failed to load config", e);
            }
        } else {
            saveConfig();
        }
    }

    public static void saveConfig() {
        Properties props = new Properties();
        props.setProperty("enabled", String.valueOf(globalEnabled));

        try (FileWriter writer = new FileWriter(configFile)) {
            props.store(writer, "Drops Into Shulker Config");
        } catch (IOException e) {
            sct.carpetsctadditon.CarpetSCTAddition.LOGGER.error("Failed to save config", e);
        }
    }

    public static boolean isGloballyEnabled() {
        return globalEnabled;
    }

    public static void setGloballyEnabled(boolean enabled) {
        globalEnabled = enabled;
        saveConfig();
    }
    
    public static boolean isPlayerEnabled(PlayerEntity player) {
        // 如果玩家有独立设置，使用玩家的设置，否则使用全局设置
        return playerStates.getOrDefault(player.getUuid(), globalEnabled);
    }
    
    public static void setPlayerEnabled(PlayerEntity player, boolean enabled) {
        playerStates.put(player.getUuid(), enabled);
    }
}