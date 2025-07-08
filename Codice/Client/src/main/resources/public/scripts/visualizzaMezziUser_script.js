
let vehicles = [];

const vehicleListComponent = document.getElementById('vehicle-list');
const filterTypeButtonsContainer = document.getElementById('filter-type-buttons');
const filterZoneOptionsContainer = document.getElementById('filter-zone-options');
const filterParkingOptionsContainer = document.getElementById('filter-parking-options');
const filterZoneButton = document.getElementById('filter-zone-button');
const filterParkingButton = document.getElementById('filter-parking-button');
const resetFiltersButton = document.getElementById('reset-filters-button');

const noResultsComponent = document.getElementById('no-results');

let currentFilters = {
    type: 'all',
    zone: 'all',
    parking: 'all'
};

async function caricaEProcessaMezzi() {
    try {
        const response = await fetch('http://localhost:4567/api/v1.0/mezziUser');
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.status}`);
        }
        const datiMezziRaw = await response.json();

        // Mappa i dati grezzi nel formato desiderato e assegnali a datiRichiesti
        vehicles = datiMezziRaw.map(mezzo => {
            return {
                id: mezzo.id,
                type: mezzo.tipo,
                location: mezzo.zona.nome,
                parking: mezzo.parcheggio.nome,
                battery: mezzo.batteria,
                available: true
            };
        });

        initializeFilters(vehicles);
        renderVehicles(vehicles);

    } catch (errore) {
        console.error("Impossibile recuperare i dati dei mezzi:", errore);
        // Potresti voler mostrare un messaggio di errore all'utente qui
        if (noResultsComponent) {
            noResultsComponent.textContent = "Errore nel caricamento dei dati dei mezzi.";
            noResultsComponent.style.display = 'block';
        }
    }
}

function populateDropdown(items, container, filterKey, buttonElement, defaultText, predefinedOptions = null) {
    container.innerHTML = '';

    const allOption = document.createElement('li');
    allOption.innerHTML = `<a class="dropdown-item" href="#" data-filter-${filterKey}="all">${defaultText}</a>`;
    container.appendChild(allOption);

    let uniqueItems;
    if (predefinedOptions) {
        uniqueItems = predefinedOptions.sort();
    } else {
        uniqueItems = [...new Set(items.map(item => item[filterKey]))].sort();
    }

    uniqueItems.forEach(itemValue => {
        const listItem = document.createElement('li');
        listItem.innerHTML = `<a class="dropdown-item" href="#" data-filter-${filterKey}="${itemValue}">${itemValue}</a>`;
        container.appendChild(listItem);
    });

    container.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', (event) => {
            event.preventDefault();
            const selectedValue = event.target.dataset[`filter${filterKey.charAt(0).toUpperCase() + filterKey.slice(1)}`];
            currentFilters[filterKey] = selectedValue;
            buttonElement.textContent = selectedValue === 'all' ? defaultText : event.target.textContent;
            renderVehicles(items);
        });
    });
}

function initializeFilters(vehicles) {
    const zoneList = ["Centro", "Nord", "Sud", "Est", "Ovest"];

    const allAvailableZones = [...new Set([...zoneList, ...vehicles.map(v => v.location)])].sort();

    populateDropdown(vehicles, filterZoneOptionsContainer, 'zone', filterZoneButton, 'Tutte le Zone', allAvailableZones);
    populateDropdown(vehicles, filterParkingOptionsContainer, 'parking', filterParkingButton, 'Tutti i Parcheggi');
}

function renderVehicles(vehicles) {
    vehicleListComponent.innerHTML = '';

    const filteredVehicles = vehicles.filter(vehicle => {
        const typeMatch = currentFilters.type === 'all' || vehicle.type === currentFilters.type;
        const zoneMatch = currentFilters.zone === 'all' || vehicle.location === currentFilters.zone;
        const parkingMatch = currentFilters.parking === 'all' || vehicle.parking === currentFilters.parking;
        return typeMatch && zoneMatch && parkingMatch;
    });

    if (filteredVehicles.length === 0) {
        noResultsComponent.classList.remove('d-none');
    } else {
        noResultsComponent.classList.add('d-none');
    }

    filteredVehicles.forEach(vehicle => {
        const col = document.createElement('div');
        col.className = 'col';

        const card = document.createElement('div');
        card.className = 'card h-100 vehicle-item d-flex flex-column';
        card.dataset.type = vehicle.type;
        card.dataset.zone = vehicle.location;
        card.dataset.parking = vehicle.parking;

        let batteryInfo = '<p></p>';
        if (vehicle.battery !== -1) {
            batteryInfo = `<p class="small text-muted mb-1"><i class="fas fa-battery-full me-1 text-success"></i>Batteria: ${vehicle.battery}%</p>`;
        }

        let rentButtonDisabled = !vehicle.available ? 'disabled' : '';
        let rentButtonClasses = vehicle.available ? 'rent-button' : 'btn-secondary disabled';

        //modifico per eseguire la post da Java al posto che qui nel JS
        card.innerHTML = `
                <form action="/noleggioMezzo" method="POST" class="selection-card">
                    
                    <img src="${renderImage(vehicle)}" alt="${vehicle.name}" class="vehicle-image card-img-top">
                    <div class="card-body d-flex flex-column flex-grow-1 p-3">
                        <div>
                            <h3 class="h6 fw-semibold text-dark mb-1">
                                <i class="fas ${getIconForType(vehicle.type)} me-1"></i> ${vehicle.type}
                            </h3>
                            <p class="small text-muted mb-1">
                                <i class="fas fa-map-marker-alt me-1 text-primary"></i>Zona: ${vehicle.location}
                            </p>
                            <p class="small text-muted mb-1">
                                <i class="fas fa-parking me-1 text-warning"></i> Parcheggio: ${vehicle.parking}
                            </p>
                            ${batteryInfo}
                        </div>

                        <!-- Campi nascosti con i dati del veicolo -->
                        <input type="hidden" id="id" name="id" value="${vehicle.id}">
                        <input type="hidden" id="tipo" name="tipo" value="${vehicle.type}">
                        <input type="hidden" id="zona" name="zona" value="${vehicle.location}">
                        <input type="hidden" id="parcheggio" name="parcheggio" value="${vehicle.parking}">
                        <input type="hidden" id="batteria" name="batteria" value="${vehicle.battery}">
                        <!--Va poi recuperata nella post anche la mail dell'utente che è nella sessione dell'utente-->
                        <button class="btn ${rentButtonClasses} w-100 fw-bold py-2 small" ${rentButtonDisabled}>
                            Noleggia Ora
                        </button>
                    </div>
                    
                </form>
                `;
        col.appendChild(card);
        vehicleListComponent.appendChild(col);
    });
}

function renderImage(vehicle) {
    switch (vehicle.type) {
        case 'BICICLETTA':
            return 'BICICLETTA.jpg';
        case 'BICI_ELETTRICA':
            return 'BICI_ELETTRICA.jpg';
        case 'MONOPATTINO':
            return 'MONOPATTINO.jpg';
        default:
            return 'https://placehold.co/600x400/E0E0E0/757575?text=Mezzo+Non+Disponibile';
    }
}

function getIconForType(type) {
    switch (type) {
        case 'BICICLETTA': return 'fas fa-bicycle';
        case 'BICI_ELETTRICA': return 'fas fa-bolt';
        case 'MONOPATTINO': return 'pi pi-scooter';
        default: return 'fas fa-circle';
    }
}

filterTypeButtonsContainer.addEventListener('click', (event) => {
    const targetButton = event.target.closest('button');
    if (targetButton && targetButton.dataset.filterType) {
        filterTypeButtonsContainer.querySelectorAll('button').forEach(btn => {
            btn.classList.remove('active');
        });
        targetButton.classList.add('active');
        currentFilters.type = targetButton.dataset.filterType;
        renderVehicles(vehicles);
    }
});

resetFiltersButton.addEventListener('click', () => {
    // Reset type filter buttons
    filterTypeButtonsContainer.querySelectorAll('button').forEach(btn => {
        btn.classList.remove('active');
    });
    filterTypeButtonsContainer.querySelector('button[data-filter-type="all"]').classList.add('active');

    // Reset dropdowns
    filterZoneButton.textContent = 'Tutte le Zone';
    filterParkingButton.textContent = 'Tutti i Parcheggi';

    // Reset current filters object
    currentFilters = {
        type: 'all',
        zone: 'all',
        parking: 'all'
    };
    renderVehicles(vehicles);
});

// Initial setup
document.addEventListener('DOMContentLoaded', () => {
    caricaEProcessaMezzi();
});

