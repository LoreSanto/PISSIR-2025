document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const errorDiv = document.getElementById('loginError');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Reset UI errori
        errorDiv.style.display = 'none';
        emailInput.classList.remove('is-invalid');
        passwordInput.classList.remove('is-invalid');

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        try {
            const response = await fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (!response.ok) {
                // Mostra errore
                showLoginError(data.error || "Errore sconosciuto.");
                return;
            }

            if (data.redirect) {
                // Login ok → redirect
                window.location.href = data.redirect;
            } else {
                // Nessun redirect (caso raro)
                showLoginError("Errore imprevisto, riprova.");
            }

        } catch (error) {
            console.error("Errore nella richiesta:", error);
            showLoginError("Errore di rete o del server, riprova più tardi.");
        }
    });

    function showLoginError(message) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        emailInput.classList.add('is-invalid');
        passwordInput.classList.add('is-invalid');
    }
});