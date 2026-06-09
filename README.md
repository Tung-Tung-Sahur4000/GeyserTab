# CrossplayNav

A reusable command suggestion framework for **Paper 26.1+** servers running **Geyser/Floodgate**, ensuring dynamic tab-complete arguments work correctly for both Java and Bedrock players.

## The problem

Java players get live tab-complete suggestions per keystroke (Brigadier `ask_server`). Geyser builds the Bedrock command screen once at join, so dynamic argument lists show empty on Bedrock. CrossplayNav solves this with a two-tier system.

## Two tiers

| Tier | Argument type | Example | Bedrock UX |
|------|--------------|---------|------------|
| 1 | Vanilla player selector | `/pay <player>` | Native tappable player list |
| 2 | Arbitrary dynamic strings | `/warp <name>`, `/home <name>` | Form popup (Floodgate) or chat list |

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/warp [name]` | — | Teleport to a warp. No arg opens picker. |
| `/setwarp <name>` | `crossplay.admin` | Create/overwrite a warp at your location. |
| `/delwarp <name>` | `crossplay.admin` | Delete a warp. |
| `/home [name]` | — | Teleport to your home. No arg opens picker. |
| `/sethome <name>` | — | Set a home at your location. |
| `/delhome <name>` | — | Delete a home. |
| `/pay <player> <amount>` | — | Pay another player (stub economy). |

## Configuration (`config.yml`)

```yaml
fallback-ui: auto   # auto | chat | form
homes:
  max-homes: 5      # -1 = unlimited
```

## Building

Requires JDK 25 and Maven 3.

```bash
mvn clean package
```

The jar is written to `target/CrossplayNav.jar`. A GitHub Actions workflow builds it automatically on push to `main`.

## Architecture

```
core/
  SuggestionSource   – provides the live value list (per sender)
  SelectAction       – called (on main thread) when a value is chosen
  Presenter          – how the list is shown with no argument given
  CrossplayCommand   – fluent builder wiring source + action + presenter
  Platform           – Floodgate-guarded Bedrock detection
  presenter/
    ChatPresenter          – clickable chat list (no Floodgate needed)
    BedrockFormPresenter   – Cumulus SimpleForm (all Floodgate code here)
    AutoPresenter          – routes by platform
examples/
  warp/   WarpService + WarpCommands
  home/   HomeService + HomeCommands
  pay/    EconomyHook (stub) + PayCommand (Tier 1)
```

## Extending

```java
CrossplayCommand.literal("kit")
    .title("Kits")
    .source(src -> kitService.names())
    .onSelect((player, name) -> kitService.give(player, name))
    .presenter(autoPresenter)
    .build();
```

## Requirements

- Paper 26.1+, JDK 25
- Geyser (optional, for Bedrock players)
- Floodgate (optional, for native form UI on Bedrock)
