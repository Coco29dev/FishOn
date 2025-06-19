//Initialisation
document.addEventListener('DOMContentLoaded', function () {
  const loginForm = document.getElementById('formLogin');
  const registerForm = document.getElementById('formRegister');

  // Gestionnaire pour le formulaire de connexion
  if (loginForm) {
    loginForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      // Création de l'objet loginData à partir du formulaire
      const loginData = {
        email: loginForm.querySelector('input[name="email"]').value,
        password: loginForm.querySelector('input[name="password"]').value
      };

      await loginAPI(loginData);
    });
  }

  // Gestionnaire pour le formulaire d'inscription
  if (registerForm) {
    registerForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      // Création de l'objet formData à partir du formulaire
      const formData = {
        userName: registerForm.querySelector('input[name="username"]').value.trim(),
        email: registerForm.querySelector('input[name="email"]').value.trim(),
        password: registerForm.querySelector('input[name="password"]').value.trim(),
        firstName: registerForm.querySelector('input[name="firstname"]').value.trim(),
        lastName: registerForm.querySelector('input[name="lastname"]').value.trim(),
        age: parseInt(registerForm.querySelector('input[name="age"]').value),
        profilPicture: registerForm.querySelector('input[name="profilpicture"]').value.trim()
      };

      await registerAPI(formData);
    });
  }
});

// Fonction spécifique authentification
async function loginAPI(loginData) {
  console.log('Données de connexion:', loginData);
    
  const response = await apiCall('/auth/login', {
    method: 'POST',
    body: JSON.stringify(loginData)
  });
  return response;
}

// Fonction spécifique d'inscription
async function registerAPI(formData) {
  console.log('Données d\'inscription:', formData);
    
  const response = await apiCall('/auth/register', {
    method: 'POST',
    body: JSON.stringify(formData)
  });
  return response;
}
