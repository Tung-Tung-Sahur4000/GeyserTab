# GeyserTab

A lightweight Paper plugin that gives Bedrock players a native form popup for commands like `/home`, `/warp`, `/kit` — no matter which plugin provides them.

## The problem

Java players get live tab-complete suggestions when typing `/home <Tab>`. Geyser builds Bedrock's command screen once at login and can't fetch dynamic suggestions per keystroke, so Bedrock players see an empty argument list and have no idea what names are available.

GeyserTab fixes this by intercepting the no-argument case (`/home` with nothing after it) and showing a tappable form instead.

## How it works

```
Bedrock player sends /home  (no argument)
        ↓
GeyserTab intercepts the command
        ↓
Asks the server: "what are the tab completions for 'home '?"
        ↓
Whatever plugin handles /home responds with the player's home names
        ↓
GeyserTab shows a form popup with those names
        ↓
Player taps a name → /home bedroom runs → original plugin teleports them
```

GeyserTab never stores any data. It owns no command logic. It purely adds a UI layer on top of whatever is already installed.

## Works with any plugin

Because GeyserTab reads values from the server's existing tab completion — not from any plugin's internal API — it works with:

- **EssentialsX** — `/home`, `/warp`
- **SimpleHome**, **DarkLorad Homes**, or any other home plugin
- **CMI**, **MyCommand**, **CommandPanels**
- Any kit plugin, shop plugin, or custom command — as long as it provides tab completions

No adapter. No config per plugin. No recompile needed when you switch plugins.

## Installation

1. Drop `GeyserTab.jar` into your `plugins/` folder
2. Also install **Geyser** (for Bedrock players to connect)
3. Optionally install **Floodgate** for the native form popup UI (without it, a clickable chat list is shown instead)
4. Start the server — `plugins/GeyserTab/config.yml` is created automatically
5. Edit `config.yml` to list the commands you want intercepted (see below)

## Configuration

```yaml
# plugins/GeyserTab/config.yml

# How to show the value list to Bedrock players.
# auto  - native form popup (requires Floodgate); falls back to chat list
# chat  - clickable chat list for everyone
fallback-ui: auto

# Commands to intercept for Bedrock players.
# When a Bedrock player sends one of these with no argument, GeyserTab
# cancels it and shows a form/list using the existing plugin's tab completions.
# Format:  commandName: "Display Title"
commands:
  home: Homes
  warp: Warps
  kit: Kits
```

Add or remove entries freely. Restart (or `/reload confirm`) to apply changes.

## For plugin developers

If your plugin's tab completions aren't accessible via the standard CommandMap (e.g. you use a custom async completion system), you can register a `CrossplayProvider` explicitly via Bukkit's `ServicesManager`. GeyserTab checks this before falling back to tab completion.

```java
import io.geysertab.api.CrossplayProvider;

// In your plugin's onEnable():
getServer().getServicesManager().register(
    CrossplayProvider.class,
    new CrossplayProvider() {
        public String commandName() { return "mycommand"; }
        public List<String> values(Player player) {
            return myPlugin.getOptionsFor(player);
        }
    },
    this,
    ServicePriority.Normal
);
```

No Geyser or Floodgate dependency needed on your side.

## Requirements

| Requirement | Notes |
|---|---|
| Paper 26.1+ | Required |
| JDK 25 | Build only |
| Geyser | Optional — needed for Bedrock players to connect |
| Floodgate | Optional — needed for native form popup; falls back to chat list |

## Building

```bash
mvn clean package
```

Output: `target/GeyserTab.jar`

GitHub Actions builds automatically on every branch push. Download the jar from the **Actions → Artifacts** section.
