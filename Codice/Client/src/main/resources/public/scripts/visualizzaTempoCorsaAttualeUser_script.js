document.addEventListener("DOMContentLoaded", function () {
    const tempoElement = document.getElementById("tempoTrascorso");
    if (!tempoElement) return;

    // Data partenza dal server (formato es: "2025-06-23 15:30:00")
    const dataPartenzaString = "{{corsa.dataPartenza}}";

    // Parsing in formato valido per JavaScript
    const dataPartenza = new Date(dataPartenzaString.replace(" ", "T"));

    function aggiornaTempoTrascorso() {
        const oraAttuale = new Date();
        const differenzaMs = oraAttuale - dataPartenza;

        const minuti = Math.floor(differenzaMs / 60000);
        const ore = Math.floor(minuti / 60);
        const minutiFinali = minuti % 60;

        if (ore > 0) {
            tempoElement.textContent = `${ore}h ${minutiFinali}min`;
        } else {
            tempoElement.textContent = `${minutiFinali} min`;
        }
    }

    // Avvia aggiornamento ogni secondo
    aggiornaTempoTrascorso(); // Prima volta subito
    setInterval(aggiornaTempoTrascorso, 1000);
});