// --- Sample Trip Data ---
let trips = [];

// --- DOM Elements ---
const tripsTableBody = document.getElementById('tripsTableBody');
const filterSearchTrips = document.getElementById('searchInput');
const filterVehicleTypeTrips = document.getElementById('filterVehicleTypeTrips');
const filterStartParkingTrips = document.getElementById('filterStartParkingTrips');
const filterEndParkingTrips = document.getElementById('filterEndParkingTrips');
const resetTripsFiltersBtn = document.getElementById('reset-filters-button');
const noTripsMessage = document.getElementById('noTripsMessage');

const statTotalTrips = document.getElementById('stat-total-trips');
const statTotalRevenue = document.getElementById('stat-total-revenue');
const statAvgDuration = document.getElementById('stat-avg-duration');

// Funzione per recuperare e processare i dati dall'URL
async function caricaEProcessaDatiCorsa() {
    try {
        const response = await fetch('http://localhost:4567/api/v1.0/corseAdmin');
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.status}`);
        }
        const datiParcheggiRaw = await response.json();

        // Mappa i dati grezzi nel formato desiderato e assegnali a datiRichiesti
        trips = datiParcheggiRaw.map(corsa => {

            const datePartenza = new Date(corsa.dataPartenza);
            const dateArrivo = new Date(corsa.dataArrivo);
            const diffMs = dateArrivo - datePartenza;
            const durata = diffMs / (1000 * 60);

            return {
                id: corsa.id,
                userName: corsa.utente.email,
                vehicleId: corsa.mezzo.id,
                vehicleType: corsa.mezzo.tipo,
                durationMinutes: durata,
                costEur: corsa.importo,
                startParking: corsa.partenza.nome,
                endParking: corsa.arrivo.nome,
                date: corsa.dataPartenza,
            };
        });

        // Una volta che i dati sono caricati e processati, renderizza la tabella
        populateParkingFilters();
        updateTripStats(trips);
        renderTripsTable(trips);

    } catch (errore) {
        console.error("Impossibile recuperare i dati delle corse:", errore);
        // Potresti voler mostrare un messaggio di errore all'utente qui
        if (noTripsMessage) {
            noTripsMessage.textContent = "Errore nel caricamento dei dati delle corse.";
            noTripsMessage.style.display = 'block';
        }
        if (tableBody) tableBody.innerHTML = ''; // Pulisce la tabella in caso di errore
        if (statTotalTrips) statTotalTrips.textContent = '0';
        if (statTotalRevenue) statTotalRevenue.textContent = '0';
        if (statAvgDuration) statAvgDuration.textContent = '0';
    }
}

// --- Extract unique parking spots for filters ---
const allParkingSpots = [...new Set(trips.flatMap(trip => [trip.startParking, trip.endParking]))].sort();

// --- Utility Functions ---
function formatCurrency(amount) {
    return `€${amount.toFixed(2)}`;
}

// Riportata alla versione precedente che restituisce solo testo formattato
function formatVehicleType(type) {
    if (!type) return 'N/D';
    return type.replace('-', ' ').split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
}

function formatDate(dateString) {
    if (!dateString) return 'N/D';
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
    try {
        return new Date(dateString).toLocaleDateString('it-IT', options);
    } catch (e) {
        return dateString;
    }
}

// --- Render Functions ---
function renderTripsTable(filteredTrips) {
    tripsTableBody.innerHTML = '';

    if (filteredTrips.length === 0) {
        noTripsMessage.style.display = 'block';
    } else {
        noTripsMessage.style.display = 'none';
    }

    filteredTrips.forEach(trip => {
        const row = document.createElement('tr');
        // Rimozione delle classi fw-medium, text-end, text-center e chiamata a formatVehicleType
        row.innerHTML = `
                    <td>${trip.id}</td>
                    <td>${trip.userName || trip.userId}</td>
                    <td>${trip.vehicleId}</td>
                    <td>${formatVehicleType(trip.vehicleType)}</td> 
                    <td>${trip.durationMinutes} min</td>
                    <td>${formatCurrency(trip.costEur)}</td>
                    <td>${trip.startParking}</td>
                    <td>${trip.endParking}</td>
                    <td>${formatDate(trip.date)}</td>
                `;
        tripsTableBody.appendChild(row);
    });
}

function updateTripStats(currentTrips) {
    const totalTrips = currentTrips.length;
    statTotalTrips.textContent = totalTrips;

    const totalRevenue = currentTrips.reduce((sum, trip) => sum + trip.costEur, 0);
    statTotalRevenue.textContent = formatCurrency(totalRevenue);

    const totalDuration = currentTrips.reduce((sum, trip) => sum + trip.durationMinutes, 0);
    const avgDuration = totalTrips > 0 ? (totalDuration / totalTrips) : 0;
    statAvgDuration.textContent = `${avgDuration.toFixed(0)} min`;
}

// --- Event Handlers ---
function applyTripFilters() {
    const searchTerm = filterSearchTrips.value.toLowerCase().trim();
    const selectedVehicleType = filterVehicleTypeTrips.value;
    const selectedStartParking = filterStartParkingTrips.value;
    const selectedEndParking = filterEndParkingTrips.value;

    const filteredTrips = trips.filter(trip => {
        const matchesSearch = searchTerm === '' ||
            trip.id.toLowerCase().includes(searchTerm) ||
            (trip.userName && trip.userName.toLowerCase().includes(searchTerm)) ||
            trip.vehicleId.toLowerCase().includes(searchTerm) ||
            trip.vehicleType.toLowerCase().includes(searchTerm) ||
            trip.startParking.toLowerCase().includes(searchTerm) ||
            trip.endParking.toLowerCase().includes(searchTerm);

        const matchesVehicleType = selectedVehicleType === '' || trip.vehicleType === selectedVehicleType;
        const matchesStartParking = selectedStartParking === '' || trip.startParking === selectedStartParking;
        const matchesEndParking = selectedEndParking === '' || trip.endParking === selectedEndParking;

        return matchesSearch && matchesVehicleType && matchesStartParking && matchesEndParking;
    });
    renderTripsTable(filteredTrips);
    updateTripStats(filteredTrips);
}

function populateParkingFilters() {
    //filterStartParkingTrips.innerHTML = '<option value="">Tutti i Parcheggi Partenza</option>';
    allParkingSpots.forEach(spot => {
        const option = document.createElement('option');
        option.value = spot;
        option.textContent = spot;
        filterStartParkingTrips.appendChild(option);
    });

    //filterEndParkingTrips.innerHTML = '<option value="">Tutti i Parcheggi Arrivo</option>';
    allParkingSpots.forEach(spot => {
        const option = document.createElement('option');
        option.value = spot;
        option.textContent = spot;
        filterEndParkingTrips.appendChild(option);
    });
}

// --- Initial Setup ---
document.addEventListener('DOMContentLoaded', () => {

    caricaEProcessaDatiCorsa();

    filterSearchTrips.addEventListener('input', applyTripFilters);
    filterVehicleTypeTrips.addEventListener('change', applyTripFilters);
    filterStartParkingTrips.addEventListener('change', applyTripFilters);
    filterEndParkingTrips.addEventListener('change', applyTripFilters);

    resetTripsFiltersBtn.addEventListener('click', () => {
        filterSearchTrips.value = '';
        filterVehicleTypeTrips.value = '';
        filterStartParkingTrips.value = '';
        filterEndParkingTrips.value = '';
        applyTripFilters();
    });
});