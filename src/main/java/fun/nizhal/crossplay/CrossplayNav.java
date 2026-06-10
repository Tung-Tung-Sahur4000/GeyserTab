package fun.nizhal.crossplay;

import fun.nizhal.crossplay.core.Platform;
import fun.nizhal.crossplay.core.Presenter;
import fun.nizhal.crossplay.core.presenter.AutoPresenter;
import fun.nizhal.crossplay.core.presenter.BedrockFormPresenter;
import fun.nizhal.crossplay.core.presenter.ChatPresenter;
import fun.nizhal.crossplay.hooks.HookRegistry;
import fun.nizhal.crossplay.hooks.essentials.EssentialsHook;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrossplayNav extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Platform platform = new Platform();
        Presenter presenter = buildPresenter(platform);

        HookRegistry registry = new HookRegistry(getLogger());
        registry.add(new EssentialsHook());
        // Add more hooks here as new plugins are supported, e.g.:
        // registry.add(new CMIHook());

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
            registry.registerAll(event, presenter)
        );

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
