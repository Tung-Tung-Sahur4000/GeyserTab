package fun.nizhal.crossplay.hooks;

import fun.nizhal.crossplay.core.Presenter;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class HookRegistry {

    private final List<PluginHook> hooks = new ArrayList<>();
    private final Logger logger;

    public HookRegistry(Logger logger) {
        this.logger = logger;
    }

    public void add(PluginHook hook) {
        hooks.add(hook);
    }

    public void registerAll(ReloadableRegistrarEvent<Commands> event, Presenter presenter) {
        HookContext ctx = new HookContext(event, presenter);
        for (PluginHook hook : hooks) {
            Plugin target = Bukkit.getPluginManager().getPlugin(hook.targetPlugin());
            if (target != null && target.isEnabled()) {
                hook.register(ctx);
                logger.info("Hooked into " + hook.targetPlugin());
            } else {
                logger.info("Skipping hook for " + hook.targetPlugin() + " (not installed)");
            }
        }
    }
}
