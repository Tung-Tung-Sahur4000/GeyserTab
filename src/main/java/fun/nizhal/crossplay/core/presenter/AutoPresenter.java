package fun.nizhal.crossplay.core.presenter;

import fun.nizhal.crossplay.core.Platform;
import fun.nizhal.crossplay.core.Presenter;
import fun.nizhal.crossplay.core.SelectAction;
import org.bukkit.entity.Player;

import java.util.List;

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
    public void present(Player player, String title, List<String> values, SelectAction action) {
        if (platform.isBedrock(player.getUniqueId())) {
            bedrockPresenter.present(player, title, values, action);
        } else {
            chatPresenter.present(player, title, values, action);
        }
    }
}
