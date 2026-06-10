package io.geysertab;

import io.geysertab.adapters.EssentialsAdapter;
import io.geysertab.api.CrossplayProvider;
import io.geysertab.core.CrossplayCommand;
import io.geysertab.core.Platform;
import io.geysertab.core.Presenter;
import io.geysertab.core.presenter.AutoPresenter;
import io.geysertab.core.presenter.BedrockFormPresenter;
import io.geysertab.core.presenter.ChatPresenter;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;

public final class GeyserTab extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Register built-in adapters for known plugins.
        // Each adapter registers CrossplayProvider instances via ServicesManager.
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            EssentialsAdapter.register(this);
        }

        Platform platform = new Platform();
        Presenter presenter = buildPresenter(platform);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Collection<RegisteredServiceProvider<CrossplayProvider>> registrations =
                Bukkit.getServicesManager().getRegistrations(CrossplayProvider.class);

            if (registrations.isEmpty()) {
                getLogger().warning("No CrossplayProvider registrations found. " +
                    "Install a supported plugin (e.g. EssentialsX) or register a provider manually.");
                return;
            }

            for (RegisteredServiceProvider<CrossplayProvider> reg : registrations) {
                CrossplayProvider provider = reg.getProvider();
                event.registrar().register(
                    CrossplayCommand.literal(provider.commandName())
                        .title(provider.displayTitle())
                        .source(src -> {
                            Player player = src.getSender() instanceof Player p ? p : null;
                            return player != null ? provider.values(player) : List.of();
                        })
                        .onSelect(provider::execute)
                        .presenter(presenter)
                        .build(),
                    provider.displayTitle() + " (GeyserTab)"
                );
                getLogger().info("Registered crossplay command: /" + provider.commandName()
                    + " from " + reg.getPlugin().getName());
            }
        });

        getLogger().info("GeyserTab enabled. Floodgate: " + platform.hasFloodgate());
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
