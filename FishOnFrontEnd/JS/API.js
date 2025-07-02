// Définition URL + préfixe API Spring Boot
const API_BASE = 'http://localhost:8080/api';

// Fonction asynchrone générique appel API
async function apiCall(endpoint, options = {}) {
  // Configuration par défaut
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include' // Envoie cookies de session automatiquement
  };

  // Fusionner les options
  const finalOptions = { ...defaultOptions, ...options };
  // ... = opérateur spread pour fusion
  // ...options = copie propréités de `options`(écrasse celle de `defaultOptions`)

  try {
    // Appel API
    const response = await fetch(`${API_BASE}${endpoint}`, finalOptions);

    // Vérification la réponse est une erreur
    if (!response.ok) {
      // Si erreur, gestion des codes d'erreur
      if (response.status === 401) {
        throw new Error('Identifiants incorrects');
      } else if (response.status === 404) {
        throw new Error('Ressource non trouvée');
      } else if (response.status === 500) {
        throw new Error('Erreur serveur interne');
      } else {
        throw new Error(`Erreur HTTP ${response.status}`);
      }
    }
    return response;
  } catch (error) {
    if (error.name === 'TypeError') {
      throw new Error('Impossible de contacter le serveur');
    }
    throw error;
  }
}
console.log('api.js chargé - apiCall() disponible');

// Fonction pour déconnecter l'utilisateur via l'API
async function logoutAPI() {
  try {
      console.log('Déconnexion en cours...');

      // Appel API pour déconnecter l'utilisateur
      const response = await apiCall('/auth/logout', {
          method: 'POST'
      });

      // Récupération de la réponse en texte brut
      const result = await response.text();
      console.log('Déconnexion réussie:', result);
      return { success: true, data: result }; // Retour standardisé en cas de succès

  } catch (error) {
      console.error('Erreur logout:', error);

      // Messages d'erreur spécifiques selon le type d'erreur
      let userMessage = 'Une erreur est survenue lors de la déconnexion';

      if (error.message.includes('Impossible de contacter')) {
          userMessage = 'Impossible de contacter le serveur';
      } else if (error.message.includes('Erreur serveur')) {
          userMessage = 'Erreur serveur, réessayez plus tard';
      }

      return { success: false, error: userMessage }; // Retour standardisé en cas d'erreur
  }
}