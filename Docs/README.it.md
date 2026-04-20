# ⚡ AE2 Draconic Fusion Autocrafter
**Automatizza il Fusion Crafting di Draconic Evolution con la potenza di AE2!**

[![Scarica su CurseForge](https://img.shields.io/badge/Scarica_su-CurseForge-orange?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/ae2-draconic-fusion-autocrafter)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)]()
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.223-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](../README.md)
[![it](https://img.shields.io/badge/lang-it-green.svg)](README.it.md)

> 📝 **Changelog**: Consulta [CHANGELOG.it.md](CHANGELOG.it.md) per la cronologia delle versioni.

---

## 📖 Panoramica

**AE2 Draconic Fusion Autocrafter** è un addon specializzato progettato per unire **Applied Energistics 2** e **Draconic Evolution**. Introduce un modo nativo per automatizzare il complesso processo di Fusion Crafting senza la necessità di circuiti di redstone disordinati, timer esterni o sottoreti ME complicate.

Grazie a Pattern Provider personalizzati e a una logica di routing intelligente, questa mod assicura che catalizzatori e ingredienti trovino sempre il loro posto corretto nella struttura di fusione, rendendo il crafting Draconic di alto livello semplice come un click nel tuo Terminale ME.

---

## ✨ Caratteristiche Principali

- 🧬 **Integrazione Nativa AE2**: Nuovi blocchi e parti che agiscono come i normali Pattern Provider ma ottimizzati per la Fusione.
- 🎯 **Routing Intelligente**: Distingue automaticamente tra il Catalizzatore (Core) e gli Ingredienti (Injector).
- 🔄 **Logica "Recipe-Aware"**: Assegna dinamicamente gli oggetti agli injector in base alla ricetta attiva per prevenire conflitti.
- ⚡ **Sistema di Retry**: Gestisce gli stati di "Core Occupato", evitando il timeout di AE2 durante le operazioni di crafting più lunghe.
- 🛠️ **Nessun Override Invasivo**: Non sovrascrive la logica degli inventari di Draconic Evolution, garantendo compatibilità con tutti i metodi di estrazione (Import Bus, tubi, ecc.).
- 📦 **Design Compatto**: Usa la variante Panel (Pannello) per mantenere il tuo setup di fusione elegante e nascosto dietro i cavi.

---

## 🎮 Come Funziona

### Configurazione dell'Automazione

1. **Crea** un `ME Draconic Pattern Provider` (Blocco o Pannello).
2. **Posizionalo** adiacente a un **Draconic Fusion Crafting Core**.
3. **Configura** le tue ricette di Fusione nei Pattern AE2 normalmente (Input da un lato, Output dall'altro).
4. **Inserisci** i pattern nel Draconic Pattern Provider.
5. **Inizia a Craftare!** La mod farà automaticamente quanto segue:
    - Invierà il catalizzatore al Core.
    - Distribuirà gli ingredienti in modo deterministico agli injector validi nel raggio d'azione.
    - Aspetterà che il Core sia libero prima di inviare la ricetta successiva.

---

## 📦 Installazione

### Requisiti
- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.223 o superiore
- **Applied Energistics 2**: 19.2.17 o superiore
- **Draconic Evolution**: 3.1.4.632 o superiore
- **Brandon's Core**: Dipendenza richiesta per Draconic Evolution

---

## 🤝 Compatibilità

### Testato Con
- ✅ **Applied Energistics 2**: Integrazione profonda
- ✅ **Draconic Evolution**: Supporto completo multi-block
- ✅ **JEI/REI/EMI**: Supporto per la visualizzazione delle ricette
- ✅ **Sophisticated Storage/Backpacks**: Funziona con inventari esterni

---

## 📄 Licenza

Questa mod è rilasciata sotto licenza [MIT](LICENSE). Sei libero di includerla nei tuoi modpack!

## 👤 Autore

**Franchino961** — [GitHub](https://github.com/Franchino961-Mod)

## 💬 Supporto

Segnala bug o suggerisci nuove funzionalità sul nostro [Issue Tracker](../../issues). Per favore, includi la versione della mod e i log rilevanti.

---

**Creato con ❤️ per la community di AE2 & Draconic Evolution!**
