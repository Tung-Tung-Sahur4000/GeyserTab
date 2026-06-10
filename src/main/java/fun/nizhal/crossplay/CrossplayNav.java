package fun.nizhal.crossplay;

import fun.nizhal.crossplay.adapters.EssentialsAdapter;
import fun.nizhal.crossplay.api.CrossplayProvider;
import fun.nizhal.crossplay.core.CrossplayCommand;
import fun.nizhal.crossplay.core.Platform;
import fun.nizhal.crossplay.core.Presenter;
import fun.nizhal.crossplay.core.presenter.AutoPresenter;
import fun.nizhal.crossplay.core.presenter.BedrockFormPresenter;
import fun.nizhal.crossplay.core.presenter.ChatPresenter;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;

public final class CrossplayNav extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Register built-in adapters for known plugins.
        // Each adapter registers CrossplayProvider instances via ServicesManager.
        // Add more adapters here as support for other plugins is added.
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
                    provider.displayTitle() + " (CrossplayNav)"
                );
                getLogger().info("Registered crossplay command: /" + provider.commandName()
                    + " from " + reg.getPlugin().getName());
            }
        });

        getLogger().info("CrossplayNav enabled. Floodgate: " + platform.hasFloodgate());
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
