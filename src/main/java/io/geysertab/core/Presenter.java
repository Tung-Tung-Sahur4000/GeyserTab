package io.geysertab.core;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

public interface Presenter {
    void present(Player player, String title, List<String> values, BiConsumer<Player, String> onSelect);
}
