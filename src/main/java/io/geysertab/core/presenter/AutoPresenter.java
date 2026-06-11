package io.geysertab.core.presenter;

import io.geysertab.core.Platform;
import io.geysertab.core.Presenter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Routes to BedrockFormPresenter for Bedrock players, ChatPresenter for Java.
 */
public final class AutoPresenter implements Presenter {

    private final Platform platform;
    private final BedrockFormPresenter bedrockPresenter;
    private final ChatPresenter chatPresenter;

    public AutoPresenter(Platform platform, BedrockFormPresenter bedrockPresenter) {
        this.platform = platform;
        this.bedrockPresenter = bedrockPresenter;
        this.chatPresenter = new ChatPresenter();
    }

    @Override
    public void present(Player player, String title, List<String> values, BiConsumer<Player, String> onSelect) {
        if (platform.isBedrock(player.getUniqueId())) {
            bedrockPresenter.present(player, title, values, onSelect);
        } else {
            chatPresenter.present(player, title, values, onSelect);
        }
    }
}
