package fun.nizhal.crossplay.examples.pay;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

public final class PayCommand {

    private final EconomyHook economy;

    public PayCommand(EconomyHook economy) {
        this.economy = economy;
    }

    /** /pay <player> <amount> — Tier 1: native player selector (works on Bedrock natively). */
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("pay")
            .then(Commands.argument("target", ArgumentTypes.player())
                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                    .executes(ctx -> {
                        if (!(ctx.getSource().getSender() instanceof Player sender)) {
                            ctx.getSource().getSender().sendMessage("Players only.");
                            return Command.SINGLE_SUCCESS;
                        }
                        Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                            .resolve(ctx.getSource()).getFirst();
                        double amount = DoubleArgumentType.getDouble(ctx, "amount");
                        if (target.equals(sender)) {
                            sender.sendMessage("You cannot pay yourself.");
                            return Command.SINGLE_SUCCESS;
                        }
                        economy.pay(sender, target, amount);
                        return Command.SINGLE_SUCCESS;
                    })))
            .build();
    }
}
