package fun.nizhal.crossplay.hooks.essentials;

import com.earth2me.essentials.Essentials;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.nizhal.crossplay.hooks.HookContext;
import fun.nizhal.crossplay.hooks.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Hooks into EssentialsX to provide crossplay UI for /warp and /home.
 * All data lives in Essentials — we only read the list and dispatch the command.
 */
public final class EssentialsHook implements PluginHook {

    @Override
    public String targetPlugin() {
        return "Essentials";
    }

    @Override
    public void register(HookContext ctx) {
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        // /warp [name] — Tier 2, global warp list from Essentials
        ctx.registrar().register(
            ctx.command("warp")
                .title("Warps")
                .source(src -> {
                    try {
                        return new ArrayList<>(ess.getWarps().getWarpList());
                    } catch (Exception e) {
                        return List.of();
                    }
                })
                .onSelect((player, name) -> player.performCommand("essentials:warp " + name))
                .build(),
            "Teleport to a warp (crossplay UI)"
        );

        // /home [name] — Tier 2, per-player home list from Essentials
        ctx.registrar().register(
            ctx.command("home")
                .title("Homes")
                .source(src -> {
                    if (!(src.getSender() instanceof Player player)) return List.of();
                    try {
                        return ess.getUser(player).getHomes();
                    } catch (Exception e) {
                        return List.of();
                    }
                })
                .onSelect((player, name) -> player.performCommand("essentials:home " + name))
                .build(),
            "Go to your home (crossplay UI)"
        );

        // /pay <player> <amount> — dispatch to Essentials /pay directly
        ctx.registrar().register(
            com.mojang.brigadier.builder.LiteralArgumentBuilder
                .<io.papermc.paper.command.brigadier.CommandSourceStack>literal("pay")
                .then(io.papermc.paper.command.brigadier.Commands.argument("target", StringArgumentType.word())
                    .suggests((c, b) -> {
                        String rem = b.getRemaining().toLowerCase();
                        Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(n -> n.toLowerCase().startsWith(rem))
                            .forEach(b::suggest);
                        return b.buildFuture();
                    })
                    .then(io.papermc.paper.command.brigadier.Commands.argument("amount", StringArgumentType.word())
                        .executes(c -> {
                            if (!(c.getSource().getSender() instanceof Player sender)) return Command.SINGLE_SUCCESS;
                            String target = StringArgumentType.getString(c, "target");
                            String amount = StringArgumentType.getString(c, "amount");
                            sender.performCommand("essentials:pay " + target + " " + amount);
                            return Command.SINGLE_SUCCESS;
                        })))
                .build(),
            "Pay a player (crossplay UI)"
        );
    }
}
