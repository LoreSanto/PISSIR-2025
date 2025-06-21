// Variabile globale per i dati degli utenti, inizializzata come array vuoto
let datiRichiesti = [];

let currentFilter = 'ALL'; // Filtro di stato corrente
let searchTerm = ''; // Termine di ricerca corrente

// Elementi del DOM
const tableBody = document.getElementById('userTableBody');
const searchInput = document.getElementById('searchInput');
const filterButtons = document.querySelectorAll('.filter-btn');
const totalUsersEl = document.getElementById('totalUsers');
const activeUsersEl = document.getElementById('activeUsers');
const suspendedUsersEl = document.getElementById('suspendedUsers');
const noResultsMessageEl = document.getElementById('noResultsMessage');

// Funzione per recuperare e processare i dati dall'URL
async function caricaEProcessaDatiUtenti() {
    try {
        const response = await fetch('http://localhost:4567/api/v1.0/users');
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.status}`);
        }
        const datiUtentiRaw = await response.json();

        // Mappa i dati grezzi nel formato desiderato e assegnali a datiRichiesti
        datiRichiesti = datiUtentiRaw.map(utente => {
            return {
                nome: utente.nome,
                cognome: utente.cognome,
                email: utente.email,
                sospensioni: utente.num_sospensioni,
                stato: utente.stato
            };
        });

        // Una volta che i dati sono caricati e processati, renderizza la tabella
        renderUserTable();
        // Le statistiche verranno aggiornate da renderUserTable()

    } catch (errore) {
        console.error("Impossibile recuperare i dati degli utenti:", errore);
        // Potresti voler mostrare un messaggio di errore all'utente qui
        if (noResultsMessageEl) {
            noResultsMessageEl.textContent = "Errore nel caricamento dei dati degli utenti.";
            noResultsMessageEl.style.display = 'block';
        }
        if (tableBody) tableBody.innerHTML = ''; // Pulisce la tabella in caso di errore
        if (totalUsersEl) totalUsersEl.textContent = '0';
        if (activeUsersEl) activeUsersEl.textContent = '0';
        if (suspendedUsersEl) suspendedUsersEl.textContent = '0';
    }
}

// Funzione per aggiornare le statistiche
function updateStats() {
    // Assicurati che gli elementi esistano prima di provare ad accedervi
    if (!totalUsersEl || !activeUsersEl || !suspendedUsersEl) return;

    // Filtra solo se datiRichiesti è popolato e non è null
    const activeUsers = datiRichiesti ? datiRichiesti.filter(user => user.stato === 'attivo').length : 0;
    const suspendedUsers = datiRichiesti ? datiRichiesti.filter(user => user.stato === 'sospeso').length : 0;
    totalUsersEl.textContent = datiRichiesti ? datiRichiesti.length : 0;
    activeUsersEl.textContent = activeUsers;
    suspendedUsersEl.textContent = suspendedUsers;
}

// Funzione per renderizzare la tabella degli utenti
function renderUserTable() {
    // Assicurati che gli elementi esistano
    if (!tableBody || !noResultsMessageEl) return;

    tableBody.innerHTML = ''; // Pulisce la tabella

    // Se datiRichiesti non è ancora stato popolato o è vuoto, gestiscilo
    if (!datiRichiesti || datiRichiesti.length === 0) {
        // Se non ci sono dati (potrebbe essere prima del fetch o fetch fallito),
        // non mostrare "Nessun utente corrisponde..." a meno che non sia un errore esplicito.
        // La gestione dell'errore di fetch è in caricaEProcessaDatiUtenti.
        // Se i dati sono stati fetchati ma sono vuoti, allora mostra il messaggio.
        if (datiRichiesti && datiRichiesti.length === 0 && !searchTerm && currentFilter === 'ALL') {
            noResultsMessageEl.textContent = "Nessun utente disponibile.";
            noResultsMessageEl.style.display = 'block';
        } else {
            noResultsMessageEl.style.display = 'none';
        }
        updateStats(); // Aggiorna le statistiche (probabilmente a zero)
        return;
    }

    const filteredUsers = datiRichiesti.filter(user => {
        const statusMatch = currentFilter === 'ALL' || user.stato === currentFilter;
        const searchMatch = searchTerm === '' ||
            user.nome.toLowerCase().includes(searchTerm) ||
            user.cognome.toLowerCase().includes(searchTerm) ||
            user.email.toLowerCase().includes(searchTerm);
        return statusMatch && searchMatch;
    });

    if (filteredUsers.length === 0) {
        noResultsMessageEl.textContent = "Nessun utente corrisponde ai criteri di ricerca.";
        noResultsMessageEl.style.display = 'block';
    } else {
        noResultsMessageEl.style.display = 'none';
    }

    filteredUsers.forEach(user => {
        const row = tableBody.insertRow();
        row.insertCell().textContent = user.nome;
        row.insertCell().textContent = user.cognome;
        row.insertCell().textContent = user.email;
        row.insertCell().textContent = user.sospensioni !== undefined ? user.sospensioni : 0; // Default a 0 se undefined

        const statusCell = row.insertCell();
        const statusBadge = document.createElement('span');
        statusBadge.classList.add('status-badge');
        statusBadge.classList.add(user.stato === 'attivo' ? 'status-attivo' : 'status-sospeso');
        statusBadge.textContent = user.stato;
        statusCell.appendChild(statusBadge);

        const actionCell = row.insertCell();
        const toggleButton = document.createElement('button');
        toggleButton.classList.add('btn', 'btn-sm', 'btn-toggle-status');
        if (user.stato === 'attivo') {
            toggleButton.classList.add('btn-warning');
            toggleButton.innerHTML = '<i class="fas fa-user-slash"></i> Sospendi';
        } else {
            toggleButton.classList.add('btn-success');
            toggleButton.innerHTML = '<i class="fas fa-user-check"></i> Attiva';
        }
        toggleButton.addEventListener('click', () => toggleUserStatus(user.email)); // Assumi che toggleUserStatus esista
        actionCell.appendChild(toggleButton);
    });
    updateStats();
}

// Funzione per inviare i dati aggiornati al server
async function updateUserOnServer(email, nuovoStato, sospensioni) {
    const url = 'http://localhost:4567/api/v1.0/updateUserStatus';

    // Crea l'oggetto payload da inviare come JSON
    const payload = {
        email: email,
        stato: nuovoStato,
        num_sospensioni: sospensioni
    };

    console.log("JSON da inviare al server:", JSON.stringify(payload)); // Mostra il JSON nella console

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json', // Indica che il corpo della richiesta è JSON
            },
            body: JSON.stringify(payload) // Converti l'oggetto JavaScript in una stringa JSON
        });

        // Controlla se la richiesta è andata a buon fine (status code 2xx)
        if (response.ok) {
            // Prova a parsare la risposta come JSON, se il server restituisce dati
            // Potrebbe restituire l'utente aggiornato o un semplice messaggio di successo
            const responseData = await response.json();
            console.log('Stato utente aggiornato con successo:', responseData);
            return { success: true, data: responseData };
        } else {
            // Se la risposta non è ok, gestisci l'errore
            // Il server potrebbe restituire un messaggio di errore in JSON
            let errorData;
            try {
                errorData = await response.json();
            } catch (e) {
                errorData = { message: 'Errore non JSON dal server.' };
            }
            console.error('Errore durante l\'aggiornamento dello stato utente:', response.status, errorData);
            return { success: false, status: response.status, error: errorData };
        }
    } catch (error) {
        // Gestisci errori di rete o altri problemi con la richiesta fetch
        console.error('Errore di rete o durante la richiesta fetch:', error);
        return { success: false, error: error.message };
    }
}

// Funzione per cambiare lo stato di un utente
function toggleUserStatus(userMail) {
    if (!datiRichiesti) return; // Guardia nel caso i dati non siano ancora stati caricati

    const user = datiRichiesti.find(u => u.email === userMail);
    if (user) {
        updateUserOnServer(user.email, user.stato === 'attivo' ? 'sospeso' : 'attivo', user.sospensioni)
           .then(() => {
             if (user.stato === 'attivo') {
               user.stato = 'sospeso';
               user.sospensioni = (user.sospensioni || 0) + 1;
               showCustomAlert(`Utente ${user.nome} ${user.cognome} sospeso.`, 'warning');
             } else {
               user.stato = 'attivo';
               showCustomAlert(`Utente ${user.nome} ${user.cognome} attivato.`, 'success');
             }
             renderUserTable(); // Ridisegna la tabella e aggiorna le statistiche
           })
           .catch(error => {
             console.error("Errore nell'aggiornamento dello stato utente:", error);
             showCustomAlert(`Errore nell'aggiornamento di ${user.nome}.`, 'danger');
           });
    }
}

// Event listener per la barra di ricerca
if (searchInput) {
    searchInput.addEventListener('input', (e) => {
        searchTerm = e.target.value.toLowerCase();
        renderUserTable();
    });
}

// Event listener per i pulsanti di filtro
if (filterButtons) {
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            filterButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            currentFilter = button.getAttribute('data-filter');
            renderUserTable();
        });
    });
}

// Funzione per mostrare un alert custom
function showCustomAlert(message, type) {
    const alertContainerId = 'customAlertContainer';
    let alertContainer = document.getElementById(alertContainerId);

    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.id = alertContainerId;
        // Applica stili per posizionare l'alert container
        Object.assign(alertContainer.style, {
            position: 'fixed',
            top: '80px', // O altra posizione desiderata
            right: '20px',
            zIndex: '1050', // Assicurati sia sopra altri elementi
            width: 'auto',
            maxWidth: '400px'
        });
        document.body.appendChild(alertContainer);
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.setAttribute('role', 'alert'); // Per accessibilità
    alertDiv.style.marginBottom = '10px'; // Spazio tra alert multipli
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    `;
    alertContainer.appendChild(alertDiv);

    setTimeout(() => {
        if (typeof $ !== 'undefined' && $.fn.alert) {
            $(alertDiv).alert('close');
            setTimeout(() => {
                alertDiv.remove();
                if (alertContainer.children.length === 0) {
                    alertContainer.remove();
                }
            }, 500);
        } else {
            alertDiv.classList.remove('show');
            setTimeout(() => {
                alertDiv.remove();
                if (alertContainer.children.length === 0) {
                    alertContainer.remove();
                }
            }, 150);
        }
    }, 3000); // L'alert scompare dopo 3 secondi
}

// === Inizializzazione ===
// Quando il DOM è pronto, carica i dati degli utenti.
// La tabella e le statistiche verranno renderizzate dalla funzione caricaEProcessaDatiUtenti
// una volta che i dati sono disponibili.
document.addEventListener('DOMContentLoaded', () => {
    // Aggiungiamo un controllo per gli elementi DOM critici prima di procedere
    if (tableBody && searchInput && filterButtons.length > 0 && totalUsersEl && activeUsersEl && suspendedUsersEl && noResultsMessageEl) {
        caricaEProcessaDatiUtenti();
    } else {
        console.error("Alcuni elementi del DOM non sono stati trovati. L'applicazione potrebbe non funzionare correttamente.");
        // Potresti voler mostrare un messaggio di errore più visibile all'utente qui
    }
});