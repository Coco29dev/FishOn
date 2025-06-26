// ========== Configuration des chemins d'images ==========
const BACKEND_URL = 'http://localhost:8080'; // URL de votre backend Spring Boot

// Fonction pour obtenir l'URL correcte des avatars depuis le backend
function getCorrectAvatarPath(profilePicture, userName) {
    // Si pas d'image de profil ou chemin invalide, utiliser l'avatar par défaut local
    if (!profilePicture || profilePicture === 'null' || profilePicture === '') {
        return '../IMG/Avatar-defaut.png';
    }

    // Si le chemin commence par "profilePicture/", construire l'URL complète du backend
    if (profilePicture.startsWith('profilePicture/')) {
        return `${BACKEND_URL}/${profilePicture}`;
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
    return `${BACKEND_URL}/profilePicture/${profilePicture}`;
}

// Fonction pour obtenir l'URL correcte des photos de poissons
function getCorrectPhotoPath(photoUrl) {
    if (!photoUrl || photoUrl === 'null' || photoUrl === '') {
        return null;
    }

    // Si c'est déjà une URL complète, la retourner telle quelle
    if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) {
        return photoUrl;
    }

    // Si le chemin commence par "img/", construire l'URL complète du backend
    if (photoUrl.startsWith('img/')) {
        return `${BACKEND_URL}/${photoUrl}`;
    }

    // Si le chemin est relatif frontend, le garder tel quel
    if (photoUrl.startsWith('../IMG/') || photoUrl.startsWith('IMG/')) {
        return photoUrl;
    }

    // Par défaut, essayer de construire l'URL backend
    return `${BACKEND_URL}/img/${photoUrl}`;
}

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
    // Récupération des références des éléments depuis le DOM
    const profileBtn = document.getElementById('profileBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    // Gestionnaire pour le bouton profil
    if (profileBtn) {
        profileBtn.addEventListener('click', function() {
            window.location.href = 'profile.html';
        });
    }

    // Gestionnaire pour le bouton déconnexion
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function() {
            const result = await logoutAPI();

            if (result.success) {
                message('Déconnexion réussie!', 'success');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1000);
            } else {
                message(result.error, 'error');
            }
        });
    }

    // Chargement initial du feed
    loadFeed();
});

// ========== Fonctions de gestion du feed ==========

async function loadFeed() {
    const loadingMessage = document.getElementById('loadingMessage');
    const errorMessage = document.getElementById('errorMessage');
    const postsContainer = document.getElementById('postsContainer');

    try {
        // Affichage du message de chargement
        if (loadingMessage) loadingMessage.style.display = 'block';
        if (errorMessage) errorMessage.style.display = 'none';
        if (postsContainer) postsContainer.style.display = 'none';

        const result = await getFeedAPI();

        if (result.success) {
            if (loadingMessage) loadingMessage.style.display = 'none';
            displayPosts(result.data);
            if (postsContainer) postsContainer.style.display = 'block';
        } else {
            if (loadingMessage) loadingMessage.style.display = 'none';
            if (errorMessage) {
                errorMessage.style.display = 'block';
                errorMessage.textContent = result.error;
            }
        }
    } catch (error) {
        console.error('Erreur lors du chargement du feed:', error);
        if (loadingMessage) loadingMessage.style.display = 'none';
        if (errorMessage) {
            errorMessage.style.display = 'block';
            errorMessage.textContent = 'Une erreur inattendue est survenue';
        }
    }
}

// Dans Feed.js - Modifier la fonction displayPosts
function displayPosts(posts) {
    const postsContainer = document.getElementById('postsContainer');
    if (!postsContainer) return;

    postsContainer.innerHTML = '';

    if (!posts || posts.length === 0) {
        postsContainer.innerHTML = `
            <div class="empty-feed-message">
                <div class="empty-feed-icon">🎣</div>
                <div class="empty-feed-title">Aucune publication à afficher</div>
                <div class="empty-feed-subtitle">
                    La communauté n'a pas encore partagé de prises.<br>
                    Revenez plus tard pour découvrir les dernières captures !
                </div>
            </div>
        `;
        return;
    }

    // ✨ SOLUTION 1 : Tri par date de création décroissante (plus récent en premier)
    const sortedPosts = [...posts].sort((a, b) => {
        const dateA = new Date(a.createdAt);
        const dateB = new Date(b.createdAt);
        return dateB - dateA; // Ordre décroissant (plus récent en premier)
    });

    // Utiliser les posts triés au lieu des posts originaux
    sortedPosts.forEach(post => {
        const postCard = createPostCard(post);
        postsContainer.appendChild(postCard);
    });
}

function createPostCard(post) {
    const postCard = document.createElement('div');
    postCard.className = 'post-card';

    const createdDate = formatDate(post.createdAt);
    const isModified = post.updatedAt && post.createdAt &&
        Math.abs(new Date(post.updatedAt) - new Date(post.createdAt)) > 1000;
    const updatedDate = isModified ? formatDate(post.updatedAt) : null;

    // CORRECTION: Utiliser les fonctions pour obtenir les URLs correctes depuis le backend
    const avatarPath = getCorrectAvatarPath(post.userProfilePicture || post.profilePicture, post.userName);
    const photoPath = getCorrectPhotoPath(post.photoUrl);

    postCard.innerHTML = `
        <div class="post-header">
            <div class="post-author-info">
                <div class="post-author-avatar">
                    <img src="${avatarPath}" 
                         alt="Photo de profil de ${escapeHtml(post.userName)}"
                         onerror="this.src='../IMG/default-avatar.png'"
                         loading="lazy">
                </div>
                <div class="post-author-details">
                    <div class="post-author">@${escapeHtml(post.userName)}</div>
                    <div class="post-date">
                        ${createdDate}
                        ${updatedDate ? ` (modifié le ${updatedDate})` : ''}
                    </div>
                </div>
            </div>
        </div>

        <div class="post-content">
            <h2 class="post-title">${escapeHtml(post.title)}</h2>
            <p class="post-description">${escapeHtml(post.description)}</p>
            ${photoPath ? `
                <img 
                    src="${escapeHtml(photoPath)}" 
                    alt="Photo de pêche" 
                    class="post-image"
                    onerror="this.style.display='none'"
                    loading="lazy"
                    onclick="openImageModal('${escapeHtml(photoPath)}')"
                    style="cursor: pointer;"
                >
            ` : ''}
        </div>

        <div class="post-details">
            <div class="detail-item">
                <div class="detail-label">Poisson</div>
                <div class="detail-value">${escapeHtml(post.fishName)}</div>
            </div>
            ${post.weight ? `
                <div class="detail-item">
                    <div class="detail-label">Poids</div>
                    <div class="detail-value">${post.weight} kg</div>
                </div>
            ` : ''}
            ${post.length ? `
                <div class="detail-item">
                    <div class="detail-label">Longueur</div>
                    <div class="detail-value">${post.length} cm</div>
                </div>
            ` : ''}
            ${post.location ? `
                <div class="detail-item">
                    <div class="detail-label">Lieu</div>
                    <div class="detail-value">${escapeHtml(post.location)}</div>
                </div>
            ` : ''}
            ${post.catchDate ? `
                <div class="detail-item">
                    <div class="detail-label">Date de capture</div>
                    <div class="detail-value">${formatDate(post.catchDate)}</div>
                </div>
            ` : ''}
        </div>

        <div class="comments-section">
            <h3 class="comments-title" onclick="toggleCommentsVisibility('${post.id}')">
                <span class="comments-toggle-icon" id="commentsIcon-${post.id}">▶</span>
                Commentaires (${post.comments ? post.comments.length : 0})
            </h3>
            <div class="comments-content hidden" id="commentsContent-${post.id}">
                <div class="comments-list">
                    ${createCommentsHTML(post.comments, post.id)}
                </div>
            </div>
        </div>
    `;

    return postCard;
}

function createCommentsHTML(comments, postId) {
    let commentsHTML = '';

    if (comments && comments.length > 0) {
        commentsHTML = comments.map(comment => {
            const commentDate = formatDate(comment.createdAt);
            const isModified = comment.updatedAt && comment.createdAt &&
                Math.abs(new Date(comment.updatedAt) - new Date(comment.createdAt)) > 1000;
            const updatedDate = isModified ? formatDate(comment.updatedAt) : null;

            // CORRECTION: Utiliser la fonction pour obtenir l'URL correcte depuis le backend
            const avatarPath = getCorrectAvatarPath(comment.userProfilePicture || comment.profilePicture, comment.userName);

            return `
                <div class="comment-item">
                    <div class="comment-header">
                        <div class="comment-author-info">
                            <div class="comment-author-avatar">
                                <img src="${avatarPath}" 
                                     alt="Photo de profil de ${escapeHtml(comment.userName)}"
                                     onerror="this.src='../IMG/default-avatar.png'"
                                     loading="lazy">
                            </div>
                            <div class="comment-author-details">
                                <div class="comment-author">@${escapeHtml(comment.userName)}</div>
                                <div class="comment-date">
                                    ${commentDate}
                                    ${updatedDate ? ` (modifié le ${updatedDate})` : ''}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="comment-content">${escapeHtml(comment.content)}</div>
                </div>
            `;
        }).join('');
    } else {
        commentsHTML = '<div class="no-comments">Aucun commentaire pour le moment</div>';
    }

    commentsHTML += `
        <button class="add-comment-toggle" onclick="toggleCommentForm('${postId}')">
            💬 Ajouter un commentaire
        </button>
        <form class="add-comment-form hidden" id="commentForm-${postId}" onsubmit="submitComment(event, '${postId}')">
            <textarea 
                class="comment-input" 
                id="commentInput-${postId}"
                placeholder="Écrivez votre commentaire..."
                maxlength="1000"
                required
            ></textarea>
            <div class="comment-form-buttons">
                <button type="button" class="comment-btn comment-btn-cancel" onclick="cancelComment('${postId}')">
                    Annuler
                </button>
                <button type="submit" class="comment-btn comment-btn-submit">
                    Publier
                </button>
            </div>
        </form>
    `;

    return commentsHTML;
}

// ========== Fonctions utilitaires ==========

function formatDate(dateString) {
    if (!dateString) return '';

    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return 'Date invalide';
        }

        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);

        // ✨ OPTION 4 : Format intelligent adaptatif

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

        // Cette semaine (< 7 jours) : "il y a X jours"
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

        // Cette année : "12 nov" (date courte)
        else if (date.getFullYear() === now.getFullYear()) {
            const options = { day: 'numeric', month: 'short' };
            return date.toLocaleDateString('fr-FR', options);
        }

        // Ancienne année : "12 nov 2023"
        else {
            const options = { day: 'numeric', month: 'short', year: 'numeric' };
            return date.toLocaleDateString('fr-FR', options);
        }

    } catch (error) {
        console.error('Erreur lors du formatage de la date:', error);
        return 'Date invalide';
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ========== Fonctions pour les images ==========

function openImageModal(imageUrl) {
    // Créer un modal pour afficher l'image en grand
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

    const img = document.createElement('img');
    img.src = imageUrl;
    img.style.cssText = `
        max-width: 90%;
        max-height: 90%;
        object-fit: contain;
        border-radius: 8px;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    `;

    modal.appendChild(img);
    document.body.appendChild(modal);

    // Fermer le modal en cliquant dessus
    modal.addEventListener('click', function() {
        document.body.removeChild(modal);
    });

    // Fermer le modal avec la touche Escape
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (document.body.contains(modal)) {
                document.body.removeChild(modal);
            }
        }
    });
}

// ========== Fonctions API spécifiques ==========

async function getFeedAPI() {
    try {
        console.log('Récupération du feed...');

        const response = await apiCall('/posts/feed', {
            method: 'GET'
        });

        const feedData = await response.json();
        console.log('Feed récupéré:', feedData);
        return { success: true, data: feedData };

    } catch (error) {
        console.error('Erreur getFeed:', error);

        let userMessage = 'Une erreur est survenue lors du chargement du feed';

        if (error.message.includes('Utilisateur non authentifié')) {
            userMessage = 'Session expirée, veuillez vous reconnecter';
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else if (error.message.includes('Impossible de contacter')) {
            userMessage = 'Impossible de contacter le serveur';
        } else if (error.message.includes('Erreur serveur')) {
            userMessage = 'Erreur serveur, réessayez plus tard';
        }

        return { success: false, error: userMessage };
    }
}

async function logoutAPI() {
    try {
        console.log('Déconnexion en cours...');

        const response = await apiCall('/auth/logout', {
            method: 'POST'
        });

        const result = await response.text();
        console.log('Déconnexion réussie:', result);
        return { success: true, data: result };

    } catch (error) {
        console.error('Erreur logout:', error);

        let userMessage = 'Une erreur est survenue lors de la déconnexion';

        if (error.message.includes('Impossible de contacter')) {
            userMessage = 'Impossible de contacter le serveur';
        } else if (error.message.includes('Erreur serveur')) {
            userMessage = 'Erreur serveur, réessayez plus tard';
        }

        return { success: false, error: userMessage };
    }
}

// ========== Fonctions de gestion des commentaires ==========

function toggleCommentsVisibility(postId) {
    const commentsContent = document.getElementById(`commentsContent-${postId}`);
    const icon = document.getElementById(`commentsIcon-${postId}`);

    if (commentsContent.classList.contains('hidden')) {
        commentsContent.classList.remove('hidden');
        icon.textContent = '▼';
    } else {
        commentsContent.classList.add('hidden');
        icon.textContent = '▶';
    }
}

function toggleCommentForm(postId) {
    const form = document.getElementById(`commentForm-${postId}`);
    const button = form.previousElementSibling;

    if (form.classList.contains('hidden')) {
        form.classList.remove('hidden');
        button.style.display = 'none';
        const input = document.getElementById(`commentInput-${postId}`);
        if (input) {
            input.focus();
        }
        // Scroll vers le formulaire
        form.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    } else {
        form.classList.add('hidden');
        button.style.display = 'block';
    }
}

function cancelComment(postId) {
    const form = document.getElementById(`commentForm-${postId}`);
    const button = form.previousElementSibling;
    const input = document.getElementById(`commentInput-${postId}`);

    if (input) {
        input.value = '';
    }

    form.classList.add('hidden');
    button.style.display = 'block';
}

async function submitComment(event, postId) {
    event.preventDefault();

    const input = document.getElementById(`commentInput-${postId}`);
    const submitButton = event.target.querySelector('.comment-btn-submit');

    if (!input || !input.value.trim()) {
        message('Veuillez saisir un commentaire', 'error');
        return;
    }

    // Désactiver le bouton et changer le texte pendant l'envoi
    submitButton.disabled = true;
    submitButton.textContent = 'Publication...';

    try {
        const commentData = {
            content: input.value.trim()
        };

        const result = await createCommentAPI(postId, commentData);

        if (result.success) {
            message('Commentaire ajouté avec succès!', 'success');
            input.value = '';
            cancelComment(postId);
            // Recharger le feed pour afficher le nouveau commentaire
            loadFeed();
        } else {
            message(result.error, 'error');
        }
    } catch (error) {
        console.error('Erreur lors de l\'ajout du commentaire:', error);
        message('Une erreur est survenue lors de l\'ajout du commentaire', 'error');
    } finally {
        // Réactiver le bouton
        submitButton.disabled = false;
        submitButton.textContent = 'Publier';
    }
}

async function createCommentAPI(postId, commentData) {
    try {
        console.log('Création commentaire pour post:', postId, commentData);

        const response = await apiCall(`/comments/post/${postId}`, {
            method: 'POST',
            body: JSON.stringify(commentData)
        });

        const result = await response.json();
        console.log('Commentaire créé:', result);
        return { success: true, data: result };

    } catch (error) {
        console.error('Erreur createComment:', error);

        let userMessage = 'Une erreur est survenue lors de l\'ajout du commentaire';

        if (error.message.includes('Utilisateur non authentifié')) {
            userMessage = 'Session expirée, veuillez vous reconnecter';
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else if (error.message.includes('Post non trouvé')) {
            userMessage = 'Cette publication n\'existe plus';
        } else if (error.message.includes('Impossible de contacter')) {
            userMessage = 'Impossible de contacter le serveur';
        } else if (error.message.includes('Erreur serveur')) {
            userMessage = 'Erreur serveur, réessayez plus tard';
        }

        return { success: false, error: userMessage };
    }
}

// ========== Fonctions de rafraîchissement ==========

function refreshFeed() {
    loadFeed();
}

// Rafraîchir automatiquement le feed toutes les 30 secondes
setInterval(refreshFeed, 30000);

console.log('Feed.js chargé - Version avec support des images backend');