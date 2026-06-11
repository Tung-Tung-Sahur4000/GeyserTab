package io.geysertab;

import io.geysertab.core.Platform;
import io.geysertab.core.Presenter;
import io.geysertab.core.presenter.AutoPresenter;
import io.geysertab.core.presenter.BedrockFormPresenter;
import io.geysertab.core.presenter.ChatPresenter;
import io.geysertab.listener.CommandInterceptor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class GeyserTab extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Platform platform = new Platform();
        Presenter presenter = buildPresenter(platform);

        Map<String, String> commands = loadCommands();
        if (commands.isEmpty()) {
            getLogger().warning("No commands configured in config.yml — nothing to intercept.");
        } else {
            getLogger().info("Intercepting commands for Bedrock players: " + commands.keySet());
        }

        getServer().getPluginManager().registerEvents(
            new CommandInterceptor(platform, presenter, commands), this);

        getLogger().info("GeyserTab enabled. Floodgate: " + platform.hasFloodgate());
    }

    /** Reads the commands section from config.yml: commandName -> displayTitle */
    private Map<String, String> loadCommands() {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection section = getConfig().getConfigurationSection("commands");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                map.put(key.toLowerCase(), section.getString(key, key));
            }
        }
        return map;
    }

    private Presenter buildPresenter(Platform platform) {
        String mode = getConfig().getString("fallback-ui", "auto");
        return switch (mode.toLowerCase()) {
            case "chat" -> new ChatPresenter();
            default -> platform.hasFloodgate()
                ? new AutoPresenter(platform, new BedrockFormPresenter(this))
                : new ChatPresenter();
        };
    }
}
