// Variabile globale per i dati dei mezzi, inizializzata come array vuoto
let vehicles = [];

// --- DOM Elements (Elementi del DOM) ---
const vehicleTableBody = document.getElementById('vehicleTableBody');
const searchInput = document.getElementById('searchInput');
const filterType = document.getElementById('filterType');
const filterZone = document.getElementById('filterZone');
const filterParking = document.getElementById('filterParking');
const filterStatus = document.getElementById('filterStatus');
const filterMalfunction = document.getElementById('filterMalfunction');
const resetFiltersBtn = document.getElementById('resetFiltersBtn');
const noResultsMessage = document.getElementById('noResultsMessage');
const addVehicleModalEl = document.getElementById('addVehicleModal');

const statTotalVehicles = document.getElementById('stat-total-vehicles');
const statAvailableVehicles = document.getElementById('stat-available-vehicles');
const statInUseVehicles = document.getElementById('stat-in-use-vehicles');
const statMalfunctioningVehicles = document.getElementById('stat-malfunctioning-vehicles');

// --- Predefined Parking Spots (Lista Parcheggi Predefinita) ---
const uniqueParkingSpotsFromData = [...new Set(vehicles.map(v => v.parking).filter(p => p))];
const additionalParkingSpots = ["Parcheggio A", "Parcheggio B", "Parcheggio C", "Parcheggio D", "Parcheggio E"];
const predefinedParkingSpots = [...new Set([...uniqueParkingSpotsFromData, ...additionalParkingSpots])].sort();

// Funzione per recuperare e processare i dati dall'URL
async function caricaEProcessaDatiMezzi() {
    try {
        const response = await fetch('http://localhost:4567/api/v1.0/mezziAdmin');
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.status}`);
        }
        const datiMezziRaw = await response.json();

        // Mappa i dati grezzi nel formato desiderato e assegnali a datiRichiesti
        vehicles = datiMezziRaw.map(mezzo => {
            return {
                id: mezzo.id,
                type: mezzo.tipo,
                zone: mezzo.zona.nome,
                parking: mezzo.parcheggio.nome,
                battery: mezzo.batteria,
                status: mezzo.stato,
                malfunction: false
            };
        });

        // Una volta che i dati sono caricati e processati, renderizza la tabella
        populateParkingFilter(); // Popola il filtro parcheggi
        sanitizeVehicleData(vehicles);
        updateStats();
        renderTable(vehicles);

    } catch (errore) {
        console.error("Impossibile recuperare i dati dei mezzi:", errore);
        // Potresti voler mostrare un messaggio di errore all'utente qui
        if (noResultsMessageEl) {
            noResultsMessageEl.textContent = "Errore nel caricamento dei dati dei mezzi.";
            noResultsMessageEl.style.display = 'block';
        }
        if (tableBody) tableBody.innerHTML = ''; // Pulisce la tabella in caso di errore
        if (statTotalVehicles) statTotalVehicles.textContent = '0';
        if (statAvailableVehicles) statAvailableVehicles.textContent = '0';
        if (statInUseVehicles) statInUseVehicles.textContent = '0';
        if (statMalfunctioningVehicles) statMalfunctioningVehicles.textContent = '0';
    }
}

// --- Utility Functions (Funzioni di Utilità) ---
function formatStatus(status) {
    return `<span class="status-badge status-${status.replace(/\s+/g, '_')}">${status.replace(/_/g, ' ')}</span>`;
}

function formatType(type) {
    return type.replace('-', ' ').split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
}

// --- Data Sanitization (Sanificazione Dati Iniziale) ---
function sanitizeVehicleData(vehicleList) {
    vehicleList.forEach(vehicle => {
        if (vehicle.status === "NON_DISPONIBILE") {
            vehicle.malfunction = true;
        } else {
            if (vehicle.status === "NON_DISPONIBILE") {
                vehicle.status = "PRELEVABILE";
            }
        }
    });
}

// --- Render Functions (Funzioni di Rendering) ---
function renderTable(filteredVehicles) {
    vehicleTableBody.innerHTML = '';

    if (filteredVehicles.length === 0) {
        noResultsMessage.style.display = 'block';
    } else {
        noResultsMessage.style.display = 'none';
    }

    filteredVehicles.forEach(vehicle => {
        const row = document.createElement('tr');
        row.setAttribute('data-vehicle-id', vehicle.id);

        const batteryDisabled = vehicle.type === 'BICICLETTA' || vehicle.status === "IN_USO" ? 'disabled' : '';
        const parkingSelectDisabled = vehicle.status === "IN_USO" ? 'disabled' : '';
        const batteryValue = vehicle.battery === null ? '' : vehicle.battery;
        const malfunctionCheckboxDisabled = vehicle.status === "IN_USO" ? 'disabled' : '';


        let parkingOptionsHTML = predefinedParkingSpots.map(spot =>
            `<option value="${spot}" ${vehicle.parking === spot ? 'selected' : ''}>${spot}</option>`
        ).join('');

        row.innerHTML = `
                    <td>${vehicle.id}</td>
                    <td>${formatType(vehicle.type)}</td>
                    <td>${vehicle.zone}</td>
                    <td>
                        <select class="form-select form-select-sm" data-field="parking" style="min-width: 160px;" ${parkingSelectDisabled}>
                            ${parkingOptionsHTML}
                        </select>
                    </td>
                    <td>
                        <input type="number" class="form-control form-control-sm-custom text-center" value="${batteryValue}" min="0" max="100" data-field="battery" style="width: 75px;" ${batteryDisabled}>
                    </td>
                    <td class="text-center">
                        <input type="checkbox" class="form-check-input" ${vehicle.malfunction ? 'checked' : ''} data-field="malfunction" id="malfunction-${vehicle.id}" ${malfunctionCheckboxDisabled}>
                        <label for="malfunction-${vehicle.id}" class="ms-1 visually-hidden">Malfunzionamento</label> 
                        <i class="fas ${vehicle.malfunction ? 'fa-exclamation-triangle malfunction-true' : 'fa-check-circle malfunction-false'} ms-1 malfunction-icon"></i>
                    </td>
                    <td data-cell="status">${formatStatus(vehicle.status)}</td>
                    <td>
                        <button class="btn btn-sm btn-primary save-btn" title="Salva Modifiche">
                            <i class="fas fa-save"></i> <span class="d-none d-md-inline">Salva</span>
                        </button>
                    </td>
                `;
        vehicleTableBody.appendChild(row);
    });

    vehicleTableBody.querySelectorAll('.save-btn').forEach(button => {
        button.addEventListener('click', handleSave);
    });

    vehicleTableBody.querySelectorAll('[data-field]').forEach(input => {
        input.addEventListener('change', handleInputChange);
        if (input.type === 'number') {
            input.addEventListener('input', handleInputChange);
        }
    });
}

function updateStats() {
    statTotalVehicles.textContent = vehicles.length;
    statAvailableVehicles.textContent = vehicles.filter(v => v.status === "PRELEVABILE").length;
    statInUseVehicles.textContent = vehicles.filter(v => v.status === "IN_USO" && !v.malfunction).length;
    statMalfunctioningVehicles.textContent = vehicles.filter(v => v.malfunction).length;
}

// --- Event Handlers (Gestori di Eventi) ---
async function handleSave(event) {
    const button = event.currentTarget;
    const row = button.closest('tr');
    const vehicleId = parseInt(row.dataset.vehicleId);
    const mezzo = vehicles.find(m => m.id === vehicleId);

    const payload = {
        id: vehicleId,
        parcheggio: {
            nome: mezzo.parking,
        },
        batteria: mezzo.battery,
        stato: mezzo.status
    }
    const jsonPayload = JSON.stringify(payload);

    console.log("JSON da inviare al server:", jsonPayload); // Mostra il JSON nella console

    // Disabilita il bottone per prevenire click multipli
    button.disabled = true;
    button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Salvo...';


    // 4. Invia il JSON al server tramite Fetch API
    try {
        const url = 'http://localhost:4567/api/v1.0/updateMezzo';
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: jsonPayload,
        });

        if (!response.ok) {
            // Se la risposta non è OK, lancia un errore per entrare nel blocco catch
            const errorData = await response.json().catch(() => ({ message: 'Errore sconosciuto dal server' }));
            throw new Error(`Errore ${response.status}: ${errorData.message}`);
        }

        const result = await response.json(); // Opzionale: se il server risponde con dati
        console.log("Risposta dal server:", result);

        // Se l'operazione ha successo
        button.innerHTML = '<i class="fas fa-check"></i> Salvato';
        button.classList.remove('btn-primary', 'btn-danger');
        button.classList.add('btn-success');

        // Aggiorna lo stato nel frontend dopo il salvataggio
        // Questo è già gestito da handleInputChange, ma è buona norma risincronizzare
        const vehicleInState = vehicles.find(v => v.id === vehicleId);
        if(vehicleInState) {
            Object.assign(vehicleInState, payload); // Aggiorna l'oggetto 'vehicles'
        }
        sanitizeVehicleData(vehicles);
        applyFilters(); // Riapplica i filtri per aggiornare la vista
        updateStats(); // Aggiorna le statistiche


    } catch (error) {
        console.error("Errore durante il salvataggio:", error);
        button.innerHTML = '<i class="fas fa-times"></i> Errore!';
        button.classList.remove('btn-primary', 'btn-success');
        button.classList.add('btn-danger');

    } finally {
        // Riattiva il bottone dopo un breve ritardo, sia in caso di successo che di errore
        setTimeout(() => {
            button.disabled = false;
            button.innerHTML = '<i class="fas fa-save"></i> <span class="d-none d-md-inline">Salva</span>';
            button.classList.remove('btn-success', 'btn-danger');
            button.classList.add('btn-primary');
        }, 3000); // 3 secondi di feedback
    }
}

function handleInputChange(event) {
    const input = event.target;
    if (input.disabled) {
        return;
    }
    const row = input.closest('tr');
    const vehicleId = parseInt(row.dataset.vehicleId);
    const field = input.dataset.field;
    let value = input.type === 'checkbox' ? input.checked : input.value;

    const vehicle = vehicles.find(v => v.id === vehicleId);
    if (vehicle) {
        if (field === 'malfunction') {
            vehicle[field] = value;

            if (vehicle.malfunction === true) {
                vehicle.status = "NON_DISPONIBILE";
            } else {
                if (vehicle.status === "NON_DISPONIBILE") {
                    vehicle.status = "PRELEVABILE";
                }
            }

            const icon = row.querySelector('.malfunction-icon');
            if (icon) {
                icon.className = `fas ${vehicle.malfunction ? 'fa-exclamation-triangle malfunction-true' : 'fa-check-circle malfunction-false'} ms-1 malfunction-icon`;
            }
            const statusCell = row.querySelector('td[data-cell="status"]');
            if (statusCell) {
                statusCell.innerHTML = formatStatus(vehicle.status);
            }

        } else if (field === 'battery') {
            value = value === '' ? null : parseInt(value);
            if (value !== null && (isNaN(value) || value < 0 || value > 100)) {
                input.classList.add('is-invalid');
                return;
            } else {
                input.classList.remove('is-invalid');
            }
            vehicle[field] = value;
        } else {
            vehicle[field] = value;
        }
        updateStats();

        if (field === 'malfunction') {
            applyFilters();
        }
    }
}

function applyFilters() {
    const searchTerm = searchInput.value.toLowerCase().trim();
    const selectedType = filterType.value;
    const selectedZone = filterZone.value;
    const selectedParking = filterParking.value;
    const selectedStatus = filterStatus.value;
    const selectedMalfunction = filterMalfunction.value;

    const filteredVehicles = vehicles.filter(vehicle => {
        const matchesSearch = searchTerm === '' ||
            vehicle.id.toString().includes(searchTerm) ||
            formatType(vehicle.type).toLowerCase().includes(searchTerm) ||
            vehicle.zone.toLowerCase().includes(searchTerm) ||
            vehicle.parking.toLowerCase().includes(searchTerm);

        const matchesType = selectedType === '' || vehicle.type === selectedType;
        const matchesZone = selectedZone === '' || vehicle.zone === selectedZone;
        const matchesParking = selectedParking === '' || vehicle.parking === selectedParking;
        const matchesStatus = selectedStatus === '' || vehicle.status === selectedStatus;
        const matchesMalfunction = selectedMalfunction === '' || vehicle.malfunction.toString() === selectedMalfunction;

        return matchesSearch && matchesType && matchesZone && matchesParking && matchesStatus && matchesMalfunction;
    });
    renderTable(filteredVehicles);
}

function populateParkingFilter() {
    filterParking.innerHTML = '<option value="">Tutti i Parcheggi</option>'; // Opzione default
    predefinedParkingSpots.forEach(spot => {
        const option = document.createElement('option');
        option.value = spot;
        option.textContent = spot;
        filterParking.appendChild(option);
    });
}

//funzione per aggiungere un nuovo veicolo
async function handleNewVehicle(){
    const messageDiv = document.getElementById("vehicleMessage");
    messageDiv.innerHTML = ""; // Pulisce messaggi precedenti

    // Elementi del DOM
    const vehicleOptions = document.querySelectorAll('.vehicle-option');
    const hiddenVehicleTypeInput = document.getElementById('vehicle-type');
    const batteryLevelContainer = document.getElementById('battery-level-container');
    const batteryLevelInput = document.getElementById('battery-level');
    const addVehicleModalEl = document.getElementById('addVehicleModal');
    const addVehicleForm = document.getElementById('add-vehicle-form');
    const vehicleTypeError = document.getElementById('vehicle-type-error');
    const saveVehicleBtn = document.getElementById('save-vehicle-btn');
    const spinner = saveVehicleBtn.querySelector('.spinner-border');

    const addVehicleModal = new bootstrap.Modal(addVehicleModalEl);

    const handleBatteryField = () => {
        const selectedType = hiddenVehicleTypeInput.value;
        if (selectedType === 'BICICLETTA') {
            batteryLevelInput.disabled = true;
            batteryLevelInput.value = '';
            batteryLevelInput.required = false;
            batteryLevelContainer.style.opacity = '0.5';
        } else {
            batteryLevelInput.disabled = false;
            batteryLevelInput.required = true;
            batteryLevelContainer.style.opacity = '1';
        }
    };

    vehicleOptions.forEach(option => {
        option.addEventListener('click', () => {
            vehicleOptions.forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');
            hiddenVehicleTypeInput.value = option.dataset.value;
            vehicleTypeError.classList.add('d-none');
            handleBatteryField();
        });
    });

    batteryLevelInput.addEventListener('input', () => {
        if (parseInt(batteryLevelInput.value, 10) > 100) {
            batteryLevelInput.value = 100;
        }
    });

    addVehicleForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        event.stopPropagation();

        if (!hiddenVehicleTypeInput.value) {
            vehicleTypeError.classList.remove('d-none');
        } else {
            vehicleTypeError.classList.add('d-none');
        }

        if (!addVehicleForm.checkValidity() || !hiddenVehicleTypeInput.value) {
            addVehicleForm.classList.add('was-validated');
            return;
        }

        const vehicleData = {
            type: hiddenVehicleTypeInput.value,
            parking: document.getElementById('parking').value,
            imei: document.getElementById('imei').value
        };
        if (batteryLevelInput.required) {
            vehicleData.battery = parseInt(batteryLevelInput.value, 10);
        }

        console.log(vehicleData);

        // Mostra lo spinner e disabilita il pulsante
        spinner.classList.remove('d-none');
        saveVehicleBtn.disabled = true;

        try {
            // Sostituisci con l'URL del tuo endpoint API
            const response = await fetch('http://localhost:4567/api/v1.0/addMezzo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(vehicleData)
            });

            if (response.ok) {
                messageDiv.innerHTML = `<div class="alert alert-success">Mezzo aggiunto correttamente</div>`;
                addVehicleModal.hide();
                caricaEProcessaDatiMezzi();
            } else {
                const errorData = await response.json();
                messageDiv.innerHTML = `<div class="alert alert-danger">Errore, Salvataggio fallito: ${errorData.message || response.statusText}</div>`;
            }
        } catch (error) {
            console.error('Errore di rete:', error);
            messageDiv.innerHTML = `<div class="alert alert-danger">Errore di Rete, Impossibile connettersi al server. Riprova più tardi.</div>`;
        } finally {
            // Nasconde lo spinner e riabilita il pulsante
            spinner.classList.add('d-none');
            saveVehicleBtn.disabled = false;
        }
    });

    addVehicleModalEl.addEventListener('hidden.bs.modal', function () {
        addVehicleForm.reset();
        addVehicleForm.classList.remove('was-validated');
        vehicleOptions.forEach(opt => opt.classList.remove('selected'));
        hiddenVehicleTypeInput.value = '';
        vehicleTypeError.classList.add('d-none');
        handleBatteryField();
    });

    handleBatteryField();
}

// --- Initial Setup (Configurazione Iniziale) ---
document.addEventListener('DOMContentLoaded', () => {

    caricaEProcessaDatiMezzi();

    searchInput.addEventListener('input', applyFilters);
    filterType.addEventListener('change', applyFilters);
    filterZone.addEventListener('change', applyFilters);
    filterParking.addEventListener('change', applyFilters); // Aggiungi event listener per il nuovo filtro
    filterStatus.addEventListener('change', applyFilters);
    filterMalfunction.addEventListener('change', applyFilters);

    resetFiltersBtn.addEventListener('click', () => {
        searchInput.value = '';
        filterType.value = '';
        filterZone.value = '';
        filterParking.value = ''; // Resetta anche il filtro parcheggio
        filterStatus.value = '';
        filterMalfunction.value = '';
        applyFilters();
    });
});

