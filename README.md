# UniversalEnchantments - Minecraft Plugin

UniversalEnchantments is a Minecraft plugin that allows you to apply any enchantment to any item, with configurable costs and cooldowns, supporting multiple server versions. This plugin gives you complete control over enchantments, enabling both natural and forced enchantment application with intelligent logic for compatibility checking. It includes commands for enchanting specific items, applying all compatible enchantments at once, listing enchantment information, removing enchantments, and reloading configuration. The plugin is designed for Paper 1.21+ servers and includes features like XP level costs, per-player cooldowns, and permission-based access control.

## Install

1. Download the UniversalEnchantments.jar file from the release section.
2. Place the JAR file into your server's `plugins` folder.
3. Restart your server or run `reload confirm` in the console.
4. Verify the plugin loaded by checking the server console for the message "UniversalEnchant enabled."

## Use

### Commands

- `/enchantall [level]` - Enchants your held item with every naturally compatible enchantment at the specified level. Add `force` as a second argument to apply ALL enchantments, even incompatible ones. Example: `/enchantall 3` or `/enchantall 1 force`.
- `/enchant <enchantment> [level]` - Applies a specific enchantment to your held item at the specified level. Use the enchantment name like `sharpness` or `minecraft:sharpness`. Example: `/enchant sharpness 5`.
- `/enchantlist` - Shows a list of all available enchantments and whether they naturally apply to your held item.
- `/removeenchant` - Removes all enchantments from your held item.
- `/enchantreload` - Reloads the plugin configuration without restarting the server.
- `/enchant cost` or `/enchant info` - Displays current cost and cooldown settings.

### Configuration

The plugin comes with a `config.yml` file where you can adjust:

- `cost.enabled` - Enable/disable XP cost for enchanting (default: true).
- `cost.xp-levels-per-enchant` - Number of XP levels deducted per enchantment applied (default: 5).
- `cooldown.enabled` - Enable/disable cooldowns (default: true).
- `cooldown.seconds-single` - Cooldown in seconds between individual enchant applications (default: 1).
- `cooldown.seconds-all` - Cooldown in seconds between `/enchantall` uses (default: 5).

### Permissions

- `universalenchant.enchantall` - Allows use of `/enchantall` (default: op).
- `universalenchant.enchant` - Allows use of `/enchant` (default: op).
- `universalenchant.list` - Allows use of `/enchantlist` (default: op).
- `universalenchant.remove` - Allows use of `/removeenchant` (default: op).
- `universalenchant.reload` - Allows use of `/enchantreload` (default: op).

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/51e303d9-c2cf-4428-9820-6826daff6ba1?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=51e303d9-c2cf-4428-9820-6826daff6ba1
