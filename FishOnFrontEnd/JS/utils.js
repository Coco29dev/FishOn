// Fonction utilitaires partagées
class Utils {
  // Gestion unifiée des messages
  static message(message, type) {
    if (type === 'error') {
      alert('Erreur: ' + message);
    } else if (type === 'success') {
      alert('Succès: ' + message);
    } else {
      alert(message);
    }
  }

  // Formatage dates
  static formatDate(dateString) {
    // Vérification paramètres
    if (!dateString) return '';

    try {
      // Conversion paramètre fonction(chaîne de caractères) en objet Date
      const date = new Date(dateString);
      // Vérification nombre valide via isNaN donc si date valide
      if (isNaN(date.getTime())) {
        return 'Date invalide';
      }

      const now = new Date();
      // Calcul de la différence en secondes
      const diffInSeconds = Math.floor((now - date) / 1000);

      if (diffInSeconds < 60) {
        return "À l'instant"
      }
      else if (diffInSeconds < 3600) {
        const minutes = Math.floor(diffInSeconds / 60);
        if (minutes === 1) {
          return `Il y a ${minutes} minute`
        } else {
          return `Il y a ${minutes} minutes`
        }
      }
      else if (diffInSeconds < 86400) {
        const heures = Math.floor(diffInSeconds / 3600);
        if (heures === 1) {
          return `Il y a ${heures} heure`;
        } else {
          return `Il y a ${heures} heures`;
        }
      }
      else if (diffInSeconds < 604800) {
        const jours = Math.floor(diffInSeconds / 86400);
        if (jours === 1) {
          return `Il y a ${jours} jour`
        } else {
          return `Il y a ${jours} jours`
        }
      }
      else if (diffInSeconds < 2592000) {
        const semaines = Math.floor(diffInSeconds / 604800);
        if (semaines === 1) {
          return `Il y a ${semaines} semaine`;
        } else {
          return `Il y a ${semaines} semaines`;
        }
      }
      else if (date.getFullYear() === now.getFullYear()) {
        const options = { day: 'numeric', month: 'short' };
        return date.toLocaleDateString('fr-FR', options);
      }
      else {
        const options = { day: 'numeric', month: 'short', year: 'numeric' };
        return date.toLocaleDateString('fr-FR', options);
      }
    } catch (error) {
      console.error('Erreur formatage date: ', error);
      return 'Date invalide';
    }
  }

  // Gestion chemins d'images
  // Gestion chemin photo de profil
  static getProfilePicturePath(profilePicture) {
    // Si aucune image de profil ou chemin ivalide -> utilisation avatar par défaut local
    if (!profilePicture || profilePicture === 'null' || profilePicture === '') {
      return '../IMG/Avatar-defaut.png';
    }

    // Si le chemin commence par "profilePicture/", construire l'URL complète du backend
    if (profilePicture.startsWith('profilePicture/')) {
      return `${API_BASE.replace('/api', '')}/${profilePicture}`; // Suppression du '/api' pour avoir l'URL racine du backend
  }

    // Si c'est déjà une URL complète, la retourner telle quelle
    if (profilePicture.startsWith('http://') || profilePicture.startsWith('https://')) {
        return profilePicture;
    }

    // Si le chemin est relatif frontend (../IMG/), le garder tel quel
    if (profilePicture.startsWith('../IMG/') || profilePicture.startsWith('IMG/')) {
        return profilePicture;
    }

  // Par défaut, essayer de construire l'URL backend
    return `${API_BASE.replace('/api', '')}/profilePicture/${profilePicture}`;
  }

  // Gestion photo de poisson
  static getFishPicturePath(photoUrl) {
    // Si pas de photo ou chemin invalide, retourner null
    if (!photoUrl || photoUrl === 'null' || photoUrl === '') {
      return null;
  }
    // Si c'est déjà une URL complète, la retourner telle quelle
    if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) {
        return photoUrl;
    }
    // Si le chemin commence par "fishPicture/", construire l'URL complète du backend
    if (photoUrl.startsWith('fishPicture/')) {
        return `${API_BASE.replace('/api', '')}/${photoUrl}`; // Construction URL backend
    }
    // Si le chemin est relatif frontend, le garder tel quel
    if (photoUrl.startsWith('../IMG/') || photoUrl.startsWith('IMG/')) {
        return photoUrl;
    }
    // Par défaut, essayer de construire l'URL backend avec fishPicture
    return `${API_BASE.replace('/api', '')}/fishPicture/${photoUrl}`;
  }
}