package fun.nizhal.crossplay.core;

import org.bukkit.entity.Player;

import java.util.List;

public interface Presenter {
    void present(Player player, String title, List<String> values, SelectAction action);
}
