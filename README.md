# AA24-25-Gruppo18

Inardi Andrea - 20050110
Santosuosso Lorenzo - 20050494


## SOFTAWARE

- Intellij
- MQTTX
- DB BROWSER (SQLite)

## SETUP PER COLLAB
Il progetto va clonato per intero in una cartella a scelta in locale.
Fatto ciò, il progetto aperto, servirà per i vari commit & push e dovrà rimanere in background (non necessita JDK).

Per effettuare le varie implementazioni bisognerà aprire la cartella interessata a parte su una nuova finestra IntelliJ.
Ad esempio se volessi lavorare sulla Cartella client, la devo aprire a parte e NON devo lavorare direttamente sul progetto clonato!
Da qui in poi si potrò modificare tutto quanto e per confermare le modifiche, caricandole sul Git, bisognerà usare la finestra messa in backround.
Quando si fa il commit, bisogna verificare di avere tutti i change spuntati nell riquadro a sinistra.

Il modulo Client utilizza: corretto-1.8 Amazon Corretto version 1.8.0_452
<br>
Il modulo Server utilizza: temurin-17 Eclipse Temurin version 17.0.7

(Verificare che la porta selezionata sia sgombra, al momento è impostata sulla 3000, ma si può cambiare senza problemi)

Nel caso si creassero delle cartelle, bisogna caricarci dentro un qualsiasi tipo di file per essere preso anche dal Git, altrimenti non lo carica.

## SETUP PER AVVIARE
Il progetto è formato da due parti distinte Server e client.
Per avviare il progetto bisogna aprire in due finestre separate su IntelliJ le cartelle Client e Server si tuate in /Codice.

All'interno del Client ci sarà il main principale, da eseguire, nella classe Client.java che farà partire la web-app visibile in: http://localhost:3000.
<br>
All'interno del Server ci sarà il main principale, da eseguire, nella classe Service.java che farà partire tutta la parte della gestione delle api rest.

Per quanto riguarda l'MQTT è simulato e si trova in /it/uniupo/pissir/mqtt dove ci sono le diverse classi che gestiscono i messaggi, inviano i dati al server e simulano eventuali situazioni come:
<br>
-Ricarica della batteria
<br>
-Scaricamento della batteria
<br>
-Segnalazione di un malfunzionamento

## Project status

In convalidazione


## UTENZE

Admin: Mario Rossi <br>
Email: mario.rossi@example.com <br>
Password: admin

Utente base: Luigi Verdi <br>
Email: luigi.verdi@example.com <br>
Password: Luigi.Verdi1234

