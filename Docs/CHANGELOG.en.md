# Changelog - AE2 Draconic Fusion Autocrafter

All notable changes to the **AE2 Draconic Fusion Autocrafter** mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
- **Core Automation**
  - Native AE2 support for Draconic Evolution Fusion Crafting.
  - **ME Draconic Pattern Provider**: A dedicated block variant for fusion setups.
  - **ME Draconic Pattern Provider (Panel)**: A sleek panel variant for compact automation.
- **Recipes**
  - Added native **Fusion Crafting** recipe for the ME Draconic Pattern Provider.
  - Added **Shapeless Crafting** recipe to convert the Pattern Provider block into its Panel variant.
- **Intelligent Routing System**
  - **Catalyst Detection**: Automatically identifies and routes the catalyst item directly to the Fusion Core.
  - **Recipe-Aware Routing**: Dynamically matches ingredients against the active fusion recipe to ensure correct placement.
  - **Deterministic Injector Selection**: Smart distribution of items to injectors based on distance and orientation.
- **Stability & Performance**
  - **Smart Retry Logic**: Prevents AE2 routing failures by gracefully handling "Busy" or "Charging" core states.
  - **Native Capability Handling**: Works with NeoForge capabilities without shadowing or blocking original mod inventories.
  - **Async Integration**: Fully compatible with AE2's simulation-based insertion flow.
- **Assets & Models**
  - Textures for parts, routing card item, and pattern provider block.
  - Models for pattern provider block, item, panel item, routing card, and parts (including active channel, on, and off states).
  - Italian and English localization base translation files.
  - Blockstates and pack/mod metadata configuration.
- **Registry**
  - Mod blocks, items, and creative tab registration.
- **Documentation**
  - Initial README with setup instructions, screenshots, and diagrams.

### Fixed
- **Compatibility**
  - Fixed Draconic Evolution 3.x recipe format (corrected `techLevel`, `totalEnergy`, and ingredient nesting).
  - Fixed **Jade/Waila** support: blocks now correctly display as "ME Draconic Pattern Provider" instead of "Air".
- **UI/UX**
  - Fixed GUI titles: the Pattern Provider screen now correctly displays the custom mod name instead of the default AE2 title.
  - Fixed mod version display in the NeoForge mod menu (resolved "0.0NONE" issue).
- **Code Quality**
  - Comprehensive code cleanup: removed unused imports, empty packages, and refined comments for clarity across all classes.
  - Added Javadocs to all major classes and registry entries.
  - Optimized logging: moved noisy diagnostic logs to DEBUG level.

### Technical
- Platform: NeoForge 1.21.1
- Dependencies: AE2 (19.2.17+), Draconic Evolution (3.1.4.632+)

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
