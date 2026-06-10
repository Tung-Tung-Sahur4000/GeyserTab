package io.geysertab.core;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface SelectAction {
    void run(Player player, String value);
}
