package io.geysertab.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Register an implementation of this interface via Bukkit's ServicesManager to give
 * any command crossplay tab-complete + Bedrock form UI — no Geyser or Floodgate
 * knowledge required on your side.
 *
 * Example registration (in your plugin's onEnable):
 *
 *   getServer().getServicesManager().register(
 *       CrossplayProvider.class,
 *       new MyKitProvider(),
 *       this,
 *       ServicePriority.Normal
 *   );
 *
 * GeyserTab will automatically build a Brigadier command for it and show the
 * right UI (form on Bedrock, clickable chat list on Java) when the player runs
 * the command with no argument.
 */
public interface CrossplayProvider {

    /** Command name without a slash, lowercase. E.g. "warp", "home", "kit". */
    String commandName();

    /** Title shown in the form header / chat list. E.g. "Warps", "Homes", "Kits". */
    String displayTitle();

    /**
     * Returns the list of valid values for this player.
     * Called on each tab-complete, so keep it fast.
     */
    List<String> values(Player player);

    /**
     * Called on the main thread when the player selects a value —
     * whether by tab, typing, or tapping a Bedrock form button.
     */
    void execute(Player player, String value);
}
