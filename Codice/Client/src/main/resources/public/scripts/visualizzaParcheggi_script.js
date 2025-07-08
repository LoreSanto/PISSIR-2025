let parkings = [];

// --- DOM Elements ---
const parkingTableBody = document.getElementById('parkingTableBody');
const filterSearchParking = document.getElementById('searchInput');
const filterZoneParking = document.getElementById('filterZoneParking');
const resetParkingFiltersBtn = document.getElementById('resetFiltersBtn');
const noParkingsMessage = document.getElementById('noParkingsMessage');

const statTotalParkings = document.getElementById('stat-total-parkings');
const statTotalSpots = document.getElementById('stat-total-spots');
const statAvailableSpots = document.getElementById('stat-available-spots');

// Funzione per recuperare e processare i dati dall'URL
async function caricaEProcessaDatiParcheggio() {
    try {
        const response = await fetch('http://localhost:4567/api/v1.0/parcheggiAdmin');
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.status}`);
        }
        const datiParcheggiRaw = await response.json();

        // Mappa i dati grezzi nel formato desiderato e assegnali a datiRichiesti
        parkings = datiParcheggiRaw.map(parcheggio => {
            return {
                name: parcheggio.nome,
                zone: parcheggio.zona.nome,
                totalSpots: parcheggio.capienzaMax,
                availableSpots: parcheggio.capienzaMax - parcheggio.postiOccupati,
            };
        });

        // Una volta che i dati sono caricati e processati, renderizza la tabella
        updateParkingStats();
        renderParkingTable(parkings);

    } catch (errore) {
        console.error("Impossibile recuperare i dati dei parcheggi:", errore);
        // Potresti voler mostrare un messaggio di errore all'utente qui
        if (noResultsMessageEl) {
            noResultsMessageEl.textContent = "Errore nel caricamento dei dati dei parcheggi.";
            noResultsMessageEl.style.display = 'block';
        }
        if (tableBody) tableBody.innerHTML = ''; // Pulisce la tabella in caso di errore
        if (statTotalParkings) statTotalParkings.textContent = '0';
        if (statTotalSpots) statTotalSpots.textContent = '0';
        if (statAvailableSpots) statAvailableSpots.textContent = '0';
    }
}

// --- Render Functions ---
function renderParkingTable(filteredParkings) {
    parkingTableBody.innerHTML = '';

    if (filteredParkings.length === 0) {
        noParkingsMessage.style.display = 'block';
    } else {
        noParkingsMessage.style.display = 'none';
    }

    filteredParkings.forEach(parking => {
        const row = document.createElement('tr');
        const occupiedSpots = parking.totalSpots - parking.availableSpots;
        const occupancyPercentage = parking.totalSpots > 0 ? (occupiedSpots / parking.totalSpots) * 100 : 0;
        const occupancyText = `${occupiedSpots} / ${parking.totalSpots}`;
        row.innerHTML = `
                    <td>${parking.name}</td>
                    <td>${parking.zone}</td>
                    <td>${parking.totalSpots}</td>
                    <td>${parking.availableSpots}</td>
                    <td>
                        <div class="availability-bar-container" title="${occupancyText} (${occupancyPercentage.toFixed(1)}% disponibili)">
                            <div class="availability-bar-text">${occupancyText}</div>
                            <div class="availability-bar" style="width: ${occupancyPercentage}%;"></div>
                        </div>
                    </td>
                `;
        parkingTableBody.appendChild(row);
    });
}

function updateParkingStats() {
    statTotalParkings.textContent = parkings.length;
    const totalSpotsAll = parkings.reduce((sum, p) => sum + p.totalSpots, 0);
    const availableSpotsAll = parkings.reduce((sum, p) => sum + p.availableSpots, 0);
    statTotalSpots.textContent = totalSpotsAll;
    statAvailableSpots.textContent = availableSpotsAll;
}

// --- Event Handlers ---
function applyParkingFilters() {
    const searchTerm = filterSearchParking.value.toLowerCase().trim();
    const selectedZone = filterZoneParking.value;

    const filteredParkings = parkings.filter(parking => {
        const matchesSearch = searchTerm === '' ||
            parking.name.toLowerCase().includes(searchTerm);

        const matchesZone = selectedZone === '' || parking.zone === selectedZone;

        return matchesSearch && matchesZone;
    });
    renderParkingTable(filteredParkings);
}

//Gestisce l'aggiunta di un nuovo parcheggio.Raccoglie i dati dal modal, li valida e li invia al server tramite una richiesta POST.
async function addParkingSpot() {
    const messageDiv = document.getElementById("parkMessage");
    messageDiv.innerHTML = ""; // Pulisce messaggi precedenti

    // 1. Riferimenti agli elementi del form nel modal
    const nameInput = document.getElementById('parking-name');
    const capacityInput = document.getElementById('max-capacity');
    const zoneInput = document.getElementById('zone');
    const addButton = document.getElementById('addParkingButton');

    // 2. Acquisizione e pulizia dei valori
    const name = nameInput.value.trim();
    const maxCapacity = capacityInput.value;
    const zone = zoneInput.value;

    // 3. Validazione base dei dati inseriti
    if (!name) {
        messageDiv.innerHTML = `<div class="alert alert-danger">Per favore, inserisci il nome del parcheggio.</div>`;
        nameInput.focus();
        return;
    }
    if (!maxCapacity || parseInt(maxCapacity, 10) <= 0) {
        messageDiv.innerHTML = `<div class="alert alert-danger">Per favore, inserisci una capienza massima valida (un numero intero positivo).</div>`;
        capacityInput.focus();
        return;
    }
    if (!zone) {
        messageDiv.innerHTML = `<div class="alert alert-danger">Per favore, seleziona una zona.</div>`;
        zoneInput.focus();
        return;
    }

    // 4. Creazione dell'oggetto JSON da inviare al server
    const parkingData = {
        nome: name,
        capienzaMassima: parseInt(maxCapacity, 10),
        zona: zone
    };

    // Disabilita il pulsante per prevenire invii multipli e mostra uno spinner per feedback
    addButton.disabled = true;
    addButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Salvataggio...';

    try {
        // 5. Invia i dati al server con l'API Fetch
        // IMPORTANTE: Sostituisci '/api/parcheggi' con l'URL corretto del tuo endpoint sul server!
        const response = await fetch('http://localhost:4567/api/v1.0/addParcheggio', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(parkingData),
        });

        // Controlla se la risposta del server non è andata a buon fine (es. errore 400 o 500)
        if (!response.ok) {
            // Prova a leggere un messaggio di errore dal corpo della risposta JSON
            const errorData = await response.json().catch(() => ({ message: 'Il server ha risposto con un errore non in formato JSON.' }));
            throw new Error(errorData.message || `Errore HTTP: ${response.status}`);
        }

        // 6. Gestione della risposta in caso di successo
        const result = await response.json();
        console.log('Parcheggio aggiunto con successo:', result);

        messageDiv.innerHTML = `<div class="alert alert-success">Parcheggio aggiunto correttamente</div>`;
        caricaEProcessaDatiParcheggio();

        // Pulisce i campi del form per il prossimo utilizzo
        document.getElementById('addParkingModal').querySelector('form').reset();


    } catch (error) {
        // 7. Gestione degli errori (es. problemi di rete, errori del server)
        console.error("Errore durante l'aggiunta del parcheggio:", error);
        messageDiv.innerHTML = `<div class="alert alert-danger">Si è verificato un errore durante il salvataggio: ${error.message}</div>`;
    } finally {
        // Riabilita il pulsante in ogni caso (successo o errore) e ripristina il testo
        addButton.disabled = false;
        addButton.innerHTML = 'Salva Parcheggio';
    }
}

// --- Initial Setup ---
document.addEventListener('DOMContentLoaded', () => {

    caricaEProcessaDatiParcheggio();

    filterSearchParking.addEventListener('input', applyParkingFilters);
    filterZoneParking.addEventListener('change', applyParkingFilters);

    resetParkingFiltersBtn.addEventListener('click', () => {
        filterSearchParking.value = '';
        filterZoneParking.value = '';
        applyParkingFilters();
    });
});