package fun.nizhal.crossplay.examples.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.nizhal.crossplay.core.CrossplayCommand;
import fun.nizhal.crossplay.core.Presenter;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public final class HomeCommands {

    private final HomeService homeService;
    private final Presenter presenter;

    public HomeCommands(HomeService homeService, Presenter presenter) {
        this.homeService = homeService;
        this.presenter = presenter;
    }

    /** /home [name] */
    public LiteralCommandNode<CommandSourceStack> homeNode() {
        return CrossplayCommand.literal("home")
            .title("Homes")
            .source(src -> {
                if (!(src.getSender() instanceof Player player)) return java.util.List.of();
                return homeService.namesFor(player.getUniqueId());
            })
            .onSelect((player, name) -> homeService.teleport(player, name))
            .presenter(presenter)
            .build();
    }

    /** /sethome <name> */
    public LiteralCommandNode<CommandSourceStack> setHomeNode() {
        return Commands.literal("sethome")
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("Players only.");
                        return Command.SINGLE_SUCCESS;
                    }
                    if (!homeService.canAdd(player.getUniqueId())) {
                        player.sendMessage("You have reached the maximum number of homes.");
                        return Command.SINGLE_SUCCESS;
                    }
                    String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                    homeService.set(player.getUniqueId(), name, player.getLocation());
                    player.sendMessage("Home '" + name + "' set.");
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }

    /** /delhome <name> */
    public LiteralCommandNode<CommandSourceStack> delHomeNode() {
        return Commands.literal("delhome")
            .then(Commands.argument("name", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    if (ctx.getSource().getSender() instanceof Player player) {
                        homeService.namesFor(player.getUniqueId()).forEach(builder::suggest);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("Players only.");
                        return Command.SINGLE_SUCCESS;
                    }
                    String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                    if (!homeService.delete(player.getUniqueId(), name)) {
                        player.sendMessage("Home '" + name + "' not found.");
                    } else {
                        player.sendMessage("Home '" + name + "' deleted.");
                    }
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }
}
