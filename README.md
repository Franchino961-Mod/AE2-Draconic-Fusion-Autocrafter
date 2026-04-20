# AE2 Draconic Fusion Autocrafter

NeoForge addon for Minecraft 1.21.1 that automates Draconic Evolution Fusion Crafting through AE2 core-only connection.

## Current status
- Project bootstrap completed.
- Base mod entrypoint and metadata created.
- Compatibility officially locked to:
  - AE2 from 19.2.0+
  - Draconic Evolution from 3.1.4.630+
- Dependency baselines configured:
  - NeoForge 21.1.223
  - AE2 19.2.17
  - Draconic Evolution 1.21.1-3.1.4.632
- Build status:
  - `./gradlew build -x test` passes.
- Phase 1 completed:
  - Fusion Routing Card item registered.
- Phase 2 started:
  - AE2 upgrade hook registers Fusion Routing Card for Export Bus via `Upgrades.add`.
- New provider variants added:
  - `ME Draconic Pattern Provider` item variant delegates placement/use to AE2 `pattern_provider`.
  - `ME Draconic Pattern Provider (Panel)` item variant delegates placement/use to AE2 `cable_pattern_provider`.
  - Temporary models/textures copied from AE2 Pattern Provider assets (same development baseline visuals).
- Fusion Core routing bridge added:
  - exposes an item-handler capability on Draconic `crafting_core`.
  - incoming inserts are routed to catalyst/core and injectors via recipe-aware routing.
  - works with simulation + execution flow used by AE2 insertion logic.
- AE2 detection helper added:
  - bus upgrade check via `IUpgradeableObject`.
  - adjacent Fusion Core resolution via world scan.
- AE2 export strategy bridge added:
  - temporary disabled at startup because AE2 already owns the global item export strategy key (`ae2:i`) and duplicate registration crashes load.
  - current runtime integration keeps the Fusion Routing Card registration only.
- Draconic validation path moved to reflection-based discovery so the code stays buildable without Brandon's Core on the compile classpath.
- Injector assignment mode: deterministic ordering by distance from core, then direction tie-break.
- Fusion Routing Card texture now present at `src/main/resources/assets/ae2_draconic_fusion_autocrafter/textures/item/fusion_routing_card.png`.
- Routing now supports recipe-aware filtering:
  - if a fusion recipe is active on the core, only matching catalyst/ingredients are assigned.
  - deterministic injector ordering remains the fallback/base behavior.
- Recipe-aware matching now prefers more specific alternatives when multiple ingredients can match.
- Prefilled injector guard added: incompatible preloaded injector stacks now stop routing to avoid corrupting recipe setup.

## Next steps
1. Replace alias delegation with fully custom provider block/part implementations.
2. Harden edge-case handling for mixed external automation on the same Fusion Core.
3. Add dedicated recipes and progression for the two new provider variants.

## Asset paths
- Item icon/texture: `src/main/resources/assets/ae2_draconic_fusion_autocrafter/textures/item/fusion_routing_card.png`
- Item model: `src/main/resources/assets/ae2_draconic_fusion_autocrafter/models/item/fusion_routing_card.json`
- Translation keys: `src/main/resources/assets/ae2_draconic_fusion_autocrafter/lang/en_us.json` and `it_it.json`

## Useful links
### Draconic Evolution
- Source code: https://github.com/Draconic-Inc/Draconic-Evolution
- FTB wiki (community): https://ftbwiki.org/Draconic_Evolution
- Community guide: https://www.minecraft-guides.com/mod/draconic-evolution/

### Applied Energistics 2
- Source code: https://github.com/AppliedEnergistics/Applied-Energistics-2
- Official guide: https://guide.appliedenergistics.org/

## Dependency notes
- AE2 API artifact is published on Maven Central as org.appliedenergistics:appliedenergistics2.
- Draconic Evolution artifacts are published through Brandon/Covers nexus indexes.
