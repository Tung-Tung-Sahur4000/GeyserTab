package io.geysertab.core;

import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

@FunctionalInterface
public interface SuggestionSource {
    List<String> values(CommandSourceStack source);
}
