package fun.nizhal.crossplay.hooks;

import fun.nizhal.crossplay.core.CrossplayCommand;
import fun.nizhal.crossplay.core.Presenter;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;

/**
 * Passed to each PluginHook so it can register Brigadier nodes without
 * depending directly on the lifecycle registrar.
 */
public final class HookContext {

    private final ReloadableRegistrarEvent<Commands> event;
    private final Presenter presenter;

    public HookContext(ReloadableRegistrarEvent<Commands> event, Presenter presenter) {
        this.event = event;
        this.presenter = presenter;
    }

    public Commands registrar() {
        return event.registrar();
    }

    public Presenter presenter() {
        return presenter;
    }

    public CrossplayCommand command(String name) {
        return CrossplayCommand.literal(name).presenter(presenter);
    }
}
