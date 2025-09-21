package sct.carpetsctadditon;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import com.google.gson.JsonElement;
import sct.carpetsctadditon.commands.ExplosionProtectionCommand;
import sct.carpetsctadditon.dropsintoshulker.DropsConfig;
import sct.carpetsctadditon.commands.DropsIntoShulkerCommand;

public class CarpetSCTAddition implements CarpetExtension, ModInitializer {
	private static final String MOD_ID = "carpetsctaddition";
	private static final String MOD_NAME = "CarpetSCTAddition";
	private static String version;


	public static String getVersion() {
		return version;
	}
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize()
	{
		final ModMetadata metadata = FabricLoader.getInstance()
				.getModContainer(MOD_ID)
				.orElseThrow(IllegalStateException::new)
				.getMetadata();
		version = metadata.getVersion().getFriendlyString();
		CarpetServer.manageExtension(new CarpetSCTAddition());
		
		// 加载配置
        DropsConfig.loadConfig();
        
        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            DropsIntoShulkerCommand.register(dispatcher);
			ExplosionProtectionCommand.register(dispatcher);
        });
	}

	public void onGameStarted() {
		CarpetServer.settingsManager.parseSettingsClass(SCTSettings.class);
	}

	public Map<String, String> canHasTranslations(String lang) {
		return Translations.getTranslationFromResourcePath("assets/carpetsctaddition/lang/%s.json".formatted(lang));
	}
}