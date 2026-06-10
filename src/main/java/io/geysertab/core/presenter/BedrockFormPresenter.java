package io.geysertab.core.presenter;

import io.geysertab.core.Presenter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * All Floodgate/Cumulus references are isolated in this class.
 * Only instantiate after confirming hasFloodgate() == true.
 */
public final class BedrockFormPresenter implements Presenter {

    private final Plugin plugin;

    public BedrockFormPresenter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void present(Player player, String title, List<String> values, BiConsumer<Player, String> onSelect) {
        if (values.isEmpty()) {
            player.sendMessage(Component.text("No " + title.toLowerCase() + " available.", NamedTextColor.YELLOW));
            return;
        }

        SimpleForm.Builder builder = SimpleForm.builder().title(title);
        for (String value : values) {
            builder.button(value);
        }

        builder.validResultHandler(response -> {
            int id = response.clickedButtonId();
            if (id < 0 || id >= values.size()) return;
            String chosen = values.get(id);
            Bukkit.getScheduler().runTask(plugin, () -> onSelect.accept(player, chosen));
        });

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());
    }
}
