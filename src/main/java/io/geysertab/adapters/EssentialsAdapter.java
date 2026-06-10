package io.geysertab.adapters;

import com.earth2me.essentials.Essentials;
import io.geysertab.api.CrossplayProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in adapter for EssentialsX.
 * Registers CrossplayProvider instances for /warp and /home so GeyserTab
 * picks them up automatically — Essentials owns all data and logic,
 * we only add the Bedrock UI layer.
 */
public final class EssentialsAdapter {

    public static void register(Plugin registrar) {
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (ess == null) return;

        var sm = Bukkit.getServicesManager();

        // IWarps.getList() → Collection<String> of warp names (from com.earth2me.essentials.api.IWarps)
        sm.register(CrossplayProvider.class, new CrossplayProvider() {
            @Override public String commandName()  { return "warp"; }
            @Override public String displayTitle() { return "Warps"; }
            @Override public List<String> values(Player player) {
                try {
                    return new ArrayList<>(ess.getWarps().getList());
                } catch (Exception e) { return List.of(); }
            }
            @Override public void execute(Player player, String value) {
                player.performCommand("essentials:warp " + value);
            }
        }, registrar, ServicePriority.Normal);

        // IUser.getHomes() → List<String> of home names for this player
        sm.register(CrossplayProvider.class, new CrossplayProvider() {
            @Override public String commandName()  { return "home"; }
            @Override public String displayTitle() { return "Homes"; }
            @Override public List<String> values(Player player) {
                try {
                    return ess.getUser(player).getHomes();
                } catch (Exception e) { return List.of(); }
            }
            @Override public void execute(Player player, String value) {
                player.performCommand("essentials:home " + value);
            }
        }, registrar, ServicePriority.Normal);
    }
}
