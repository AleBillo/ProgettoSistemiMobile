# Relazione finale progetto Android: Sasso-Carta-Forbici con riconoscimento gesti

## 1. Scopo del progetto

Il progetto nasce con l’obiettivo di realizzare un’applicazione mobile Android che consenta all’utente di giocare a Sasso-Carta-Forbici contro il computer. La particolarità sta nell’utilizzo della fotocamera del dispositivo e di un modello di Machine Learning (basato su PyTorch) in grado di riconoscere in tempo reale la forma della mano dell’utente (sasso, carta o forbice) e contenendo utenticazione tramite Google e uno storico partite consultabile.
L'app intende mettere in pratica i principi di progettazione software moderni, come la modularità, la riusabilità e la separazione delle responsabilità, sfruttando il linguaggio Kotlin e le tecnologie Jetpack Compose, ViewModel, LiveData e Coroutines.  

## Funzionalità principali


- **Accesso alla fotocamera:**  
  Dopo aver ottenuto i permessi, l'app attiva la fotocamera per acquisire in tempo reale l'immagine della mano dell'utente.

- **Interfaccia moderna:**  
  L’interfaccia è realizzata in Jetpack Compose, che permette una UI moderna, responsive e facilmente manutenibile.

- **Riconoscimento dei gesti con PyTorch:**  
  Viene utilizzato un modello preallenato in PyTorch per classificare il gesto mostrato nella fotocamera come sasso, carta o forbice.

- **Storico delle partite:**  
  Le partite vengono salvate in locale. Ogni partita registra la mossa del giocatore, quella del computer e il conseguente esito. Lo storico è sempre accessibile tramite una sezione dedicata della UI, realizzata in Jetpack Compose.

---

## 2. Descrizione della struttura dei sorgenti

L’organizzazione del codice segue rigorosamente il principio di “separation of concerns”, isolando le responsabilità in moduli e packages distinti:

```
progettoSM/
│
├── app/
│   └── main/
├── common/
│   ├── game/
│   ├── login/
│   ├── permission/
│   ├── rpsmodel/
│   └── storage/
└── feature/
    ├── camera/
    ├── scoreboard/
    └── settings/
```

### Dettaglio moduli e packages

- **app/**: modulo principale, contiene MainActivity, entry point dell’applicazione, e il setup generale (tema, navigation, ecc.).
- **common/**: contiene codice riutilizzabile e condiviso:
  - **game/**: logica del gioco (es. calcolo vincitore, rappresentazione delle mosse, modelli dati).
  - **login/**: gestione dell’autenticazione.
  - **permission/**: gestione centralizzata dei permessi di sistema, in particolare l’accesso alla fotocamera.
- **feature/**: raccoglie tutte le funzionalità accessibili dall’utente:
  - **camera/**: gestione della fotocamera, acquisizione immagini, interfacciamento col modello ML, UI di gioco.
  - **scoreboard/**: salvataggio locale e visualizzazione dello storico partite, UI per consultazione risultati.
  - **settings/**: gestione delle preferenze utente (tema, lingua, permessi).

---

 ## APP
 La cartella `app` rappresenta il modulo principale dell’applicazione Android. All’interno di questa cartella si trovano:
  - **build.gradle.kts** e file di configurazione per la build
  - **src/** che contiene il codice sorgente vero e proprio
  - Nel percorso `progettoSM/app/src/main/java/com/example/pingu/MainActivity.kt` troviamo il file principale dell’applicazione:
   **MainActivity.kt**
    
    Il file `MainActivity.kt` rappresenta il punto di ingresso dell’applicazione  e  Gestisce la 
    navigazione tra le tre schermate principali dell’app:
    
    - **Camera**: per giocare tramite la fotocamera (serve permesso).
    - **Scoreboard**: mostra una lista di partite giocate 
    - **Settings**: permette di accedere alle impostazioni di sistema dell'app

### Esempio di navigazione principale (snippet da MainActivity):

```kotlin
Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
        NavigationBar {
            bottomNavItems.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = currentScreen.route == screen.route,
                    onClick = { currentScreen = screen }
                )
            }
        }
    }
) { innerPadding ->
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (currentScreen) {
            Screen.Camera -> { /* UI e logica fotocamera */ }
            Screen.Scoreboard -> { ScoreboardRoute() }
            Screen.Settings -> { SettingsScreen(...) }
        }
    }
}
```
---
 
## FEATURE
  La cartella feature rappresenta tutte quelle funzionalità tangibili, che l'utente può usare per sfruttare l'applicazione.
  è composto da :

  #### 1. `camera/`
  Questa sottocartella contiene il codice relativo alla gestione della fotocamera e al riconoscimento dei gesti della mano.  
  **Ruolo principale:**
- Attivare la fotocamera del dispositivo.
- Acquisire in tempo reale le immagini della mano dell’utente.
- Interfacciarsi con il modello PyTorch per riconoscere il gesto (sasso, carta o forbice).
- Fornire una UI reattiva e accessibile tramite Jetpack Compose per la parte di gioco.

#### 2. `scoreboard/`
  Questo modulo contiene la logica e la UI per la gestione dello storico delle partite e dei risultati.  
  **Ruolo principale:**
  - Salvare localmente i dati di ogni partita giocata (mossa utente, mossa computer, esito).
  - Visualizzare lo storico delle partite tramite un’interfaccia dedicata.
  - Permettere all’utente di consultare le proprie performance nel tempo.

#### 3. `settings/`
  Questo modulo gestisce le impostazioni e le preferenze dell’utente.  
  **Ruolo principale:**
  - Offrire una schermata dove l’utente può modificare le proprie preferenze ( tema, lingua, ecc.).
  - Gestire eventuali configurazioni relative al login e alla gestione dei permessi.

---
## COMMON
   questo modulo contiene codice riutilizzabile e condiviso tra più parti dell'app, come utility, gestione delle permissioni, autenticazione (es. Google Sign-In), 
   estensioni e costanti, per mantenere il progetto organizzato e modulare.



#### 1. `login/`

 

#### 2. `permission/`
   Questo modulo centralizza la logica per la gestione dei permessi Android, in particolare per l’accesso alla fotocamera.  
  - Gestire la richiesta di permessi runtime come CAMERA.
  - Fornire utilities per controllare lo stato dei permessi e reagire ai cambiamenti (es. permesso negato, permanentemente negato, ecc.).


---

## 3. Utilizzo dei componenti di lifecycle (ViewModel, LiveData, ecc.)

L’applicazione utilizza ViewModel e LiveData per la gestione dello stato e dei dati persistenti attraverso i cambi di configurazione e il ciclo di vita dell’activity.  
Ad esempio, nella gestione della schermata Scoreboard, i dati delle partite sono mantenuti in un ViewModel, che espone uno stream LiveData osservato dalla UI:

```kotlin
class ScoreboardViewModel : ViewModel() {
    private val _games = MutableLiveData<List<GameResult>>()
    val games: LiveData<List<GameResult>> = _games

    fun loadGames() {
        // Caricamento dallo storage locale (es. Room DB o file)
        viewModelScope.launch {
            val results = repository.getGames()
            _games.value = results
        }
    }
}
```
La UI si aggiorna automaticamente quando i dati vengono modificati:

```kotlin
@Composable
fun ScoreboardScreen(viewModel: ScoreboardViewModel = viewModel()) {
    val games by viewModel.games.observeAsState(emptyList())
    // Visualizzazione lista partite...
}
```

---

## 4. Utilizzo di Coroutines e separazione tra Main Thread e Background Thread

L’applicazione sfrutta le coroutines di Kotlin per tutte le operazioni che richiedono l’uso di thread secondari, garantendo così una UI sempre fluida e reattiva.

- Tutti i task di I/O, come il caricamento/salvataggio dello storico partite o le chiamate alle API, sono eseguiti in coroutine scope con dispatcher appropriato (come `Dispatchers.IO`):

```kotlin
viewModelScope.launch(Dispatchers.IO) {
    val response = apiService.getRemoteData()
    withContext(Dispatchers.Main) {
        // Aggiorna lo stato UI con il risultato
    }
}
```

- La separazione tra codice su Main Thread e Background Thread è garantita dall’utilizzo dei Dispatcher, evitando blocchi della UI anche in caso di operazioni “pesanti”.

---

## 5. Chiamate verso API remote

Il progetto soddisfa il requisito delle chiamate a due o più API remote. In particolare:

**1) Login Google Sign-In API**  

**2) Download modello PyTorch da risorsa remota**  
All’avvio o durante l’uso, l’app può scaricare il modello PyTorch da una risorsa remota, assicurandosi di avere la versione aggiornata del modello ML.  
Esempio di chiamata (semplificata):

```kotlin
suspend fun fetchModel(): ModelData {
    return apiService.downloadModelFile() // Coroutines + Retrofit/Http
}
```

---

## 6. Punti di forza

- **Architettura modulare**: la separazione in moduli e packages permette la massima manutenibilità, testabilità e riusabilità del codice.
- **Adozione di Jetpack Compose**: tutte le schermate sono dichiarative, moderne e facilmente modificabili.
- **Gestione robusta del ciclo di vita**: uso di ViewModel e LiveData garantisce la persistenza e la corretta gestione dei dati e dello stato UI.
- **Gestione permessi centralizzata**: migliora la sicurezza e la UX.
- **Uso di Coroutines**: tutte le operazioni “costose” sono eseguite off-main thread per una UI sempre fluida.
- **Integrazione ML on-device**: inferenza direttamente sul dispositivo, senza invio di dati sensibili all’esterno.
- **Facilità di estensione**: la struttura modulare e l’uso di pattern moderni (come le sealed class per la navigation) permette di aggiungere nuove funzionalità con minimi rischi di regressione.
- **Chiamate API remote**: login sicuro e aggiornamento risorse da servizi esterni.

---

## 7. Possibili migliorie

- Aggiunta di test automatici per migliorare l’affidabilità del software.
- Miglioramento delle interfacce utente e dell’esperienza d’uso.
- Ottimizzazione delle performance nelle parti critiche dell’applicazione.
- Possibile migrazione o supporto a ulteriori piattaforme.

---

## Membri del gruppo

- Jacopo Maria Spitaleri
- Alessandro Dominici
- Seck Mactar Ibrahima
