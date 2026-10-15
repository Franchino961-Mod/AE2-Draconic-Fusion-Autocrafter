# Changelog - AE2 Draconic Fusion Autocrafter

Tutte le modifiche rilevanti al mod **AE2 Draconic Fusion Autocrafter** saranno documentate in questo file.

Il formato è basato su [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
e questo progetto aderisce a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.7] - 2026-10-15

### Risolto
- **Autocrafting di Gruppo & Prevenzione Stalli**: Risolto il problema in cui la richiesta di oggetti multipli causava il sovraccarico degli iniettori e lo stallo del crafting. Il provider verifica ora che la struttura sia interamente pronta e libera (`isStructureReadyForCraft`), assicurando che lo slot di output del core dal craft precedente sia vuoto prima di inserire una nuova ricetta.
- **Interferenza tra Multi-Block Vicini**: Risolto il cross-talk tra molteplici setup di Draconic Fusion posizionati a distanza ravvicinata. I Pattern Provider ora danno priorità assoluta ai core direttamente adiacenti e utilizzano la vera distanza euclidea (`distSqr`) anziché l'ordine di scansione per assi delle coordinate.
- **Accumulo Catalizzatori Multi-Stack**: Risolto il bug in cui ricette con catalizzatori multipli nel core (come l'Awakened Draconium Block che richiede 4 blocchi) ricevevano solo 1 blocco se gli ingredienti venivano forniti in stack separati. Il sistema accumula ora correttamente il conteggio fino al target richiesto e convoglia eventuali item in eccesso negli iniettori.
- **Simulazione Stateful del Pattern**: Aggiornata la fase di simulazione del pattern per tracciare in memoria l'occupazione reale di core e iniettori durante tutti gli ingressi della ricetta, garantendo un'erogazione atomica priva di falsi positivi.

## [0.1.6] - 2026-09-21

### Aggiunto
- **Traduzioni**: Aggiunta la traduzione in italiano (`it_it`).
- **Ricette**: Aggiunta la ricetta shapeless per riconvertire il Pannello ME Draconic Pattern Provider in un Blocco.
- **Upgrades**: Aggiunto il supporto per la Capacity Card di AE2 al ME Draconic Pattern Provider, permettendo fino a 36 slot per pattern.

### Risolto
- **Routing Ricette**: Risolto un problema in cui la quantità di catalizzatori per ricette complesse (come l'Awakened Draconium Block) veniva erroneamente valutata come 1, causando l'inserimento errato degli oggetti rimanenti negli iniettori invece che nel core. Il sistema ora estrae in modo robusto le quantità direttamente da `Ingredient.getItems()` quando necessario.

### Modificato
- **Dipendenze**: Aggiunte le dipendenze esplicite per `BrandonsCore` e `DraconicEvolution` al processo di build.
- **Logica di Routing**: `FusionRoutingService` ora associa dinamicamente sia il catalizzatore che gli ingredienti tramite la ricetta `IFusionRecipe` attiva, supportando il matching esatto di NBT/Components (risolvendo i problemi con oggetti come i Draconic Energy Relay Crystals e uova di Productive Bees).
- **Logica di Routing**: `FusionRoutingService` ora ordina gli iniettori in modo deterministico in base alla distanza al quadrato e alla direzione relativa per un'esecuzione prevedibile.
- **Uso API**: Rimosso completamente l'accesso alle API tramite Reflection. La mod ora interagisce direttamente con le interfacce native `TileFusionCraftingCore` e `IFusionInjector`.
- **Integrazione AE2**: `DraconicPatternProviderLogic` ora gestisce correttamente il fallimento di inserimento dei pattern restituendo `false`, permettendo all'`ExecutingCraftingJob` di AE2 di riprovare senza distruggere o bloccare gli oggetti.

### Rimosso
- **Codice / Oggetti Inutilizzati**: Rimossa completamente la `Fusion Routing Card` deprecata, inclusi asset (texture/modelli), chiavi di traduzione e logica di registrazione.
- **Traduzioni**: Rimossa la sovrascrittura del nome personalizzato in `DraconicPatternProviderBlockEntity` e `DraconicPatternProviderPart` per delegare la gestione alla classe base.
- **Documentazione**: Rimossi i claim di integrazione non verificati dai README (JEI/REI/EMI, Sophisticated Storage) e aggiornato il raggio d'azione documentato a 8 blocchi.

---

## [0.1.5] - 2026-08-07

### Risolto
- **Tag di efficacia del piccone**: Corretto il percorso della cartella dei tag per i blocchi da `tags/blocks/mineable/` a `tags/block/mineable/`, ripristinando il corretto funzionamento del piccone sul blocco ME Draconic Pattern Provider.
- **Titolo GUI dei Pattern Provider**: Rimosso il file di override della schermata nel namespace di AE2 (`assets/ae2/screens/pattern_provider.json`) che rinominava erroneamente la GUI di tutti i Pattern Provider standard di AE2 nel gioco.

---

## [0.1.4] - 2026-06-25

### Risolto
- **Bug di Riconnessione alla Rete**: Risolto un problema critico in cui il ME Draconic Pattern Provider perdeva la connessione alla rete ME al riavvio del server o al ricaricamento dei chunk, costringendo il giocatore a rompere e ripiazzare il blocco. La capability `IInWorldGridNodeHost` è ora registrata correttamente per il block entity.

### Modificato
- **Refactoring del Codice**: Rimosse le annotazioni superflue `@SuppressWarnings("null")` in `FusionRoutingService.java`.
- **Refactoring del Codice**: Soppresso un avviso IDE benigno `Null type mismatch` in `ModBlocks.java` relativo ai tipi del builder per il block entity.
- **Git**: Aggiunta la directory `.eclipse` al file `.gitignore`.

---

## [0.1.3] - 2026-06-09

### Risolto
- **Drop al disassemblaggio/rottura**: Risolto un bug per cui facendo shift-tasto destro sul ME Draconic Pattern Provider con una chiave inglese (come il Configurator di Mekanism o il Certus Quartz Wrench di AE2) o rompendolo normalmente, il blocco veniva eliminato senza droppare nulla. Aggiunta la loot table mancante per il blocco.

---

## [0.1.2] - 2026-05-26

### Aggiunto
- **Traduzioni**: Aggiunte traduzioni localizzate per Tedesco (`de_de`), Spagnolo (`es_es`), Francese (`fr_fr`), Portoghese (Brasile) (`pt_br`), Russo (`ru_ru`) e Cinese (Semplificato) (`zh_cn`).
- **Configurazione del Progetto**: Aggiunto il file `.gitattributes` per normalizzare i terminatori di riga (LF) per codice, risorse, proprietà, markdown e script.

### Modificato
- **Documentazione**: Ampliato il README con dettagli sui requisiti (Java 21+), passaggi di installazione, comportamento client/server, compatibilità, limitazioni note, risoluzione dei problemi, FAQ e canali di supporto.
- **Configurazione Git**: Aggiornato il file `.gitignore` per escludere file di journal del database, script locali e includere nella whitelist i wrapper Gradle.

---

## [0.1.1] - 2026-05-19

### Aggiunto
- **Documentazione**: Aggiunta alla documentazione l'immagine d'esempio dei pattern per il ME Draconic Pattern Provider.

### Risolto
- **B011 – Item voided alla cancellazione del craft**: Risolto un bug critico in cui gli item inviati agli iniettori venivano persi definitivamente in caso di cancellazione del crafting a causa di iniettori insufficienti. La fase di esecuzione esegue ora un rollback completo (svuota iniettori e core) e restituisce `false` per preservare gli item nella rete AE2.
- **Qualità del Codice**: Rimossi `@SuppressWarnings` non necessari e aggiunti null-check espliciti prima delle chiamate API annotate con `@Nonnull` in `FusionRoutingService`.
- **Qualità del Codice**: Aggiunte le annotazioni `@Nonnull` mancanti sui parametri di `getCloneItemStack` in `DraconicPatternProviderBlock` per rispettare il contratto ereditato da `Block`.

---

## [0.1.0] - 2026-04-22

### Aggiunto
- **Ricette**
  - Aggiunta ricetta nativa **Fusion Crafting** per il ME Draconic Pattern Provider.
  - Aggiunta ricetta **Shapeless Crafting** per convertire il blocco Pattern Provider nella sua variante Panel.
- **Documentazione**
  - Ampliato il README con immagini e schemi delle ricette e dei pattern.
- **Asset**
  - Aggiornata l'icona ufficiale della mod.

### Risolto
- **Qualità del Codice**
  - Pulizia estesa dei commenti, formattazione e ottimizzazione della chiarezza dei log per tutte le classi (es. `FusionStructureScanner`, `FusionRoutingService`, `DraconicPatternProviderPart`).

---

## [0.0.4] - 2026-04-21

### Aggiunto
- **Integrazioni**
  - Registrazione dei BlockEntities per il supporto e la visualizzazione in Jade/Waila e AE2.
- **Funzionalità**
  - Metodi per nome personalizzato e display name per `DraconicPatternProviderPart` e `DraconicPatternProviderBlockEntity`.
  - Aggiunto metodo clone item stack a `DraconicPatternProviderBlock`.
  - Aggiunta configurazione JSON per il pattern provider di Draconic Fusion Autocrafter.
- **Documentazione**
  - Aggiunte immagini dell'installazione e schemi di configurazione.

### Modificato
- **Sistema di Build**
  - Rifattorizzati `build.gradle` e `gradle.properties` per una migliore gestione degli attributi manifest e processing delle risorse.
  - Aggiornata la gestione del placeholder di versione in `neoforge.mods.toml`.

---

## [0.0.3] - 2026-04-20

### Aggiunto
- **Logica Core**
  - Implementata logica dello scanner di struttura per scansionare i setup multi-blocco di fusione.
  - Implementato servizio core di routing per catalizzatori e ingredienti.
  - Creata enumerazione per i risultati di routing.
- **Integrazione AE2**
  - Aggiunti blocchi e parti ME Draconic Pattern Provider personalizzati.
  - Aggiunta logica del pattern provider e block entity personalizzata.
  - Implementata integrazione upgrade della scheda per la Fusion Routing Card.
  - Implementate utility di accesso al bus di fusione AE2.

---

## [0.0.2] - 2026-04-20

### Aggiunto
- **Registro**
  - Registrazione di blocchi, oggetti (inclusa la Fusion Routing Card) e schede creative della mod.
- **Asset & Modelli**
  - Icona della mod e configurazioni dei metadati del pacchetto.
  - Blockstate di base per il ME Draconic Pattern Provider.
  - File di traduzione in inglese e italiano.
  - Modelli 3D per blocco, oggetto, pannello, scheda di routing e parti (inclusi canali attivi, accesi e spenti).
  - Texture per parti, blocchi e schede di routing.

---

## [0.0.1] - 2026-04-20

### Aggiunto
- **Bootstrap**
  - Struttura iniziale del progetto e configurazione dei metadati.
  - Configurato il sistema di build con script Gradle, settings, properties, wrapper scripts e file batch.
  - README iniziale del progetto.
  - Aggiunte regole iniziali del file `.gitignore`.

---

## Roadmap di Sviluppo

### Fase 1 - Bootstrap ✅
- Configurazione del progetto e metadati.
- Registrazione della Fusion Routing Card.

### Fase 2 - Logica Core ✅
- Implementazione del Routing Service.
- Reflection-based integration with Draconic Evolution.
- Sistema di snapshot per la scansione multi-block.

### Fase 3 - Integrazione AE2 ✅
- Implementazione di blocchi e parti Pattern Provider personalizzati.
- Upgrade hook per l'Export Bus di AE2.

### Fase 4 - Rifiniture ed Espansione 📋
- Miglioramenti HUD/GUI.
- Opzioni di configurazione avanzate via JSON.
- Effetti particellari e suoni.

---

*Formato: [Versione] - Data*  
*Categories: Aggiunto, Modificato, Risolto, Rimosso, Tecnico*
