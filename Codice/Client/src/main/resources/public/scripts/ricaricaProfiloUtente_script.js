//Script per la ricarica del profilo utente ed aggiornamento dei dati
window.handleRecharge = async function () {
    const amountInput = document.getElementById("rechargeAmount");
    const cardInput = document.getElementById("paymentMethod"); //Da ultimare
    const messageDiv = document.getElementById("rechargeMessage");

    const importo = parseFloat(amountInput.value);
    const numeroCarta = cardInput.value.trim();
    const email =  document.getElementById("utenteEmail")?.value;

    // Validazione base
    if (!email) {
        messageDiv.innerHTML = "<div class='alert alert-danger'>Email utente non trovata. Effettua il login.</div>";
        return;
    }

    if (isNaN(importo) || importo <= 0) {
        messageDiv.innerHTML = "<div class='alert alert-danger'>Inserisci un importo valido.</div>";
        return;
    }

    if (numeroCarta.length < 12) {
        messageDiv.innerHTML = "<div class='alert alert-danger'>Numero di carta non valido.</div>";
        return;
    }

    // Prepara il payload
    const payload = {
        email: email,
        importo: importo
    };

    try {
        const response = await fetch("http://localhost:4567/api/v1.0/ricaricaCredito", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const text = await response.text(); // il server restituisce testo, non JSON


        //se è andato tutto bene, il server risponde con un codice 200 ed allora aggiorno il credito in locale in base all'importo ricaricato
        if (response.ok) {

            //Aggiorno il credito a video
            const current = document.getElementById("currentCreditModal");
            const currentCredit = parseFloat(current.value.replace("€", "").trim());
            const nuovoCredito = (currentCredit + importo).toFixed(2);
            current.value = `${nuovoCredito} €`;
            document.getElementById("userCredit").innerText = `${nuovoCredito} €`;

            messageDiv.innerHTML = `<div class="alert alert-success">Importo aggiornato con successo!</div>`;

            // Aggiorno i punti credito utente
            try {
                const aggiornaResponse = await fetch('/aggiornoPuntiCreditoUtente', {
                    method: 'POST'
                });
                if (!aggiornaResponse.ok) {
                    console.warn('Errore durante aggiornamento dati utente');
                } else {
                    console.log('Utente aggiornato correttamente');
                }
            } catch (err) {
                console.error('Errore di rete durante aggiornamento utente:', err);
            }

            setTimeout(() => {
                const modal = bootstrap.Modal.getInstance(document.getElementById("rechargeModal"));
                modal.hide();
            }, 1500);

        } else {
            messageDiv.innerHTML = `<div class="alert alert-danger">${text}</div>`;
        }

    } catch (err) {
        console.error("Errore nella richiesta di ricarica:", err);
        messageDiv.innerHTML = `<div class="alert alert-danger">Errore di rete. Riprova.</div>`;
    }
}

//Gestisce la conversione dei punti in credito.
window.handleConversion = async function () {
    const messageDiv = document.getElementById("conversionMessage");
    messageDiv.innerHTML = ""; // Pulisce messaggi precedenti

    // 1. Recupera gli elementi e i dati dall'interfaccia
    const email = document.getElementById("utenteEmail")?.value;
    const userPointsSpan = document.getElementById("userPoints");
    const userCreditSpan = document.getElementById("userCredit");
    const creditModalInput = document.getElementById("currentCreditModal");
    const pointsModalSpan = document.getElementById("pointsForConversion");

    const puntiAttuali = parseInt(userPointsSpan.innerText, 10);
    const creditoAttuale = parseFloat(userCreditSpan.innerText.replace("€", "").trim());

    // 2. Validazione dei dati
    if (!email) {
        messageDiv.innerHTML = "<div class='alert alert-danger'>Email utente non trovata. Riprova a fare il login.</div>";
        return;
    }

    if (isNaN(puntiAttuali) || puntiAttuali < 50) {
        messageDiv.innerHTML = "<div class='alert alert-warning'>Non hai abbastanza punti per la conversione (minimo 50).</div>";
        return;
    }

    // 3. Esegue la logica di conversione sul client
    const puntiPerEuro = 50;
    const blocchiConvertibili = Math.floor(puntiAttuali / puntiPerEuro);
    const puntiDaConvertire = blocchiConvertibili * puntiPerEuro;
    const creditoOttenuto = blocchiConvertibili; // 1€ per blocco

    const nuoviPunti = puntiAttuali - puntiDaConvertire;
    const nuovoCredito = creditoAttuale + creditoOttenuto;

    // 4. Prepara il payload con i nuovi dati calcolati da inviare al server
    const payload = {
        email: email,
        nuoviPunti: nuoviPunti,
        nuovoCredito: nuovoCredito
    };

    try {
        // 5. Invia i dati aggiornati al server per la memorizzazione nel database.
        //    NOTA: Dovrai creare questo nuovo endpoint sul tuo backend (server porta 4567).
        const response = await fetch("http://localhost:4567/api/v1.0/conversionePunti", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        // 6. Gestisce la risposta del server
        if (response.ok) {
            // 7. Aggiorna l'interfaccia utente con i nuovi valori
            userPointsSpan.innerText = nuoviPunti;
            pointsModalSpan.innerText = nuoviPunti;
            userCreditSpan.innerText = `${nuovoCredito.toFixed(2)} €`;
            creditModalInput.value = `${nuovoCredito.toFixed(2)} €`;

            messageDiv.innerHTML = `<div class="alert alert-success">Conversione riuscita! Hai ottenuto ${creditoOttenuto.toFixed(2)}€.</div>`;

            // 8. Aggiorna la sessione sul server client (come fa la ricarica)
            try {
                const aggiornaResponse = await fetch('/aggiornoPuntiCreditoUtente', {
                    method: 'POST'
                });
                if (!aggiornaResponse.ok) {
                    console.warn('Errore durante aggiornamento dati utente nella sessione.');
                } else {
                    console.log('Sessione utente aggiornata correttamente.');
                }
            } catch (err) {
                console.error('Errore di rete durante aggiornamento sessione utente:', err);
            }

        } else {
            // Se il server restituisce un errore
            const errorText = await response.text();
            messageDiv.innerHTML = `<div class="alert alert-danger">${errorText || 'Errore durante la finalizzazione della conversione.'}</div>`;
        }

    } catch (err) {
        console.error("Errore nella richiesta di conversione:", err);
        messageDiv.innerHTML = `<div class="alert alert-danger">Errore di rete. Riprova.</div>`;
    }
}