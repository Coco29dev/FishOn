const API_BASE = window.location.hostname.includes('railway.app')
    ? '/api'  // Production Railway
    : '/api'; // Développement local

class API {
    // Fonction asynchrone générique appel API
    static async apiCall(endpoint, options = {}) {
        // Configuration par défaut
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include' // Envoie cookies de session automatiquement
        };

        // Fusionner les options
        const finalOptions = { ...defaultOptions, ...options };

        try {
            // Construction URL complète
            const url = `${API_BASE}${endpoint}`;
            console.log(`API Call: ${options.method || 'GET'} ${url}`); // Debug

            // Appel API
            const response = await fetch(url, finalOptions);

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

    // Méthodes spécialisées (inchangées)
    static get(endpoint) {
        return this.apiCall(endpoint, {
            method: 'GET'
        });
    }

    static post(endpoint, data) {
        return this.apiCall(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    static put(endpoint, data) {
        return this.apiCall(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    static delete(endpoint) {
        return this.apiCall(endpoint, {
            method: 'DELETE'
        });
    }
}