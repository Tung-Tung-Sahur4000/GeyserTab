package io.geysertab.listener;

import io.geysertab.api.CrossplayProvider;
import io.geysertab.core.Platform;
import io.geysertab.core.Presenter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Intercepts commands sent by Bedrock players with no argument and shows a
 * form/chat UI populated from whatever plugin currently handles that command.
 *
 * No knowledge of the target plugin is required — values come from the
 * existing plugin's tab completion, so this works with EssentialsX,
 * SimpleHome, DarkLorad, CMI, or any other plugin.
 */
public final class CommandInterceptor implements Listener {

    private final Platform platform;
    private final Presenter presenter;
    /** commandName (lowercase) → display title, from config.yml */
    private final Map<String, String> commands;

    public CommandInterceptor(Platform platform, Presenter presenter, Map<String, String> commands) {
        this.platform = platform;
        this.presenter = presenter;
        this.commands = commands;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // Only intercept Bedrock players — Java players already get tab-complete from the original plugin
        if (!platform.isBedrock(player.getUniqueId())) return;

        String message = event.getMessage();
        if (!message.startsWith("/")) return;

        String[] parts = message.substring(1).split("\\s+", 2);
        String cmdName = parts[0].toLowerCase();

        String title = commands.get(cmdName);
        if (title == null) return;

        // Only intercept the no-arg case — if they typed an arg, pass through to original plugin
        if (parts.length > 1 && !parts[1].isBlank()) return;

        event.setCancelled(true);

        List<String> values = resolveValues(player, cmdName);
        presenter.present(player, title, values,
            (p, value) -> p.performCommand(cmdName + " " + value));
    }

    private List<String> resolveValues(Player player, String cmdName) {
        // 1. Check if a CrossplayProvider is explicitly registered for this command
        //    (allows plugins to provide their own list instead of relying on tab complete)
        for (RegisteredServiceProvider<CrossplayProvider> reg :
                Bukkit.getServicesManager().getRegistrations(CrossplayProvider.class)) {
            if (reg.getProvider().commandName().equalsIgnoreCase(cmdName)) {
                return reg.getProvider().values(player);
            }
        }

        // 2. Fall back to the original plugin's tab completion — works for any plugin
        return tabCompleteValues(player, cmdName);
    }

    /**
     * Queries the server's CommandMap for tab completions of "<cmdName> <partial>".
     * This reuses whatever the installed plugin already provides for tab-complete,
     * so no plugin-specific API is needed.
     */
    private List<String> tabCompleteValues(Player player, String cmdName) {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap map = (CommandMap) field.get(Bukkit.getServer());
            List<String> completions = map.tabComplete(player, cmdName + " ");
            return completions != null ? completions : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
