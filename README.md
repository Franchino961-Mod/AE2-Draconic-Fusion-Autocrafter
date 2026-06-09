# ⚡ AE2 Draconic Fusion Autocrafter
**Seamlessly Automate Fusion Crafting with the Power of AE2!**

[![Download on CurseForge](https://img.shields.io/badge/Download_on-CurseForge-orange?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/ae2-draconic-fusion-autocrafter)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)]()
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.223-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![it](https://img.shields.io/badge/lang-it-green.svg)](Docs/README.it.md)

> 📝 **Changelog**: See [CHANGELOG.en.md](Docs/CHANGELOG.en.md) for version history.

---

## 📖 Overview

**AE2 Draconic Fusion Autocrafter** is a specialized addon designed to bridge the gap between **Applied Energistics 2** and **Draconic Evolution**. It introduces a native way to automate the complex Multi-Block Fusion Crafting process without the need for messy redstone, external timers, or complex subnetworks.

By providing custom Pattern Providers and intelligent routing logic, this mod ensures that catalysts and ingredients always find their correct place in your fusion structure, making top-tier Draconic crafting as simple as clicking "Craft" in your ME Terminal.

---

## 🌟 Why Use AE2 Draconic Fusion Autocrafter?

- **Redstone-Free Automation**: No more complex redstone timers or clock sub-networks to trigger fusion.
- **Intelligent Routing**: Catalysts automatically go to the core, and ingredients are distributed evenly to injectors.
- **Safe Crafting**: Avoids ME system lockups or item loss by waiting for the crafting core to be free.

---

## ✨ Main Features

- 🧬 **Native AE2 Integration**: New blocks and parts that act like standard Pattern Providers but speak "Fusion".
- 🎯 **Intelligent Routing**: Automatically distinguishes between the Fusion Catalyst (Core) and Ingredients (Injectors).
- 🔄 **Recipe-Aware Logic**: Dynamically assigns items to injectors based on the active fusion recipe to prevent conflicts.
- ⚡ **Retry System**: Handles "Core Busy" states gracefully, preventing AE2 from timing out during long crafting operations.
- 🛠️ **No Shadowing**: Does not override Draconic Evolution's inventory logic, ensuring compatibility with all extraction methods (Import Bus, pipes, etc.).
- 📦 **Compact Design**: Use the Panel variant to keep your fusion setup sleek and hidden behind cables.

---

## 🚀 Quick Start

### Setting Up the Automation

1. **Craft** an `ME Draconic Pattern Provider` (Block or Panel). ![Crafting Recipe](Docs/assets/image/ME_Draconic_Pattern_Provider-Recipe_Small.png)
2. **Place** it adjacent to a **Draconic Fusion Crafting Core**.
3. **Configure** your Fusion recipes in AE2 Patterns normally (Inputs on one side, Output on the other).
4. **Insert** the patterns into the Draconic Pattern Provider.
5. **Start Crafting!** The mod will automatically:
    - Send the catalyst to the Core.
    - Deterministically distribute ingredients to the valid injectors in range.
    - Wait for the Core to be free before pushing the next recipe.

![ME Draconic Pattern Provider setup](Docs/assets/image/ME_Draconic_Pattern_Provider.png)

---

## 📦 Requirements

- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.223 or higher
- **Applied Energistics 2**: 19.2.17 or higher
- **Draconic Evolution**: 3.1.4.632 or higher
- **Brandon's Core**: Required dependency for Draconic Evolution
- **Java**: 21 or higher

---

## 📥 Installation

1. Make sure you have installed NeoForge and all the required dependencies.
2. Download the `ae2-draconic-fusion-autocrafter` `.jar` file.
3. Place the file in your Minecraft installation's `mods` folder.
4. Launch the game!

---

## 🖥️ Client/Server Behavior

- **Server**: Required. Handles AE2 pattern injection, recipe matching, and item routing.
- **Client**: Required. Renders blocks, handles GUI interactions, and displays recipe integration.

---

## 🤝 Compatibility & Modpack Notes

- **Applied Energistics 2**: Deep integration.
- **Draconic Evolution**: Full multi-block support.
- **JEI/REI/EMI**: Integration for patterns.
- **Sophisticated Storage/Backpacks**: Works with external inventories.
- **Modpacks**: You are free to distribute and include this mod in any modpack.

---

## ⚠️ Known Limitations

- Requires all injectors to be properly powered and linked to the core.
- Automation depends on the core having a clear line of sight to the injectors as per Draconic Evolution mechanics.

---

## 🛠️ Troubleshooting

### Crafting is stuck / items not routing
- Ensure all ingredients are defined in the AE2 pattern and the core is active.
- Check if another recipe is currently running in the same fusion setup.

### System locks up
- Ensure the Pattern Provider is placed directly adjacent to the Fusion Crafting Core.

---

## ❓ FAQ

### Q: Does this support all Draconic Evolution tiers?
A: Yes, from Basic to Chaotic tiers.

### Q: Can I use it with other automation pipes?
A: Yes, it is fully compatible with standard items extraction methods (e.g. Import Bus, pipes).

---

## 💬 Support & Feedback

Report bugs or suggest features on our [Issue Tracker](../../issues). Please include your mod version and any relevant logs.

---

## 📄 License

This mod is licensed under the [MIT License](LICENSE). You are free to include it in any modpack!

## 👤 Author

**Franchino961** — [GitHub](https://github.com/Franchino961-Mod)

---

## 🔗 Links

- [Draconic Evolution](https://www.curseforge.com/minecraft/mc-mods/draconic-evolution)
- [Applied Energistics 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2)
- [NeoForge](https://neoforged.net/)

---

**Made with ❤️ for the AE2 & Draconic Evolution community!**
