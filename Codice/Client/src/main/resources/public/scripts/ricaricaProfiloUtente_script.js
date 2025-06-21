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

        if (response.ok) {
            //Aggiorno la sessione nel client (Spark)
            await fetch("/aggiornaProfiloRicarica", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    importo: importo
                })
            });

            //Aggiorno il credito a video
            const current = document.getElementById("currentCreditModal");
            const currentCredit = parseFloat(current.value.replace("€", "").trim());
            const nuovoCredito = (currentCredit + importo).toFixed(2);
            current.value = `${nuovoCredito} €`;
            document.getElementById("userCredit").innerText = `${nuovoCredito} €`;

            messageDiv.innerHTML = `<div class="alert alert-success">Importo aggiornato con successo!</div>`;

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