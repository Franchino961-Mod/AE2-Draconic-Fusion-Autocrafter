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
- **Automazione Core**
  - Supporto nativo AE2 per il Fusion Crafting di Draconic Evolution.
  - **ME Draconic Pattern Provider**: Variante a blocco dedicata per i setup di fusione.
  - **ME Draconic Pattern Provider (Panel)**: Variante a pannello elegante per automazioni compatte.
- **Ricette**
  - Aggiunta ricetta nativa **Fusion Crafting** per il ME Draconic Pattern Provider.
  - Aggiunta ricetta **Shapeless Crafting** per convertire il blocco Pattern Provider nella sua variante Panel.
- **Sistema di Routing Intelligente**
  - **Rilevamento Catalizzatore**: Identifica e invia automaticamente l'oggetto catalizzatore direttamente al Fusion Core.
  - **Routing "Recipe-Aware"**: Confronta dinamicamente gli ingredienti con la ricetta di fusione attiva per garantire il corretto posizionamento.
  - **Selezione Deterministica Injector**: Distribuzione intelligente degli oggetti agli injector in base alla distanza e all'orientamento.
- **Stabilità e Performance**
  - **Logica di Retry Intelligente**: Previene i fallimenti di AE2 gestendo gli stati di core "Occupato" o "In Carica".
  - **Gestione Nativa delle Capability**: Funziona con le capability di NeoForge senza bloccare gli inventari originali del mod.
  - **Integrazione Async**: Completamente compatibile con il flusso di inserimento basato su simulazione di AE2.
- **Asset & Modelli**
  - Texture per le parti, l'oggetto scheda di routing e il blocco pattern provider.
  - Modelli per blocco pattern provider, oggetto, pannello, scheda di routing e parti (inclusi canali attivi, accesi e spenti).
  - File di localizzazione di base in inglese e italiano.
  - Configurazione dei blockstate e metadati di pacchetto/mod.
- **Registro**
  - Registrazione di blocchi, oggetti e schede creative del mod.
- **Documentazione**
  - README iniziale con istruzioni di installazione, screenshot e diagrammi.

### Risolto
- **Compatibilità**
  - Corretto il formato delle ricette per Draconic Evolution 3.x (sistemati `techLevel`, `totalEnergy` e l'annidamento degli ingredienti).
  - Corretto il supporto **Jade/Waila**: i blocchi ora mostrano correttamente "ME Draconic Pattern Provider" invece di "Air".
- **UI/UX**
  - Corretti i titoli della GUI: la schermata del Pattern Provider ora mostra correttamente il nome personalizzato della mod invece del titolo predefinito di AE2.
  - Risolto il bug della versione nel menu mod di NeoForge (risolto il problema "0.0NONE").
- **Qualità del Codice**
  - Pulizia completa del codice: rimossi import inutilizzati, pacchetti vuoti e migliorati i commenti per chiarezza in tutte le classi.
  - Aggiunto Javadoc a tutte le classi principali e alle voci di registro.
  - Log ottimizzati: spostati i log diagnostici ridondanti al livello DEBUG.

### Tecnico
- Piattaforma: NeoForge 1.21.1
- Dipendenze: AE2 (19.2.17+), Draconic Evolution (3.1.4.632+)

---

## Roadmap di Sviluppo

### Fase 1 - Bootstrap ✅
- Configurazione del progetto e metadati.
- Registrazione della Fusion Routing Card.

### Fase 2 - Logica Core ✅
- Implementazione del Routing Service.
- Integrazione basata su reflection con Draconic Evolution.
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
