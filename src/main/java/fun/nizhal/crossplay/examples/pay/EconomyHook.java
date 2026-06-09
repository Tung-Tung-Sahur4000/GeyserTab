package fun.nizhal.crossplay.examples.pay;

import org.bukkit.entity.Player;

/**
 * Stub economy hook. Replace with a Vault implementation when needed.
 * Log-only by default.
 */
public interface EconomyHook {

    EconomyHook LOG_ONLY = (from, to, amount) -> {
        from.sendMessage(String.format("(stub) You paid %s $%.2f.", to.getName(), amount));
        to.sendMessage(String.format("(stub) You received $%.2f from %s.", amount, from.getName()));
    };

    void pay(Player from, Player to, double amount);
}
