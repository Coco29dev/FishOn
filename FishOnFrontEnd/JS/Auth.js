// ========== Fonction Message d'erreur ==========
function message(message, type) {
  // Gestion des messages d'erreur avec préfixe visuel
  if (type === 'error') {
    alert('Erreur: ' + message);
  } 
  // Gestion des messages de succès avec préfixe positif
  else if (type === 'success') {
    alert('Succès: ' + message);
  } 
  // Messages neutres sans préfixe
  else {
    alert(message);
  }
}

// ========== Initialisation ========== 
document.addEventListener('DOMContentLoaded', function () {
  // Récupération des références des formulaires depuis le DOM
  const loginForm = document.getElementById('formLogin');         // Formulaire de connexion
  const registerForm = document.getElementById('formRegister');   // Formulaire d'inscription


  // Gestionnaire pour le formulaire de connexion
  if (loginForm) {
    // Installation du gestionnaire d'événement 'submit'
    loginForm.addEventListener('submit', async function(e) {
      // Empêche le rechargement de la page (comportement par défaut des formulaires)
      e.preventDefault();

      // Création de l'objet loginData à partir du formulaire
      const loginData = {
        email: loginForm.querySelector('input[name="email"]').value.trim(),
        password: loginForm.querySelector('input[name="password"]').value
      };

      const result = await loginAPI(loginData);
      
      // Cas de succès : connexion réussie
      if (result.success) {
        // Affichage du message de confirmation
        message('Connexion Réussi!', 'success');
        
        // Redirection automatique vers le fil d'actualité après 1.5 secondes
        // Le délai permet à l'utilisateur de voir le message de succès
        setTimeout(() => {
          window.location.href = 'feed.html';  // Page principale de l'application
        }, 1500);
      } 
      // Cas d'échec : erreur lors de la connexion
      else {
        // Affichage du message d'erreur retourné par l'API
        message(result.error, 'error');
      }
    });
  }


  // Gestionnaire pour le formulaire d'inscription
  if (registerForm) {
    // Installation du gestionnaire d'événement 'submit'
    registerForm.addEventListener('submit', async function(e) {
      // Empêche le rechargement de la page
      e.preventDefault();

      // Création de l'objet formData à partir du formulaire
      const formData = {
        userName: registerForm.querySelector('input[name="username"]').value.trim(),
        email: registerForm.querySelector('input[name="email"]').value.trim(),
        password: registerForm.querySelector('input[name="password"]').value.trim(),
        firstName: registerForm.querySelector('input[name="firstname"]').value.trim(),
        lastName: registerForm.querySelector('input[name="lastname"]').value.trim(),
        age: parseInt(registerForm.querySelector('input[name="age"]').value),
        profilePicture: registerForm.querySelector('input[name="profilepicture"]').value.trim()
      };

      const result = await registerAPI(formData);

      // Cas de succès : inscription réussie
      if (result.success) {
        // Affichage du message de confirmation
        message('Inscription Réussie!', 'success');
        
        // Nettoyage du formulaire (vide tous les champs)
        // Améliore l'UX et évite les soumissions multiples accidentelles
        registerForm.reset();
        
        // Redirection vers la page de connexion après 2 secondes
        // Délai plus long que pour la connexion car l'utilisateur doit lire le message
        setTimeout(() => {
          window.location.href = 'login.html';  // Retour à la page de connexion
        }, 2000);
      } 
      // Cas d'échec : erreur lors de l'inscription
      else {
        // Affichage du message d'erreur retourné par l'API
        message(result.error, 'error');
      }
    });
  }
});

// ========== Fonction Spécifique ========== 

// authentification
async function loginAPI(loginData) {
  try {
    console.log('Données de connexion:', loginData);
      
    const response = await apiCall('/auth/login', {
      method: 'POST',
      body: JSON.stringify(loginData) // Conversion objet -> Chaîne JSON
    });

    const userData = await response.json();
    console.log('Connexion réussie:', userData);
    return { success: true, data: userData };
    
  } catch (error) {
    console.error('Erreur login:', error);
    
    // Messages d'erreur spécifiques
    let userMessage = 'Une erreur est survenue lors de la connexion';
    if (error.message.includes('Identifiants incorrects')) {
      userMessage = 'Email ou mot de passe incorrect';
    } else if (error.message.includes('Impossible de contacter')) {
      userMessage = 'Impossible de contacter le serveur';
    } else if (error.message.includes('Erreur serveur')) {
      userMessage = 'Erreur serveur, réessayez plus tard';
    }
    
    return { success: false, error: userMessage };
  }
}

// Inscription
async function registerAPI(formData) {
  try {
    console.log('Données d\'inscription:', formData);
      
    const response = await apiCall('/auth/register', {
      method: 'POST',
      body: JSON.stringify(formData)
    });

    const userData = await response.json();
    console.log('Inscription réussie:', userData);
    return { success: true, data: userData };
    
  } catch (error) {
    console.error('Erreur register:', error);
    
    // Messages d'erreur spécifiques selon votre backend Spring Boot
    let userMessage = 'Une erreur est survenue lors de l\'inscription';
    
    if (error.message.includes('email') && error.message.includes('déjà')) {
      userMessage = 'Cette adresse email est déjà utilisée';
    } else if (error.message.includes('username') && error.message.includes('déjà')) {
      userMessage = 'Ce nom d\'utilisateur est déjà pris';
    } else if (error.message.includes('Impossible de contacter')) {
      userMessage = 'Impossible de contacter le serveur';
    } else if (error.message.includes('Erreur serveur')) {
      userMessage = 'Erreur serveur, réessayez plus tard';
    }
    
    return { success: false, error: userMessage };
  }
}
