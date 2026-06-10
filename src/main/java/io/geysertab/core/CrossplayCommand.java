package io.geysertab.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.List;

public final class CrossplayCommand {

    private final String name;
    private String title = "";
    private SuggestionSource source = src -> List.of();
    private SelectAction onSelect = (p, v) -> {};
    private Presenter presenter;

    private CrossplayCommand(String name) {
        this.name = name;
    }

    public static CrossplayCommand literal(String name) {
        return new CrossplayCommand(name);
    }

    public CrossplayCommand title(String title) {
        this.title = title;
        return this;
    }

    public CrossplayCommand source(SuggestionSource source) {
        this.source = source;
        return this;
    }

    public CrossplayCommand onSelect(SelectAction action) {
        this.onSelect = action;
        return this;
    }

    public CrossplayCommand presenter(Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        String t = title;
        SuggestionSource s = source;
        SelectAction a = onSelect;
        Presenter p = presenter;

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name)
            .executes(ctx -> {
                if (!(ctx.getSource().getSender() instanceof Player player)) {
                    ctx.getSource().getSender().sendMessage("This command can only be used by players.");
                    return Command.SINGLE_SUCCESS;
                }
                List<String> values = s.values(ctx.getSource());
                p.present(player, t, values, a);
                return Command.SINGLE_SUCCESS;
            });

        RequiredArgumentBuilder<CommandSourceStack, String> valueArg =
            Commands.argument("value", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    List<String> vals = s.values(ctx.getSource());
                    String remaining = builder.getRemaining().toLowerCase();
                    vals.stream()
                        .filter(v -> v.startsWith(remaining))
                        .forEach(builder::suggest);
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("This command can only be used by players.");
                        return Command.SINGLE_SUCCESS;
                    }
                    String value = StringArgumentType.getString(ctx, "value");
                    a.run(player, value);
                    return Command.SINGLE_SUCCESS;
                });

        root.then(valueArg);
        return root.build();
    }
}
