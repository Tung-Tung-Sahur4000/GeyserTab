package fun.nizhal.crossplay.core.presenter;

import fun.nizhal.crossplay.core.Presenter;
import fun.nizhal.crossplay.core.SelectAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public final class ChatPresenter implements Presenter {

    @Override
    public void present(Player player, String title, List<String> values, SelectAction action) {
        if (values.isEmpty()) {
            player.sendMessage(Component.text("No " + title.toLowerCase() + " available.", NamedTextColor.YELLOW));
            return;
        }

        player.sendMessage(Component.text("--- " + title + " ---", NamedTextColor.GOLD));
        for (String value : values) {
            Component line = Component.text(" » ", NamedTextColor.GRAY)
                .append(Component.text(value, NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/" + value))
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                        Component.text("Click to select: " + value))));
            player.sendMessage(line);
        }
    }
}
