// ======== INITIALISATION ========
document.addEventListener('DOMContentLoaded', function () {
    // Récupération références des formulaires
    const registerForm = document.getElementById('formRegister');
    const loginForm = document.getElementById('formLogin');

    // Gestionnaire formulaire d'inscription
    if (registerForm) {
        //Installation gestionnaire d'événements 'submit'
        registerForm.addEventListener('submit', async function (e) {
            // Empêche le rechargement de la page
            e.preventDefault();
            // Création objet à partir du formulaire
            const formData = {
                userName: registerForm.querySelector('input[name="username"]').value.trim(),
                email: registerForm.querySelector('input[name="email"]').value.trim(),
                password: registerForm.querySelector('input[name="password"]').value.trim(),
                firstName: registerForm.querySelector('input[name="firstname"]').value.trim(),
                lastName: registerForm.querySelector('input[name="lastname"]').value.trim(),
                age: parseInt(registerForm.querySelector('input[name="age"]').value),
                profilePicture: registerForm.querySelector('input[name="profilepicture"]').value.trim()
            };

            const result = await APIService.register(formData);

            // Cas de succès
            if (result.success) {
                Utils.message('Inscription Réussi!', 'success');

                // Nettoyage formulaire
                registerForm.reset();

                // Rédirection vers page de connexion
                // Délai augmenter pour que l'utilisateur puisse lire le message
                Utils.redirectTo('login.html', 2000);
            }
            // Cas d'échec
            else {
                Utils.message(result.error, 'error');
            }
        });
    }

    // Gestionnaire formulaire de connexion
    if (loginForm) {
        //Installation gestionnaire d'événements 'submit'
        loginForm.addEventListener('submit', async function (e) {
            // Empêche le rechargement de la page
            e.preventDefault();
            // Création objet à partir du formulaire
            const formData = {
                email: loginForm.querySelector('input[name="email"]').value.trim(),
                password: loginForm.querySelector('input[name="password"]').value.trim()
            };

            const result = await APIService.login(formData);

            // Cas de succès
            if (result.success) {
                Utils.message('Connexion Réussi!', 'success');
                // Redirection vers fil d'actualité
                Utils.redirectTo('feed.html', 2000);
            }
            // Cas d'échec
            else {
                Utils.message(result.error, 'error');
            }
        });
    }
});
// ========== MESSAGE DE DÉBOGAGE ==========
console.log('Auth.js chargé - Version simplifiée avec APIService + Utils');

