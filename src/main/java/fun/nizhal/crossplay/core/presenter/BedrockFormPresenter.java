package fun.nizhal.crossplay.core.presenter;

import fun.nizhal.crossplay.core.Presenter;
import fun.nizhal.crossplay.core.SelectAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

/**
 * All Floodgate/Cumulus references are isolated in this class.
 * Only instantiate or call methods here after confirming hasFloodgate() == true.
 */
public final class BedrockFormPresenter implements Presenter {

    private final Plugin plugin;

    public BedrockFormPresenter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void present(Player player, String title, List<String> values, SelectAction action) {
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
            Bukkit.getScheduler().runTask(plugin, () -> action.run(player, chosen));
        });

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());
    }
}
