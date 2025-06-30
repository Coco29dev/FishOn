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

// ========== Configuration des chemins d'images ==========

// Fonction pour obtenir l'URL correcte des avatars depuis le backend
function getCorrectAvatarPath(profilePicture) {
    // Si pas d'image de profil ou chemin invalide, utiliser l'avatar par défaut local
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

// Fonction pour obtenir l'URL correcte des photos de poissons
function getCorrectPhotoPath(photoUrl) {
    // Si pas de photo ou chemin invalide, retourner null
    if (!photoUrl || photoUrl === 'null' || photoUrl === '') {
        return null;
    }
    // Si c'est déjà une URL complète, la retourner telle quelle
    if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) {
        return photoUrl;
    }
    // CORRECTION : Si le chemin commence par "fishPicture/", construire l'URL complète du backend
    if (photoUrl.startsWith('fishPicture/')) {
        return `${API_BASE.replace('/api', '')}/${photoUrl}`; // Construction URL backend
    }
    // Si le chemin est relatif frontend, le garder tel quel
    if (photoUrl.startsWith('../IMG/') || photoUrl.startsWith('IMG/')) {
        return photoUrl;
    }
    // CORRECTION : Par défaut, essayer de construire l'URL backend avec fishPicture
    return `${API_BASE.replace('/api', '')}/fishPicture/${photoUrl}`;
}

// ========== Initialisation ==========
document.addEventListener('DOMContentLoaded', function () {
    // Récupération des références des boutons de navigation depuis le DOM
    const profileBtn = document.getElementById('profileBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    // Gestionnaire pour le bouton profil
    if (profileBtn) {
        profileBtn.addEventListener('click', function() {
            // Redirection vers la page de profil
            window.location.href = 'profile.html';
        });
    }

    // Gestionnaire pour le bouton déconnexion
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function() {
            // Appel API pour déconnecter l'utilisateur
            const result = await logoutAPI();

            // Cas de succès : déconnexion réussie
            if (result.success) {
                message('Déconnexion réussie!', 'success');
                // Redirection vers la page de connexion après 1 seconde
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1000);
            }
            // Cas d'échec : erreur lors de la déconnexion
            else {
                message(result.error, 'error');
            }
        });
    }

    // Chargement initial du feed au chargement de la page
    loadFeed();
});

// ========== Fonctions de gestion du feed ==========

// Fonction pour charger et afficher le feed des publications
async function loadFeed() {
    // Récupération des éléments d'interface pour la gestion des états
    const loadingMessage = document.getElementById('loadingMessage');
    const errorMessage = document.getElementById('errorMessage');
    const postsContainer = document.getElementById('postsContainer');

    try {
        // Affichage de l'état de chargement
        if (loadingMessage) loadingMessage.style.display = 'block';
        if (errorMessage) errorMessage.style.display = 'none';
        if (postsContainer) postsContainer.style.display = 'none';

        // Appel API pour récupérer les données du feed
        const result = await getFeedAPI();

        // Cas de succès : affichage des publications
        if (result.success) {
            if (loadingMessage) loadingMessage.style.display = 'none';
            displayPosts(result.data); // Traitement et affichage des données
            if (postsContainer) postsContainer.style.display = 'block';
        }
        // Cas d'échec : affichage du message d'erreur
        else {
            if (loadingMessage) loadingMessage.style.display = 'none';
            if (errorMessage) {
                errorMessage.style.display = 'block';
                errorMessage.textContent = result.error;
            }
        }
    } catch (error) {
        // Gestion des erreurs non prévues
        console.error('Erreur lors du chargement du feed:', error);
        if (loadingMessage) loadingMessage.style.display = 'none';
        if (errorMessage) {
            errorMessage.style.display = 'block';
            errorMessage.textContent = 'Une erreur inattendue est survenue';
        }
    }
}

// Fonction pour traiter et afficher la liste des publications
function displayPosts(posts) {
    const postsContainer = document.getElementById('postsContainer');
    if (!postsContainer) return; // Protection si l'élément n'existe pas

    // Vidage du contenu précédent
    postsContainer.innerHTML = '';

    // Gestion du cas où aucune publication n'est disponible
    if (!posts || posts.length === 0) {
        // Utilisation template pour feed vide
        const emptyTemplate = document.getElementById('empty-feed-template');
        const emptyElement = emptyTemplate.content.cloneNode(true);
        postsContainer.appendChild(emptyElement);
        return;
    }

    // Tri des publications par date de création décroissante (plus récent en premier)
    const sortedPosts = [...posts].sort((a, b) => {
        const dateA = new Date(a.createdAt);
        const dateB = new Date(b.createdAt);
        return dateB - dateA; // Ordre décroissant
    });

    // Création et ajout de chaque carte de publication au DOM
    sortedPosts.forEach(post => {
        const postCard = createPostFromTemplate(post);
        postsContainer.appendChild(postCard);
    });
}

// Fonction pour créer l'élément HTML d'une carte de publication
function createPostFromTemplate(post) {
    // Récupération et clonage du template
    const template = document.getElementById('post-template');
    const postCard = template.content.cloneNode(true);

    // Remplir les données de base
    postCard.querySelector('.post-author').textContent = `@${post.userName}`;
    postCard.querySelector('.post-title').textContent = post.title;
    postCard.querySelector('.post-description').textContent = post.description;
    
    // Gestion des dates
    const createdDate = formatDate(post.createdAt);
    const isModified = post.updatedAt && post.createdAt &&
        Math.abs(new Date(post.updatedAt) - new Date(post.createdAt)) > 1000;
    const dateText = createdDate + (isModified ? ` (modifié le ${formatDate(post.updatedAt)})` : '');
    postCard.querySelector('.post-date').textContent = dateText;
    
    // Avatar
    const avatarImg = postCard.querySelector('.avatar-img');
    const avatarPath = getCorrectAvatarPath(post.userProfilePicture || post.profilePicture, post.userName);
    avatarImg.src = avatarPath;
    avatarImg.alt = `Photo de profil de ${post.userName}`;
    avatarImg.onerror = () => avatarImg.src = '../IMG/Avatar-defaut.png';
    
    // Image du post
    const postImage = postCard.querySelector('.post-image');
    const photoPath = getCorrectPhotoPath(post.photoUrl);
    if (photoPath) {
        postImage.src = photoPath;
        postImage.style.display = 'block';
        postImage.onclick = () => openImageModal(photoPath);
    }
    
    // Remplir les détails
    fillPostDetails(postCard, post);
    
    // Gestion des commentaires
    setupComments(postCard, post);

    return postCard;
}

// ========== Fonctions utilitaires ==========

// Fonction pour formater les dates de manière intelligente et relative
function formatDate(dateString) {
    // Vérification de la validité du paramètre
    if (!dateString) return '';

    try {
        // Conversion en objet Date
        const date = new Date(dateString);
        // Vérification que la date est valide
        if (isNaN(date.getTime())) {
            return 'Date invalide';
        }

        const now = new Date();
        // Calcul de la différence en secondes
        const diffInSeconds = Math.floor((now - date) / 1000);

        // Format intelligent adaptatif selon l'ancienneté

        // Très récent (< 1 minute) : "À l'instant"
        if (diffInSeconds < 60) {
            return "À l'instant";
        }
        // Récent (< 1 heure) : "il y a X min"
        else if (diffInSeconds < 3600) {
            const minutes = Math.floor(diffInSeconds / 60);
            return `il y a ${minutes} min`;
        }
        // Aujourd'hui (< 24h) : "il y a Xh"
        else if (diffInSeconds < 86400) {
            const hours = Math.floor(diffInSeconds / 3600);
            return `il y a ${hours}h`;
        }
        // Cette semaine (< 7 jours) : "il y a X jours" ou "hier"
        else if (diffInSeconds < 604800) {
            const days = Math.floor(diffInSeconds / 86400);
            if (days === 1) return "hier";
            return `il y a ${days} jours`;
        }
        // Ce mois (< 30 jours) : "il y a X semaines"
        else if (diffInSeconds < 2592000) {
            const weeks = Math.floor(diffInSeconds / 604800);
            return `il y a ${weeks} sem`;
        }
        // Cette année : "12 nov" (date courte sans année)
        else if (date.getFullYear() === now.getFullYear()) {
            const options = { day: 'numeric', month: 'short' };
            return date.toLocaleDateString('fr-FR', options);
        }
        // Ancienne année : "12 nov 2023" (date complète avec année)
        else {
            const options = { day: 'numeric', month: 'short', year: 'numeric' };
            return date.toLocaleDateString('fr-FR', options);
        }
    } catch (error) {
        // Gestion des erreurs de formatage
        console.error('Erreur lors du formatage de la date:', error);
        return 'Date invalide';
    }
}

// Fonction pour échapper les caractères HTML et prévenir les attaques XSS
function escapeHtml(text) {
    // Vérification de la validité du paramètre
    if (!text) return '';

    // Utilisation d'un élément div temporaire pour échapper automatiquement le HTML
    const div = document.createElement('div');
    div.textContent = text; // Définit le texte de manière sécurisée
    return div.innerHTML; // Récupère le HTML échappé
}

// ========== AJOUTER ICI après escapeHtml() ========== 

function fillPostDetails(postElement, post) {
    const detailsContainer = postElement.querySelector('.post-details');
    const detailTemplate = document.getElementById('detail-template');
    
    // Poisson (toujours présent)
    const fishDetail = detailTemplate.content.cloneNode(true);
    fishDetail.querySelector('.detail-label').textContent = 'POISSON';
    fishDetail.querySelector('.detail-value').textContent = post.fishName;
    detailsContainer.appendChild(fishDetail);
    
    // Poids (si disponible)
    if (post.weight) {
        const weightDetail = detailTemplate.content.cloneNode(true);
        weightDetail.querySelector('.detail-label').textContent = 'POIDS';
        weightDetail.querySelector('.detail-value').textContent = `${post.weight} kg`;
        detailsContainer.appendChild(weightDetail);
    }
    
    // Longueur (si disponible)
    if (post.length) {
        const lengthDetail = detailTemplate.content.cloneNode(true);
        lengthDetail.querySelector('.detail-label').textContent = 'LONGUEUR';
        lengthDetail.querySelector('.detail-value').textContent = `${post.length} cm`;
        detailsContainer.appendChild(lengthDetail);
    }
    
    // Lieu (si disponible)
    if (post.location) {
        const locationDetail = detailTemplate.content.cloneNode(true);
        locationDetail.querySelector('.detail-label').textContent = 'LIEU';
        locationDetail.querySelector('.detail-value').textContent = post.location;
        detailsContainer.appendChild(locationDetail);
    }
}

function setupComments(postElement, post) {
    const commentsCount = postElement.querySelector('.comments-count');
    const commentsTitle = postElement.querySelector('.comments-title');
    const commentsContent = postElement.querySelector('.comments-content');
    
    // Mettre à jour le nombre de commentaires
    commentsCount.textContent = `Commentaires (${post.comments ? post.comments.length : 0})`;
    
    // Event listener pour toggle commentaires
    commentsTitle.onclick = () => toggleCommentsVisibility(post.id);
    
    // Ajouter l'ID pour le toggle
    commentsContent.id = `commentsContent-${post.id}`;
    commentsTitle.querySelector('.comments-toggle-icon').id = `commentsIcon-${post.id}`;
    
    // Remplir les commentaires existants
    fillComments(postElement, post.comments, post.id);
}

function fillComments(postElement, comments, postId) {
    const commentsList = postElement.querySelector('.comments-list');
    
    if (!comments || comments.length === 0) {
        const noCommentsTemplate = document.getElementById('no-comments-template');
        const noCommentsElement = noCommentsTemplate.content.cloneNode(true);
        commentsList.appendChild(noCommentsElement);
    } else {
        // Ajouter chaque commentaire
        comments.forEach(comment => {
            const commentElement = createCommentFromTemplate(comment);
            commentsList.appendChild(commentElement);
        });
    }
    
    // Configurer le formulaire de commentaire
    setupCommentForm(postElement, postId);
}

function createCommentFromTemplate(comment) {
    const template = document.getElementById('comment-template');
    const commentElement = template.content.cloneNode(true);
    
    // Remplir les données du commentaire
    commentElement.querySelector('.comment-author').textContent = `@${comment.userName}`;
    commentElement.querySelector('.comment-content').textContent = comment.content;
    
    // Date du commentaire
    const commentDate = formatDate(comment.createdAt);
    const isModified = comment.updatedAt && comment.createdAt &&
        Math.abs(new Date(comment.updatedAt) - new Date(comment.createdAt)) > 1000;
    const dateText = commentDate + (isModified ? ` (modifié le ${formatDate(comment.updatedAt)})` : '');
    commentElement.querySelector('.comment-date').textContent = dateText;
    
    // Avatar du commentaire
    const avatarImg = commentElement.querySelector('.comment-avatar-img');
    const avatarPath = getCorrectAvatarPath(comment.userProfilePicture || comment.profilePicture, comment.userName);
    avatarImg.src = avatarPath;
    avatarImg.alt = `Photo de profil de ${comment.userName}`;
    avatarImg.onerror = () => avatarImg.src = '../IMG/Avatar-defaut.png';
    
    return commentElement;
}

function setupCommentForm(postElement, postId) {
    const form = postElement.querySelector('.add-comment-form');
    const button = postElement.querySelector('.add-comment-toggle');
    const textarea = postElement.querySelector('.comment-input');
    const cancelBtn = postElement.querySelector('.comment-btn-cancel');
    
    // Ajouter les IDs nécessaires
    form.id = `commentForm-${postId}`;
    textarea.id = `commentInput-${postId}`;
    
    // Event listeners
    button.onclick = () => toggleCommentForm(postId);
    cancelBtn.onclick = () => cancelComment(postId);
    form.onsubmit = (e) => submitComment(e, postId);
}

// Fonction pour ouvrir une image en modal (plein écran)
function openImageModal(imageUrl) {
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

// ========== Fonctions API spécifiques ==========

// Fonction pour récupérer le feed des publications depuis l'API
async function getFeedAPI() {
    try {
        console.log('Récupération du feed...');

        // Appel API pour récupérer les données du feed
        const response = await apiCall('/posts/feed', {
            method: 'GET'
        });

        // Conversion de la réponse en JSON
        const feedData = await response.json();
        console.log('Feed récupéré:', feedData);
        return { success: true, data: feedData }; // Retour standardisé en cas de succès

    } catch (error) {
        console.error('Erreur getFeed:', error);

        // Messages d'erreur spécifiques selon le type d'erreur
        let userMessage = 'Une erreur est survenue lors du chargement du feed';

        if (error.message.includes('Identifiants incorrects')) {
            userMessage = 'Session expirée, veuillez vous reconnecter';
            // Redirection automatique vers la page de connexion après 2 secondes
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else if (error.message.includes('Impossible de contacter')) {
            userMessage = 'Impossible de contacter le serveur';
        } else if (error.message.includes('Erreur serveur')) {
            userMessage = 'Erreur serveur, réessayez plus tard';
        }

        return { success: false, error: userMessage }; // Retour standardisé en cas d'erreur
    }
}

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

// Fonction pour créer un nouveau commentaire via l'API
async function createCommentAPI(postId, commentData) {
    try {
        console.log('Création commentaire pour post:', postId, commentData);

        // Appel API pour créer le commentaire
        const response = await apiCall(`/comments/post/${postId}`, {
            method: 'POST',
            body: JSON.stringify(commentData) // Conversion de l'objet en JSON
        });

        // Conversion de la réponse en JSON
        const result = await response.json();
        console.log('Commentaire créé:', result);
        return { success: true, data: result }; // Retour standardisé en cas de succès

    } catch (error) {
        console.error('Erreur createComment:', error);

        // Messages d'erreur spécifiques selon le type d'erreur
        let userMessage = 'Une erreur est survenue lors de l\'ajout du commentaire';

        if (error.message.includes('Identifiants incorrects')) {
            userMessage = 'Session expirée, veuillez vous reconnecter';
            // Redirection automatique vers la page de connexion
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else if (error.message.includes('Ressource non trouvée')) {
            userMessage = 'Cette publication n\'existe plus';
        } else if (error.message.includes('Impossible de contacter')) {
            userMessage = 'Impossible de contacter le serveur';
        } else if (error.message.includes('Erreur serveur')) {
            userMessage = 'Erreur serveur, réessayez plus tard';
        }

        return { success: false, error: userMessage }; // Retour standardisé en cas d'erreur
    }
}

// ========== Fonctions de gestion des commentaires ==========

// Fonction pour afficher/masquer la section commentaires d'une publication
function toggleCommentsVisibility(postId) {
    // Récupération des éléments DOM pour la gestion de l'affichage
    const commentsContent = document.getElementById(`commentsContent-${postId}`);
    const icon = document.getElementById(`commentsIcon-${postId}`);

    // Vérification si les commentaires sont actuellement masqués
    if (commentsContent.classList.contains('hidden')) {
        // Affichage des commentaires
        commentsContent.classList.remove('hidden');
        icon.textContent = '▼'; // Icône pointant vers le bas (ouvert)
    } else {
        // Masquage des commentaires
        commentsContent.classList.add('hidden');
        icon.textContent = '▶'; // Icône pointant vers la droite (fermé)
    }
}

// Fonction pour afficher/masquer le formulaire d'ajout de commentaire
function toggleCommentForm(postId) {
    // Récupération du formulaire et du bouton associé
    const form = document.getElementById(`commentForm-${postId}`);
    const button = form.previousElementSibling; // Bouton "Ajouter un commentaire"

    // Vérification si le formulaire est actuellement masqué
    if (form.classList.contains('hidden')) {
        // Affichage du formulaire
        form.classList.remove('hidden');
        button.style.display = 'none'; // Masquage du bouton

        // Focus automatique sur le champ de saisie pour améliorer l'UX
        const input = document.getElementById(`commentInput-${postId}`);
        if (input) {
            input.focus();
        }

        // Scroll vers le formulaire pour s'assurer qu'il est visible
        form.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    } else {
        // Masquage du formulaire
        form.classList.add('hidden');
        button.style.display = 'block'; // Réaffichage du bouton
    }
}

// Fonction pour annuler l'ajout d'un commentaire
function cancelComment(postId) {
    // Récupération des éléments du formulaire
    const form = document.getElementById(`commentForm-${postId}`);
    const button = form.previousElementSibling; // Bouton "Ajouter un commentaire"
    const input = document.getElementById(`commentInput-${postId}`);

    // Vidage du contenu du champ de saisie
    if (input) {
        input.value = '';
    }

    // Masquage du formulaire et réaffichage du bouton
    form.classList.add('hidden');
    button.style.display = 'block';
}

// Fonction pour soumettre un nouveau commentaire
async function submitComment(event, postId) {
    // Empêche le rechargement de la page (comportement par défaut des formulaires)
    event.preventDefault();

    // Récupération des éléments du formulaire
    const input = document.getElementById(`commentInput-${postId}`);
    const submitButton = event.target.querySelector('.comment-btn-submit');

    // Validation du contenu du commentaire
    if (!input || !input.value.trim()) {
        message('Veuillez saisir un commentaire', 'error');
        return; // Arrêt de l'exécution si le commentaire est vide
    }

    // Désactivation du bouton et indication de l'envoi en cours
    submitButton.disabled = true;
    submitButton.textContent = 'Publication...';

    try {
        // Préparation des données du commentaire
        const commentData = {
            content: input.value.trim() // Suppression des espaces en début/fin
        };

        // Appel API pour créer le commentaire
        const result = await createCommentAPI(postId, commentData);

        // Traitement du résultat
        if (result.success) {
            message('Commentaire ajouté avec succès!', 'success');
            input.value = ''; // Vidage du champ de saisie
            cancelComment(postId); // Fermeture du formulaire
            loadFeed(); // Rechargement du feed pour afficher le nouveau commentaire
        } else {
            message(result.error, 'error');
        }
    } catch (error) {
        // Gestion des erreurs non prévues
        console.error('Erreur lors de l\'ajout du commentaire:', error);
        message('Une erreur est survenue lors de l\'ajout du commentaire', 'error');
    } finally {
        // Réactivation du bouton dans tous les cas (succès ou erreur)
        submitButton.disabled = false;
        submitButton.textContent = 'Publier';
    }
}

// ========== Fonctions de rafraîchissement ==========

// Fonction pour rafraîchir manuellement le feed
function refreshFeed() {
    loadFeed(); // Rechargement complet du feed
}

// ========== Rafraîchissement automatique ==========
// Rafraîchissement automatique du feed toutes les 30 secondes (30000 ms)
// Permet de maintenir le contenu à jour sans intervention de l'utilisateur
setInterval(refreshFeed, 30000);

// ========== Message de débogage ==========
console.log('Feed.js chargé - Version refactorisée selon logique Auth.js');