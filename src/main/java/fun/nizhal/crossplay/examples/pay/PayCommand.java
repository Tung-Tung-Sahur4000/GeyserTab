package fun.nizhal.crossplay.examples.pay;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PayCommand {

    private final EconomyHook economy;

    public PayCommand(EconomyHook economy) {
        this.economy = economy;
    }

    /** /pay <player> <amount> — suggests online players on Java; cross-platform compatible. */
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("pay")
            .then(Commands.argument("target", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase();
                    Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(remaining))
                        .forEach(builder::suggest);
                    return builder.buildFuture();
                })
                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                    .executes(ctx -> {
                        if (!(ctx.getSource().getSender() instanceof Player sender)) {
                            ctx.getSource().getSender().sendMessage("Players only.");
                            return Command.SINGLE_SUCCESS;
                        }
                        String targetName = StringArgumentType.getString(ctx, "target");
                        Player target = Bukkit.getPlayerExact(targetName);
                        if (target == null) {
                            sender.sendMessage("Player '" + targetName + "' is not online.");
                            return Command.SINGLE_SUCCESS;
                        }
                        if (target.equals(sender)) {
                            sender.sendMessage("You cannot pay yourself.");
                            return Command.SINGLE_SUCCESS;
                        }
                        double amount = DoubleArgumentType.getDouble(ctx, "amount");
                        economy.pay(sender, target, amount);
                        return Command.SINGLE_SUCCESS;
                    })))
            .build();
    }
}
