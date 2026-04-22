# Changelog - AE2 Draconic Fusion Autocrafter

All notable changes to the **AE2 Draconic Fusion Autocrafter** mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.0] - April 22, 2026 (Initial Beta Release)

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

### Fixed
- **Compatibility**
  - Fixed Draconic Evolution 3.x recipe format (corrected `techLevel`, `totalEnergy`, and ingredient nesting).
  - Fixed **Jade/Waila** support: blocks now correctly display as "ME Draconic Pattern Provider" instead of "Air".
- **UI/UX**
  - Fixed GUI titles: the Pattern Provider screen now correctly displays the custom mod name instead of the default AE2 title.
  - Fixed mod version display in the NeoForge mod menu (resolved "0.0NONE" issue).
- **Code Quality**
  - Comprehensive code cleanup: removed unused imports and empty packages.
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
