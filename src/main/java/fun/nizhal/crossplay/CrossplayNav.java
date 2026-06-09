package fun.nizhal.crossplay;

import fun.nizhal.crossplay.core.Platform;
import fun.nizhal.crossplay.core.Presenter;
import fun.nizhal.crossplay.core.presenter.AutoPresenter;
import fun.nizhal.crossplay.core.presenter.BedrockFormPresenter;
import fun.nizhal.crossplay.core.presenter.ChatPresenter;
import fun.nizhal.crossplay.examples.home.HomeCommands;
import fun.nizhal.crossplay.examples.home.HomeService;
import fun.nizhal.crossplay.examples.pay.EconomyHook;
import fun.nizhal.crossplay.examples.pay.PayCommand;
import fun.nizhal.crossplay.examples.warp.WarpCommands;
import fun.nizhal.crossplay.examples.warp.WarpService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrossplayNav extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Platform platform = new Platform();
        Presenter presenter = buildPresenter(platform);

        WarpService warpService = new WarpService(this);
        HomeService homeService = new HomeService(this);
        EconomyHook economy = EconomyHook.LOG_ONLY;

        WarpCommands warpCmds = new WarpCommands(warpService, presenter);
        HomeCommands homeCmds = new HomeCommands(homeService, presenter);
        PayCommand payCmds = new PayCommand(economy);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var registrar = event.registrar();
            registrar.register(warpCmds.warpNode(), "Teleport to a warp point");
            registrar.register(warpCmds.setWarpNode(), "Create or overwrite a warp point");
            registrar.register(warpCmds.delWarpNode(), "Delete a warp point");
            registrar.register(homeCmds.homeNode(), "Teleport to your home");
            registrar.register(homeCmds.setHomeNode(), "Set your home to your current location");
            registrar.register(homeCmds.delHomeNode(), "Delete one of your homes");
            registrar.register(payCmds.node(), "Pay another online player");
        });

        getLogger().info("CrossplayNav enabled. Floodgate: " + platform.hasFloodgate());
    }

    private Presenter buildPresenter(Platform platform) {
        String mode = getConfig().getString("fallback-ui", "auto");
        return switch (mode.toLowerCase()) {
            case "form" -> platform.hasFloodgate()
                ? new AutoPresenter(platform, new BedrockFormPresenter(this))
                : new ChatPresenter();
            case "chat" -> new ChatPresenter();
            default -> platform.hasFloodgate()
                ? new AutoPresenter(platform, new BedrockFormPresenter(this))
                : new ChatPresenter();
        };
    }
}
