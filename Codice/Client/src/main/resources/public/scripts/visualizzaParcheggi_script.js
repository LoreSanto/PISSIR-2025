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
                availableSpots: parcheggio.postiOccupati,
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
        const occupancyPercentage = parking.totalSpots > 0 ? (parking.availableSpots / parking.totalSpots) * 100 : 0;
        const occupancyText = `${parking.availableSpots} / ${parking.totalSpots}`;

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