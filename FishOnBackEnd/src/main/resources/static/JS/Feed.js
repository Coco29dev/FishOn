// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function () {
    // Configuration navigation
    setupNavigation();

    // Chargement initial du feed
    loadFeed();
});

// ========== NAVIGATION ==========

function setupNavigation() {
    const journalBtn = document.getElementById('journalBtn');
    const profileBtn = document.getElementById('profileBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (journalBtn) journalBtn.onclick = () => window.location.href = 'journal.html';
    if (profileBtn) profileBtn.onclick = () => window.location.href = 'profile.html';
    if (logoutBtn) logoutBtn.onclick = handleLogout;
}

async function handleLogout() {
    const result = await APIService.logout();

    if (result.success) {
        Utils.message('Déconnexion réussie!', 'success');
        Utils.redirectTo('login.html', 1000);
    } else {
        Utils.message(result.error, 'error');
    }
}

// ========== CHARGEMENT DU FEED ==========

async function loadFeed() {
    const loadingMessage = document.getElementById('loadingMessage');
    const errorMessage = document.getElementById('errorMessage');
    const postsContainer = document.getElementById('postsContainer');

    try {
        // Gestion des états UI
        if (loadingMessage) loadingMessage.style.display = 'block';
        if (errorMessage) errorMessage.style.display = 'none';
        if (postsContainer) postsContainer.style.display = 'none';

        // Appel API
        const result = await APIService.getFeed();

        if (result.success) {
            if (loadingMessage) loadingMessage.style.display = 'none';
            displayPosts(result.data);
            if (postsContainer) postsContainer.style.display = 'block';
        } else {
            showError(errorMessage, loadingMessage, result.error);
        }
    } catch (error) {
        console.error('Erreur lors du chargement du feed:', error);
        showError(errorMessage, loadingMessage, 'Une erreur inattendue est survenue');
    }
}

function showError(errorMessage, loadingMessage, error) {
    if (loadingMessage) loadingMessage.style.display = 'none';
    if (errorMessage) {
        errorMessage.style.display = 'block';
        errorMessage.textContent = error;
    }

    // Gestion session expirée
    if (error.includes('Session expirée') || error.includes('Identifiants incorrets')) {
        Utils.redirectTo('login.html', 2000);
    }
}

function displayPosts(posts) {
    const postsContainer = document.getElementById('postsContainer');
    if (!postsContainer) return;

    postsContainer.innerHTML = '';

    // Gestion posts vides
    if (!posts || posts.length === 0) {
        Utils.displayEmptyState(postsContainer, 'empty-feed-template');
        return;
    }

    // Tri et affichage
    const sortedPosts = Utils.sortPostsByDate(posts);
    sortedPosts.forEach(post => {
        const postCard = createPostCard(post);
        postsContainer.appendChild(postCard);
    });
}

// ========== CRÉATION DE CARTES ==========

function createPostCard(post) {
    const template = document.getElementById('post-template');
    const postCard = template.content.cloneNode(true);

    // Données de base
    postCard.querySelector('.post-author').textContent = `@${post.userName}`;
    postCard.querySelector('.post-title').textContent = post.title;
    postCard.querySelector('.post-description').textContent = post.description;

    // Date formatée avec Utils
    const dateText = Utils.formatPostDate(post.createdAt, post.updatedAt);
    postCard.querySelector('.post-date').textContent = dateText;

    // Images avec Utils
    Utils.setupAvatar(postCard.querySelector('.avatar-img'), post.userProfilePicture || post.profilePicture, post.userName);
    Utils.setupPostImage(postCard.querySelector('.post-image'), post.photoUrl);

    // Détails avec Utils
    Utils.fillPostDetails(postCard, post);

    // Commentaires
    setupComments(postCard, post);

    return postCard;
}

// ========== GESTION DES COMMENTAIRES ==========

function setupComments(postElement, post) {
    const commentsCount = postElement.querySelector('.comments-count');
    const commentsTitle = postElement.querySelector('.comments-title');
    const commentsContent = postElement.querySelector('.comments-content');

    // Compteur
    commentsCount.textContent = `Commentaires (${post.comments ? post.comments.length : 0})`;

    // Toggle
    commentsTitle.onclick = () => toggleCommentsVisibility(post.id);

    // IDs
    commentsContent.id = `commentsContent-${post.id}`;
    commentsTitle.querySelector('.comments-toggle-icon').id = `commentsIcon-${post.id}`;

    // Remplissage
    fillComments(postElement, post.comments, post.id);
}

function fillComments(postElement, comments, postId) {
    const commentsList = postElement.querySelector('.comments-list');

    if (!comments || comments.length === 0) {
        Utils.displayEmptyState(commentsList, 'no-comments-template');
    } else {
        comments.forEach(comment => {
            const commentElement = createCommentElement(comment);
            commentsList.appendChild(commentElement);
        });
    }

    setupCommentForm(postElement, postId);
}

function createCommentElement(comment) {
    const template = document.getElementById('comment-template');
    const commentElement = template.content.cloneNode(true);

    // Données
    commentElement.querySelector('.comment-author').textContent = `@${comment.userName}`;
    commentElement.querySelector('.comment-content').textContent = comment.content;

    // Date
    const dateText = Utils.formatPostDate(comment.createdAt, comment.updatedAt);
    commentElement.querySelector('.comment-date').textContent = dateText;

    // Avatar
    Utils.setupAvatar(
        commentElement.querySelector('.comment-avatar-img'),
        comment.userProfilePicture || comment.profilePicture,
        comment.userName
    );

    return commentElement;
}

function setupCommentForm(postElement, postId) {
    const form = postElement.querySelector('.add-comment-form');
    const button = postElement.querySelector('.add-comment-toggle');
    const textarea = postElement.querySelector('.comment-input');
    const cancelBtn = postElement.querySelector('.comment-btn-cancel');

    // IDs
    form.id = `commentForm-${postId}`;
    textarea.id = `commentInput-${postId}`;

    // Events
    button.onclick = () => toggleCommentForm(postId);
    cancelBtn.onclick = () => cancelComment(postId);
    form.onsubmit = (e) => submitComment(e, postId);
}

// ========== TOGGLE FUNCTIONS ==========

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
        if (input) input.focus();
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

    if (input) input.value = '';
    form.classList.add('hidden');
    button.style.display = 'block';
}

// ========== SOUMISSION COMMENTAIRE ==========

async function submitComment(event, postId) {
    event.preventDefault();

    const input = document.getElementById(`commentInput-${postId}`);
    const submitButton = event.target.querySelector('.comment-btn-submit');

    // Validation
    if (!input || !input.value.trim()) {
        Utils.message('Veuillez saisir un commentaire', 'error');
        return;
    }

    // État bouton
    submitButton.disabled = true;
    submitButton.textContent = 'Publication...';

    try {
        const commentData = { content: input.value.trim() };
        const result = await APIService.createComment(postId, commentData);

        if (result.success) {
            Utils.message('Commentaire ajouté avec succès!', 'success');
            input.value = '';
            cancelComment(postId);
            await loadFeed();
        } else {
            Utils.handleAPIError(result.error);
        }
    } catch (error) {
        console.error('Erreur lors de l\'ajout du commentaire:', error);
        Utils.message('Une erreur est survenue lors de l\'ajout du commentaire', 'error');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Publier';
    }
}

// ========== RAFRAÎCHISSEMENT ==========

function refreshFeed() {
    loadFeed();
}

// Auto-refresh toutes les 30 secondes
setInterval(refreshFeed, 30000);

// ========== DEBUG ==========
console.log('Feed.js chargé - Version ultra-simplifiée');