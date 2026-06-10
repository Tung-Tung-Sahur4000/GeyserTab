package fun.nizhal.crossplay.hooks;

/**
 * A hook registers crossplay UI wrappers for commands provided by an external plugin.
 * Implementations read data from the target plugin's API and dispatch back to it —
 * CrossplayNav never stores warps, homes, or economy data itself.
 */
public interface PluginHook {
    /** The Bukkit plugin name this hook targets, e.g. "Essentials". */
    String targetPlugin();

    /** Called once after the target plugin is confirmed present. Register commands here. */
    void register(HookContext ctx);
}
