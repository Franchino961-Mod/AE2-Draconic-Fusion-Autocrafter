# Changelog - AE2 Draconic Fusion Autocrafter

All notable changes to the **AE2 Draconic Fusion Autocrafter** mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.3] - 2026-06-09

### Fixed
- **Dismantling/Breaking drops**: Resolved a bug where shift-right-clicking the ME Draconic Pattern Provider with a wrench (such as the Mekanism Configurator or AE2 Certus Quartz Wrench) or breaking it normally would delete the block and drop nothing. Added the missing block loot table.

---

## [0.1.2] - 2026-05-26

### Added
- **Translations**: Added localized translations for German (`de_de`), Spanish (`es_es`), French (`fr_fr`), Portuguese (Brazil) (`pt_br`), Russian (`ru_ru`), and Chinese (Simplified) (`zh_cn`).
- **Project Configuration**: Added `.gitattributes` file to normalize line endings (LF) for code, resources, properties, markdown, and scripts.

### Changed
- **Documentation**: Expanded README with details on requirements (Java 21+), installation steps, client/server behavior, compatibility, limitations, troubleshooting, FAQ, and support channels.
- **Git Configuration**: Updated `.gitignore` to exclude database journal files, local scripting files, and whitelist Gradle wrappers.

---

## [0.1.1] - 2026-05-19

### Added
- **Documentation**: Added ME Draconic Pattern Provider pattern example image to documentation.

### Fixed
- **B011 – Items voided on craft cancellation**: Resolved critical bug where items routed to injectors were lost upon craft cancellation with insufficient injectors. The execution phase now performs a full rollback (clears injectors and catalyst from the core) and returns `false` to retain items in the AE2 network.
- **Code Quality**: Removed unnecessary `@SuppressWarnings` annotations and added explicit null-checks before `@Nonnull`-annotated API calls in `FusionRoutingService`.
- **Code Quality**: Added missing `@Nonnull` annotations on `getCloneItemStack` parameters in `DraconicPatternProviderBlock` to match block interface contracts.

---

## [0.1.0] - 2026-04-22

### Added
- **Recipes**
  - Added native **Fusion Crafting** recipe for the ME Draconic Pattern Provider.
  - Added **Shapeless Crafting** recipe to convert the Pattern Provider block into its Panel variant.
- **Documentation**
  - Expanded README with recipes and pattern setup images.
- **Assets**
  - Updated the official mod icon.

### Fixed
- **Code Quality**
  - Comprehensive comment cleanup, formatting, and logging clarity improvements across all classes (e.g. `FusionStructureScanner`, `FusionRoutingService`, `DraconicPatternProviderPart`).

---

## [0.0.4] - 2026-04-21

### Added
- **Integrations**
  - Registered BlockEntities for Jade/Waila and AE2 visual representation.
- **Features**
  - Custom name and display name methods for `DraconicPatternProviderPart` and `DraconicPatternProviderBlockEntity`.
  - Added clone item stack method to `DraconicPatternProviderBlock`.
  - Added JSON configuration for Draconic Fusion Autocrafter pattern provider.
- **Documentation**
  - Added setup diagrams and installation screenshots.

### Changed
- **Build System**
  - Refactored `build.gradle` and `gradle.properties` for better manifest attributes and resource processing.
  - Updated version placeholder handling in `neoforge.mods.toml`.

---

## [0.0.3] - 2026-04-20

### Added
- **Core Logic**
  - Implemented structure scanner logic to scan multi-block fusion setups.
  - Implemented core routing service for catalysts and ingredients.
  - Created routing result enumeration.
- **AE2 Integration**
  - Added custom ME Draconic Pattern Provider blocks and parts.
  - Added pattern provider logic and custom block entity.
  - Implemented card upgrade integration for the Fusion Routing Card.
  - Implemented AE2 fusion bus access utilities.

---

## [0.0.2] - 2026-04-20

### Added
- **Registry**
  - Registered mod blocks, items (including the Fusion Routing Card), and custom creative tabs.
- **Assets & Models**
  - Mod icon and pack metadata configurations.
  - Base blockstate files for the ME Draconic Pattern Provider.
  - Translation files for English and Italian.
  - 3D models for block, item, panel item, routing card, and parts (including active channel, on, and off states).
  - Textures for parts, blocks, and routing card items.

---

## [0.0.1] - 2026-04-20

### Added
- **Bootstrap**
  - Initial project structure setup and metadata.
  - Configured build system with Gradle build scripts, settings, properties, wrapper scripts, and batch files.
  - Initial project README.
  - Added initial `.gitignore` rules.

---

## Development Roadmap

### Phase 1 - Bootstrap ✅
- Project setup and metadata.
- Registration of the Fusion Routing Card.

### Phase 2 - Core Logic ✅
- Implementation of the Routing Service.
- Reflection-based integration with Draconic Evolution.
- Snapshot system for multi-block scanning.

### Phase 3 - AE2 Integration ✅
- Custom Pattern Provider block and part implementation.
- Upgrade hook for AE2 Export Bus.

### Phase 4 - Polish & Expansion 📋
- HUD/GUI enhancements.
- Advanced configuration options via JSON.
- Particle effects and sounds.

---

*Format: [Version] - Date*  
*Categories: Added, Changed, Fixed, Removed, Technical*
