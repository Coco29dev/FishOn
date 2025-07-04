// Fonction utilitaires partagées
class Utils {
  // ========== MESSAGES ==========

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

  // ========== DATES ==========

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

  // Formatage date pour posts (avec modification)
  static formatPostDate(createdAt, updatedAt) {
    const createdDate = Utils.formatDate(createdAt);
    const isModified = updatedAt && createdAt &&
        Math.abs(new Date(updatedAt) - new Date(createdAt)) > 1000;
    return isModified ? `${createdDate} (modifié le ${Utils.formatDate(updatedAt)})` : createdDate;
  }

  // ========== IMAGES ==========

  // Gestion chemin photo de profil
  static getProfilePicturePath(profilePicture) {
    // Si aucune image de profil ou chemin invalide -> utilisation avatar par défaut local
    if (!profilePicture || profilePicture === 'null' || profilePicture === '') {
      return '../IMG/Avatar-defaut.png';
    }

    // Si le chemin commence par "profilePicture/", construire l'URL complète du backend
    if (profilePicture.startsWith('profilePicture/')) {
      return `${API_BASE.replace('/api', '')}/${profilePicture}`;
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
      return `${API_BASE.replace('/api', '')}/${photoUrl}`;
    }
    // Si le chemin est relatif frontend, le garder tel quel
    if (photoUrl.startsWith('../IMG/') || photoUrl.startsWith('IMG/')) {
      return photoUrl;
    }
    // Par défaut, essayer de construire l'URL backend avec fishPicture
    return `${API_BASE.replace('/api', '')}/fishPicture/${photoUrl}`;
  }

  // Configuration avatar
  static setupAvatar(avatarImg, profilePicture, userName) {
    const avatarPath = Utils.getProfilePicturePath(profilePicture);
    avatarImg.src = avatarPath;
    avatarImg.alt = `Photo de profil de ${userName}`;
    avatarImg.onerror = () => avatarImg.src = '../IMG/Avatar-defaut.png';
  }

  // Configuration image de post
  static setupPostImage(postImage, photoUrl) {
    const photoPath = Utils.getFishPicturePath(photoUrl);
    if (photoPath) {
      postImage.src = photoPath;
      postImage.style.display = 'block';
      postImage.onclick = () => Utils.openImageModal(photoPath);
    }
  }

  // Modal d'affichage d'images
  static openImageModal(imageUrl) {
    // Création de l'élément modal
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.8);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 2000;
        cursor: pointer;
    `;

    // Création de l'élément image
    const img = document.createElement('img');
    img.src = imageUrl;
    img.style.cssText = `
        max-width: 90%;
        max-height: 90%;
        object-fit: contain;
        border-radius: 8px;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    `;

    // Ajout de l'image au modal et du modal au DOM
    modal.appendChild(img);
    document.body.appendChild(modal);

    // Fermeture du modal en cliquant dessus
    modal.addEventListener('click', function() {
      document.body.removeChild(modal);
    });

    // Fermeture du modal avec la touche Escape
    document.addEventListener('keydown', function(e) {
      if (e.key === 'Escape') {
        if (document.body.contains(modal)) {
          document.body.removeChild(modal);
        }
      }
    });
  }

  // ========== POSTS ==========

  // Tri des posts par date - FONCTION MANQUANTE AJOUTÉE
  static sortPostsByDate(posts) {
    // Protection contre les valeurs nulles/undefined
    if (!posts || !Array.isArray(posts)) {
      console.warn('sortPostsByDate: posts n\'est pas un tableau valide', posts);
      return []; // Retourne un tableau vide si pas de posts
    }

    return [...posts].sort((a, b) => {
      return new Date(b.createdAt) - new Date(a.createdAt);
    });
  }

  // Ajout d'un détail de post
  static addDetailItem(container, template, label, value) {
    const detail = template.content.cloneNode(true);
    detail.querySelector('.detail-label').textContent = label;
    detail.querySelector('.detail-value').textContent = value;
    container.appendChild(detail);
  }

  // Remplissage des détails d'un post
  static fillPostDetails(postElement, post) {
    const detailsContainer = postElement.querySelector('.post-details');
    const detailTemplate = document.getElementById('detail-template');

    // Poisson (champ obligatoire)
    Utils.addDetailItem(detailsContainer, detailTemplate, 'POISSON', post.fishName);

    // Champs optionnels
    if (post.weight) {
      Utils.addDetailItem(detailsContainer, detailTemplate, 'POIDS', `${post.weight} kg`);
    }

    if (post.length) {
      Utils.addDetailItem(detailsContainer, detailTemplate, 'LONGUEUR', `${post.length} cm`);
    }

    if (post.location) {
      Utils.addDetailItem(detailsContainer, detailTemplate, 'LIEU', post.location);
    }
  }

  // ========== ÉTATS UI ==========

  // Affichage état vide
  static displayEmptyState(container, templateId) {
    const emptyTemplate = document.getElementById(templateId);
    if (emptyTemplate) {
      const emptyElement = emptyTemplate.content.cloneNode(true);
      container.appendChild(emptyElement);
    }
  }

  // ========== NAVIGATION ==========

  // Gestion redirection avec délai
  static redirectTo(url, delay = 1500) {
    setTimeout(() => {
      window.location.href = url;
    }, delay);
  }

  // ========== GESTION D'ERREURS ==========

  // Gestion des erreurs API avec redirection pour session expirée
  static handleAPIError(error) {
    Utils.message(error, 'error');

    // Gestion spéciale pour session expirée
    if (error.includes('Session expirée') || error.includes('Identifiants incorrects')) {
      Utils.redirectTo('login.html', 2000);
    }
  }
}