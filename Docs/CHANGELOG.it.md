# Changelog - AE2 Draconic Fusion Autocrafter

Tutte le modifiche rilevanti al mod **AE2 Draconic Fusion Autocrafter** saranno documentate in questo file.

Il formato è basato su [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
e questo progetto aderisce a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.1] - 2 Maggio 2026

### Risolto
- **B011 – Item voided alla cancellazione del craft**: Quando un craft veniva cancellato con injector
  insufficienti, gli item già inviati agli injector andavano persi definitivamente. La fase di esecuzione
  ora esegue un rollback completo (svuota gli injector e il catalizzatore dal core) e restituisce `false`
  in modo che AE2 mantenga correttamente gli item nella rete.
- **Qualità del Codice**: Rimossi `@SuppressWarnings` non necessari e aggiunti null-check espliciti
  prima delle chiamate API annotate con `@Nonnull` in `FusionRoutingService`.
- **Qualità del Codice**: Aggiunte le annotazioni `@Nonnull` mancanti sui parametri di `getCloneItemStack`
  in `DraconicPatternProviderBlock` per rispettare il contratto ereditato da `Block`.

---

## [0.1.0] - 22 Aprile 2026 (Rilascio Iniziale Beta)

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

### Risolto
- **Compatibilità**
  - Corretto il formato delle ricette per Draconic Evolution 3.x (sistemati `techLevel`, `totalEnergy` e l'annidamento degli ingredienti).
  - Corretto il supporto **Jade/Waila**: i blocchi ora mostrano correttamente "ME Draconic Pattern Provider" invece di "Air".
- **UI/UX**
  - Corretti i titoli della GUI: la schermata del Pattern Provider ora mostra correttamente il nome personalizzato della mod invece del titolo predefinito di AE2.
  - Risolto il bug della versione nel menu mod di NeoForge (risolto il problema "0.0NONE").
- **Qualità del Codice**
  - Pulizia completa del codice: rimossi import inutilizzati e pacchetti vuoti.
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
*Categorie: Aggiunto, Modificato, Risolto, Rimosso, Tecnico*
