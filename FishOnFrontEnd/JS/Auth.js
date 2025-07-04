// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function () {
  // Récupération des références des formulaires depuis le DOM
  const loginForm = document.getElementById('formLogin');
  const registerForm = document.getElementById('formRegister');

  // Gestionnaire pour le formulaire de connexion
  if (loginForm) {
    loginForm.addEventListener('submit', async function(e) {
      e.preventDefault();

      // Extraction des données du formulaire
      const loginData = {
        email: loginForm.querySelector('input[name="email"]').value.trim(),
        password: loginForm.querySelector('input[name="password"]').value
      };

      // Appel API via APIService
      const result = await APIService.login(loginData);

      if (result.success) {
        Utils.message('Connexion réussie!', 'success');
        Utils.redirectTo('feed.html', 1500);
      } else {
        Utils.message(result.error, 'error');
      }
    });
  }

  // Gestionnaire pour le formulaire d'inscription
  if (registerForm) {
    registerForm.addEventListener('submit', async function(e) {
      e.preventDefault();

      // Extraction des données du formulaire
      const registerData = {
        userName: registerForm.querySelector('input[name="username"]').value.trim(),
        email: registerForm.querySelector('input[name="email"]').value.trim(),
        password: registerForm.querySelector('input[name="password"]').value.trim(),
        firstName: registerForm.querySelector('input[name="firstname"]').value.trim(),
        lastName: registerForm.querySelector('input[name="lastname"]').value.trim(),
        age: parseInt(registerForm.querySelector('input[name="age"]').value),
        profilePicture: registerForm.querySelector('input[name="profilepicture"]').value.trim()
      };

      // Appel API via APIService
      const result = await APIService.register(registerData);

      if (result.success) {
        Utils.message('Inscription réussie!', 'success');
        registerForm.reset();
        Utils.redirectTo('login.html', 2000);
      } else {
        Utils.message(result.error, 'error');
      }
    });
  }
});

// ========== MESSAGE DE DÉBOGAGE ==========
console.log('Auth.js chargé - Version simplifiée avec APIService + Utils');