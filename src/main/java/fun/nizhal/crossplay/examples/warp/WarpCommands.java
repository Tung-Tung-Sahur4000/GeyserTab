package fun.nizhal.crossplay.examples.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.nizhal.crossplay.core.CrossplayCommand;
import fun.nizhal.crossplay.core.Presenter;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.List;

public final class WarpCommands {

    private final WarpService warpService;
    private final Presenter presenter;

    public WarpCommands(WarpService warpService, Presenter presenter) {
        this.warpService = warpService;
        this.presenter = presenter;
    }

    /** /warp [name] */
    public LiteralCommandNode<CommandSourceStack> warpNode() {
        return CrossplayCommand.literal("warp")
            .title("Warps")
            .source(src -> warpService.names())
            .onSelect((player, name) -> warpService.teleport(player, name))
            .presenter(presenter)
            .build();
    }

    /** /setwarp <name> */
    public LiteralCommandNode<CommandSourceStack> setWarpNode() {
        return Commands.literal("setwarp")
            .requires(src -> src.getSender().hasPermission("crossplay.admin"))
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("Players only.");
                        return Command.SINGLE_SUCCESS;
                    }
                    String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                    warpService.set(name, player.getLocation());
                    player.sendMessage("Warp '" + name + "' set.");
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }

    /** /delwarp <name> */
    public LiteralCommandNode<CommandSourceStack> delWarpNode() {
        return Commands.literal("delwarp")
            .requires(src -> src.getSender().hasPermission("crossplay.admin"))
            .then(Commands.argument("name", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    warpService.names().forEach(builder::suggest);
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                    if (!warpService.exists(name)) {
                        ctx.getSource().getSender().sendMessage("Warp '" + name + "' not found.");
                        return Command.SINGLE_SUCCESS;
                    }
                    warpService.delete(name);
                    ctx.getSource().getSender().sendMessage("Warp '" + name + "' deleted.");
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }
}
