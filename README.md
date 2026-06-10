# CrossplayNav

A UI bridge plugin for Paper 26.1+ that gives any plugin's dynamic commands proper crossplay support — tab-complete on Java, native form popup on Bedrock (Geyser/Floodgate). CrossplayNav owns zero data and zero command logic. It only adds the UI layer.

## How it works

CrossplayNav uses Bukkit's `ServicesManager` (the same mechanism Vault uses). Any plugin registers a `CrossplayProvider`, CrossplayNav picks it up and automatically wires:
- **Java** → live tab-complete suggestions per keystroke
- **Bedrock** → native form popup with tappable buttons (requires Floodgate), or clickable chat list as fallback

```
Plugin registers CrossplayProvider
         ↓
CrossplayNav reads values() at tab-complete time
         ↓
Java player: suggestion list inline
Bedrock player: /command with no arg → form popup → tap → execute()
```

## For server admins

Drop `CrossplayNav.jar` into `plugins/`. Supported plugins are detected automatically.

| Plugin | Commands bridged | Notes |
|--------|-----------------|-------|
| EssentialsX | `/warp`, `/home` | Built-in adapter |
| Any plugin | any command | Register a `CrossplayProvider` (see below) |

For Bedrock form popup: also install **Floodgate**. Without it, CrossplayNav falls back to a clickable chat list for everyone.

## For plugin developers

Implement `CrossplayProvider` and register it. No Geyser/Floodgate dependency needed on your side.

```java
import fun.nizhal.crossplay.api.CrossplayProvider;

// In your plugin's onEnable():
getServer().getServicesManager().register(
    CrossplayProvider.class,
    new CrossplayProvider() {
        public String commandName()  { return "kit"; }
        public String displayTitle() { return "Kits"; }
        public List<String> values(Player player) {
            return kitPlugin.getKitNames(); // your data source
        }
        public void execute(Player player, String value) {
            kitPlugin.giveKit(player, value); // your logic
        }
    },
    this,
    ServicePriority.Normal
);
```

That's it. CrossplayNav handles the rest — no knowledge of Geyser or Bedrock required.

## Configuration (`config.yml`)

```yaml
fallback-ui: auto   # auto | chat
```

`auto` — Bedrock players get a form (requires Floodgate), Java players get a chat list.  
`chat` — everyone gets a chat list, no Floodgate needed.

## Requirements

- Paper 26.1+, JDK 25
- Geyser (optional — for Bedrock players to connect)
- Floodgate (optional — for native form popup on Bedrock)
- EssentialsX (optional — built-in adapter included)

## Building

```bash
mvn clean package
```

Jar: `target/CrossplayNav.jar`. GitHub Actions builds automatically on every push.
