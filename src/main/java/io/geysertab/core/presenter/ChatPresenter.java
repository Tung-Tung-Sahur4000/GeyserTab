package io.geysertab.core.presenter;

import io.geysertab.core.Presenter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

public final class ChatPresenter implements Presenter {

    @Override
    public void present(Player player, String title, List<String> values, BiConsumer<Player, String> onSelect) {
        if (values.isEmpty()) {
            player.sendMessage(Component.text("No " + title.toLowerCase() + " available.", NamedTextColor.YELLOW));
            return;
        }
        player.sendMessage(Component.text("--- " + title + " ---", NamedTextColor.GOLD));
        for (String value : values) {
            player.sendMessage(
                Component.text(" » ", NamedTextColor.GRAY)
                    .append(Component.text(value, NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.runCommand("/" + value))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to select: " + value))))
            );
        }
    }
}
