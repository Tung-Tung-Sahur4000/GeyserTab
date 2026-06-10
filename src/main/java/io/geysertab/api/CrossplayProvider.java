package io.geysertab.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Optional explicit integration API.
 *
 * Register an implementation via Bukkit's ServicesManager if you want to provide
 * the value list programmatically rather than relying on tab completion.
 * GeyserTab checks this first before falling back to tab completion.
 *
 * Example (in your plugin's onEnable):
 *
 *   getServer().getServicesManager().register(
 *       CrossplayProvider.class,
 *       new MyKitProvider(),
 *       this,
 *       ServicePriority.Normal
 *   );
 */
public interface CrossplayProvider {

    /** Command name without slash, lowercase. Must match an entry in GeyserTab's config.yml. */
    String commandName();

    /** Returns the list of valid values for this player at call time. */
    List<String> values(Player player);
}
