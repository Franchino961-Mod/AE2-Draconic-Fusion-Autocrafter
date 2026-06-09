# ⚡ AE2 Draconic Fusion Autocrafter
**Automatizza il Fusion Crafting di Draconic Evolution con la potenza di AE2!**

[![Scarica su CurseForge](https://img.shields.io/badge/Scarica_su-CurseForge-orange?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/ae2-draconic-fusion-autocrafter)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)]()
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.223-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](../LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](../README.md)
[![it](https://img.shields.io/badge/lang-it-green.svg)](README.it.md)

> 📝 **Changelog**: Consulta [CHANGELOG.it.md](CHANGELOG.it.md) per la cronologia delle versioni.

---

## 📖 Panoramica

**AE2 Draconic Fusion Autocrafter** è un addon specializzato progettato per unire **Applied Energistics 2** e **Draconic Evolution**. Introduce un modo nativo per automatizzare il complesso processo di Fusion Crafting senza la necessità di circuiti di redstone disordinati, timer esterni o sottoreti ME complicate.

Grazie a Pattern Provider personalizzati e a una logica di routing intelligente, questa mod assicura che catalizzatori e ingredienti trovino sempre il loro posto corretto nella struttura di fusione, rendendo il crafting Draconic di alto livello semplice come un click nel tuo Terminale ME.

---

## 🌟 Perché usare AE2 Draconic Fusion Autocrafter?

- **Automazione Senza Redstone**: Niente più timer complessi o clock sub-networks di redstone per avviare il fusion crafting.
- **Routing Intelligente**: I catalizzatori vengono inviati automaticamente al core di fusione, e gli ingredienti vengono distribuiti in modo uniforme agli iniettori.
- **Crafting Sicuro**: Previene blocchi del sistema ME o perdite di oggetti attendendo che il core sia effettivamente libero prima di procedere.

---

## ✨ Funzionalità Principali

- 🧬 **Integrazione Nativa AE2**: Nuovi blocchi e parti che agiscono come i normali Pattern Provider ma ottimizzati per la Fusione.
- 🎯 **Routing Intelligente**: Distingue automaticamente tra il Catalizzatore (Core) e gli Ingredienti (Injector).
- 🔄 **Logica "Recipe-Aware"**: Assegna dinamicamente gli oggetti agli injector in base alla ricetta attiva per prevenire conflitti.
- ⚡ **Sistema di Retry**: Gestisce gli stati di "Core Occupato", evitando il timeout di AE2 durante le operazioni di crafting più lunghe.
- 🛠️ **Nessun Override Invasivo**: Non sovrascrive la logica degli inventari di Draconic Evolution, garantendo compatibilità con tutti i metodi di estrazione (Import Bus, tubi, etc.).
- 📦 **Design Compatto**: Usa la variante Panel (Pannello) per mantenere il tuo setup di fusione elegante e nascosto dietro i cavi.

---

## 🚀 Guida Rapida

### Configurazione dell'Automazione

1. **Crea** un `ME Draconic Pattern Provider` (Blocco o Pannello). ![Crafting Recipe](assets/image/ME_Draconic_Pattern_Provider-Recipe_Small.png)
2. **Posizionalo** adiacente a un **Draconic Fusion Crafting Core**.
3. **Configura** le tue ricette di Fusione nei Pattern AE2 normalmente (Input da un lato, Output dall'altro).
4. **Inserisci** i pattern nel Draconic Pattern Provider.
5. **Inizia a Craftare!** La mod farà automaticamente quanto segue:
    - Invierà il catalizzatore al Core.
    - Distribuirà gli ingredienti in modo deterministico agli injector validi nel raggio d'azione.
    - Aspetterà che il Core sia libero prima di inviare la ricetta successiva.

![ME Draconic Pattern Provider setup](assets/image/ME_Draconic_Pattern_Provider.png)

---

## 📦 Requisiti

- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.223 o superiore
- **Applied Energistics 2**: 19.2.17 o superiore
- **Draconic Evolution**: 3.1.4.632 o superiore
- **Brandon's Core**: Dipendenza richiesta per Draconic Evolution
- **Java**: 21 o superiore

---

## 📥 Installazione

1. Assicurati di avere installato NeoForge e tutte le dipendenze richieste.
2. Scarica il file `.jar` di `ae2-draconic-fusion-autocrafter`.
3. Inserisci il file nella cartella `mods` della tua installazione di Minecraft.
4. Avvia il gioco!

---

## 🖥️ Comportamento Client/Server

- **Server**: Richiesto. Gestisce l'iniezione dei pattern AE2, il matching delle ricette e il routing degli oggetti.
- **Client**: Richiesto. Gestisce il rendering dei blocchi, le interazioni GUI e l'integrazione delle ricette.

---

## 🤝 Compatibilità

- **Applied Energistics 2**: Integrazione profonda.
- **Draconic Evolution**: Supporto completo multi-block.
- **JEI/REI/EMI**: Supporto per la visualizzazione delle ricette e dei pattern.
- **Sophisticated Storage/Backpacks**: Funziona con inventari esterni.
- **Modpack**: Sei libero di includere e distribuire questa mod in qualsiasi modpack.

---

## ⚠️ Limitazioni Note

- Richiede che tutti gli iniettori siano correttamente alimentati ed associati al core.
- L'automazione dipende dalla visibilità diretta tra il core e gli iniettori, secondo le meccaniche base di Draconic Evolution.

---

## 🛠️ Risoluzione dei Problemi

### Il crafting è bloccato / gli oggetti non vengono inviati
- Merifica che tutti gli ingredienti siano definiti correttamente nel pattern AE2 e che il core sia attivo.
- Controlla se c'è un'altra ricetta in esecuzione nello stesso setup di fusione.

### Il sistema si blocca
- Assicurati che il Pattern Provider sia posizionato direttamente adiacente al core di fusione.

---

## ❓ FAQ

### D: Supporta tutti i tier di Draconic Evolution?
R: Sì, dai tier Basic fino a quelli Chaotic.

### D: Posso usarlo con altri tubi di automazione?
R: Sì, è pienamente compatibile con i normali metodi di estrazione oggetti (es. Import Bus, tubi).

---

## 💬 Supporto e Feedback

Segnala bug o suggerisci nuove funzionalità sul nostro [Issue Tracker](../../issues). Per favore, includi la versione della mod e i log rilevanti.

---

## 📄 Licenza

Questa mod è rilasciata sotto licenza [MIT](../LICENSE). Sei libero di includerla nei tuoi modpack!

## 👤 Autore

**Franchino961** — [GitHub](https://github.com/Franchino961-Mod)

---

## 🔗 Link

- [Draconic Evolution](https://www.curseforge.com/minecraft/mc-mods/draconic-evolution)
- [Applied Energistics 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2)
- [NeoForge](https://neoforged.net/)

---

**Creato con ❤️ per la community di AE2 & Draconic Evolution!**
