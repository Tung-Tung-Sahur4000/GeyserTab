package fun.nizhal.crossplay.core;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

@FunctionalInterface
public interface SuggestionSource {
    List<String> values(CommandSourceStack source);
}
