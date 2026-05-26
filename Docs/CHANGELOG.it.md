# Changelog - AE2 Draconic Fusion Autocrafter

Tutte le modifiche rilevanti al mod **AE2 Draconic Fusion Autocrafter** saranno documentate in questo file.

Il formato è basato su [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
e questo progetto aderisce a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
