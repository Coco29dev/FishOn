// Configuration
const API_BASE = 'http://localhost:8080/api';

// Fonction générique appel API
async function apiCall(endpoint, options = {}) {
  // Configuration par défaut
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include'
  };

  // Fusionner les options
  const finalOptions = { ...defaultOptions, ...options };

  try {
    const response = await fetch(`${API_BASE}${endpoint}`, finalOptions);

    if (!response.ok) {
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