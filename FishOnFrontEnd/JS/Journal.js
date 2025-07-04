// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function () {
    // Configuration navigation
    setupNavigation();

    // Configuration formulaire création
    setupCreateForm();

    // Chargement initial
    loadMemoires();
});

// ========== NAVIGATION ==========

function setupNavigation() {
    const feedBtn = document.getElementById('feedBtn');
    const profileBtn = document.getElementById('profileBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (feedBtn) feedBtn.onclick = () => window.location.href = 'feed.html';
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

// ========== CONFIGURATION FORMULAIRE ==========

function setupCreateForm() {
    const toggleBtn = document.getElementById('toggleCreatePostBtn');
    const cancelBtn = document.getElementById('cancelPostBtn');
    const form = document.getElementById('postForm');

    if (toggleBtn) toggleBtn.onclick = toggleCreatePostForm;
    if (cancelBtn) cancelBtn.onclick = cancelCreatePost;
    if (form) form.onsubmit = async (e) => {
        e.preventDefault();
        await submitCreatePost(e);
    };
}

// ========== CHARGEMENT DES MÉMOIRES ==========

async function loadMemoires() {
    const elements = {
        loading: document.getElementById('loadingMessage'),
        error: document.getElementById('errorMessage'),
        latest: document.getElementById('latestPostSection'),
        memory: document.getElementById('memoryPostsSection')
    };

    try {
        // Gestion états UI
        showLoading(elements);

        // Appel API
        const result = await APIService.getCurrentUserPosts();

        if (result.success) {
            hideLoading(elements);
            displayUserPosts(result.data);
            showContent(elements);
        } else {
            showError(elements, result.error);
        }
    } catch (error) {
        console.error('Erreur lors du chargement des mémoires:', error);
        showError(elements, 'Une erreur inattendue est survenue');
    }
}

// ========== GESTION ÉTATS UI ==========

function showLoading(elements) {
    if (elements.loading) elements.loading.style.display = 'block';
    if (elements.error) elements.error.style.display = 'none';
    if (elements.latest) elements.latest.style.display = 'none';
    if (elements.memory) elements.memory.style.display = 'none';
}

function hideLoading(elements) {
    if (elements.loading) elements.loading.style.display = 'none';
}

function showContent(elements) {
    if (elements.latest) elements.latest.style.display = 'block';
    if (elements.memory) elements.memory.style.display = 'block';
}

function showError(elements, error) {
    hideLoading(elements);
    if (elements.error) {
        elements.error.style.display = 'block';
        elements.error.textContent = error;
    }

    // Gestion session expirée
    if (error.includes('Session expirée') || error.includes('Identifiants incorrects')) {
        Utils.redirectTo('login.html', 2000);
    }
}

// ========== AFFICHAGE POSTS ==========

function displayUserPosts(posts) {
    const latestContainer = document.getElementById('latestPostContainer');
    const memoryContainer = document.getElementById('memoryPostsContainer');

    if (!latestContainer || !memoryContainer) return;

    // Nettoyage
    latestContainer.innerHTML = '';
    memoryContainer.innerHTML = '';

    // Gestion posts vides
    if (!posts || posts.length === 0) {
        Utils.displayEmptyState(latestContainer, 'empty-page-template');
        return;
    }

    // Tri par date
    const sortedPosts = Utils.sortPostsByDate(posts);

    // Affichage dernier post
    const latestPostCard = createPostCard(sortedPosts[0], 'latest-post-template', true);
    latestContainer.appendChild(latestPostCard);

    // Affichage souvenirs
    sortedPosts.slice(1).forEach(post => {
        const souvenirCard = createPostCard(post, 'souvenir-post-template', false);
        memoryContainer.appendChild(souvenirCard);
    });
}

// ========== CRÉATION CARTES ==========

function createPostCard(post, templateId, isLatest) {
    const template = document.getElementById(templateId);
    const card = template.content.cloneNode(true);

    // Remplissage données
    fillBasicData(card, post, isLatest);

    // Détails
    if (isLatest) {
        Utils.fillPostDetails(card, post);
    } else {
        fillSouvenirDetails(card, post);
    }

    // Boutons action
    setupActionButtons(card, post, isLatest);

    return card;
}

function fillBasicData(card, post, isLatest) {
    if (isLatest) {
        // Données pour dernière publication
        card.querySelector('.post-author').textContent = `@${post.userName}`;
        card.querySelector('.post-title').textContent = post.title;
        card.querySelector('.post-description').textContent = post.description;
        card.querySelector('.post-date').textContent = Utils.formatPostDate(post.createdAt, post.updatedAt);

        // Images
        Utils.setupAvatar(card.querySelector('.avatar-img'), post.userProfilePicture || post.profilePicture, post.userName);
        Utils.setupPostImage(card.querySelector('.post-image'), post.photoUrl);
    } else {
        // Données pour souvenir
        card.querySelector('.souvenir-title').textContent = post.title;
        card.querySelector('.souvenir-fish').textContent = post.fishName;
        card.querySelector('.souvenir-date').textContent = Utils.formatDate(post.createdAt);

        // Image souvenir
        const img = card.querySelector('.souvenir-img');
        const photoPath = Utils.getFishPicturePath(post.photoUrl);
        if (photoPath) {
            img.src = photoPath;
            img.alt = `Photo de ${post.fishName}`;
            img.onclick = () => Utils.openImageModal(photoPath);
        } else {
            img.src = '../IMG/Avatar-defaut.png';
        }
    }
}

function fillSouvenirDetails(card, post) {
    const container = card.querySelector('.souvenir-details');
    const template = document.getElementById('detail-template');

    if (post.weight) {
        Utils.addDetailItem(container, template, 'POIDS', `${post.weight}kg`);
    }

    if (post.length) {
        Utils.addDetailItem(container, template, 'TAILLE', `${post.length}cm`);
    }
}

function setupActionButtons(card, post, isLatest) {
    const editSelector = isLatest ? '.btn-edit-post' : '.btn-edit-souvenir';
    const deleteSelector = isLatest ? '.btn-delete-post' : '.btn-delete-souvenir';

    const editBtn = card.querySelector(editSelector);
    const deleteBtn = card.querySelector(deleteSelector);

    if (editBtn) editBtn.onclick = () => openEditModal(post);
    if (deleteBtn) deleteBtn.onclick = () => openDeleteModal(post.id);
}

// ========== FORMULAIRE CRÉATION ==========

function toggleCreatePostForm() {
    const form = document.getElementById('createPostForm');
    const btn = document.getElementById('toggleCreatePostBtn');

    if (!form || !btn) return;

    const isHidden = form.classList.contains('hidden');

    if (isHidden) {
        form.classList.remove('hidden');
        btn.textContent = '❌ Annuler la création';

        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        setTimeout(() => {
            const input = document.getElementById('post-title');
            if (input) input.focus();
        }, 300);
    } else {
        cancelCreatePost();
    }
}

function cancelCreatePost() {
    const form = document.getElementById('createPostForm');
    const btn = document.getElementById('toggleCreatePostBtn');
    const postForm = document.getElementById('postForm');

    if (form && btn) {
        form.classList.add('hidden');
        btn.textContent = '➕ Créer un nouveau souvenir';
    }

    if (postForm) postForm.reset();
}

async function submitCreatePost(event) {
    const form = event.target;
    const submitBtn = form.querySelector('.btn-submit');

    // État bouton
    submitBtn.disabled = true;
    submitBtn.textContent = 'Publication...';

    try {
        const postData = extractFormData(form);
        const result = await APIService.createPost(postData);

        if (result.success) {
            Utils.message('Souvenir créé avec succès!', 'success');
            cancelCreatePost();
            await loadMemoires();
        } else {
            Utils.handleAPIError(result.error);
        }
    } catch (error) {
        console.error('Erreur lors de la création du post:', error);
        Utils.message('Une erreur est survenue lors de la création du souvenir', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Publier le souvenir';
    }
}

function extractFormData(form) {
    return {
        title: form.querySelector('#post-title').value.trim(),
        description: form.querySelector('#post-description').value.trim(),
        fishName: form.querySelector('#post-fish').value.trim(),
        photoUrl: form.querySelector('#post-photo').value.trim(),
        weight: form.querySelector('#post-weight').value ? parseFloat(form.querySelector('#post-weight').value) : null,
        length: form.querySelector('#post-length').value ? parseFloat(form.querySelector('#post-length').value) : null,
        location: form.querySelector('#post-location').value.trim() || null,
        catchDate: form.querySelector('#post-date').value ? new Date(form.querySelector('#post-date').value).toISOString() : null
    };
}

// ========== MODALS ==========

function openEditModal(postData) {
    const template = document.getElementById('edit-post-modal-template');
    const modal = template.content.cloneNode(true);

    // Pré-remplissage
    prefillEditForm(modal, postData);

    // Configuration événements
    const closeModal = setupModalEvents(modal);

    // Soumission
    const form = modal.querySelector('.edit-post-form');
    form.onsubmit = async (e) => {
        e.preventDefault();
        await submitUpdate(form, postData.id, closeModal);
    };

    // Affichage
    document.body.appendChild(modal);
    setTimeout(() => {
        const input = modal.querySelector('#edit-post-title');
        if (input) input.focus();
    }, 100);
}

function prefillEditForm(modal, postData) {
    modal.getElementById('edit-post-title').value = postData.title;
    modal.getElementById('edit-post-description').value = postData.description;
    modal.getElementById('edit-post-fish').value = postData.fishName;
    modal.getElementById('edit-post-photo').value = postData.photoUrl;
    modal.getElementById('edit-post-weight').value = postData.weight || '';
    modal.getElementById('edit-post-length').value = postData.length || '';
    modal.getElementById('edit-post-location').value = postData.location || '';

    if (postData.catchDate) {
        const date = new Date(postData.catchDate);
        modal.getElementById('edit-post-date').value = date.toISOString().slice(0, 16);
    }
}

function setupModalEvents(modal) {
    const closeModal = () => {
        const modalElement = document.querySelector('.modal-overlay');
        if (modalElement) document.body.removeChild(modalElement);
    };

    // Boutons fermeture
    modal.querySelector('.modal-close').onclick = closeModal;
    modal.querySelector('.btn-cancel').onclick = closeModal;

    // Clic overlay
    modal.querySelector('.modal-overlay').onclick = (e) => {
        if (e.target === e.currentTarget) closeModal();
    };

    return closeModal;
}

async function submitUpdate(form, postId, closeModal) {
    const submitBtn = form.querySelector('.btn-save');

    submitBtn.disabled = true;
    submitBtn.textContent = 'Sauvegarde...';

    try {
        const updateData = extractUpdateData(form);
        const result = await APIService.updatePost(postId, updateData);

        if (result.success) {
            Utils.message('Souvenir mis à jour avec succès!', 'success');
            closeModal();
            await loadMemoires();
        } else {
            Utils.handleAPIError(result.error);
        }
    } catch (error) {
        console.error('Erreur lors de la mise à jour du post:', error);
        Utils.message('Une erreur est survenue lors de la mise à jour du souvenir', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Sauvegarder';
    }
}

function extractUpdateData(form) {
    return {
        title: form.querySelector('#edit-post-title').value.trim(),
        description: form.querySelector('#edit-post-description').value.trim(),
        fishName: form.querySelector('#edit-post-fish').value.trim(),
        photoUrl: form.querySelector('#edit-post-photo').value.trim(),
        weight: form.querySelector('#edit-post-weight').value ? parseFloat(form.querySelector('#edit-post-weight').value) : null,
        length: form.querySelector('#edit-post-length').value ? parseFloat(form.querySelector('#edit-post-length').value) : null,
        location: form.querySelector('#edit-post-location').value.trim() || null,
        catchDate: form.querySelector('#edit-post-date').value ? new Date(form.querySelector('#edit-post-date').value).toISOString() : null
    };
}

function openDeleteModal(postId) {
    const template = document.getElementById('delete-confirmation-modal-template');
    const modal = template.content.cloneNode(true);

    const closeModal = setupModalEvents(modal);

    // Bouton confirmation
    modal.querySelector('.btn-delete-confirm').onclick = async () => {
        await confirmDelete(postId, closeModal);
    };

    // Affichage
    document.body.appendChild(modal);
    setTimeout(() => {
        const cancelBtn = modal.querySelector('.btn-cancel');
        if (cancelBtn) cancelBtn.focus();
    }, 100);
}

async function confirmDelete(postId, closeModal) {
    const confirmBtn = document.querySelector('.btn-delete-confirm');

    if (confirmBtn) {
        confirmBtn.disabled = true;
        confirmBtn.textContent = 'Suppression...';
    }

    try {
        const result = await APIService.deletePost(postId);

        if (result.success) {
            Utils.message('Souvenir supprimé avec succès!', 'success');
            closeModal();
            await loadMemoires();
        } else {
            Utils.handleAPIError(result.error);
        }
    } catch (error) {
        console.error('Erreur lors de la suppression du post:', error);
        Utils.message('Une erreur est survenue lors de la suppression du souvenir', 'error');
    } finally {
        if (confirmBtn) {
            confirmBtn.disabled = false;
            confirmBtn.textContent = 'Supprimer';
        }
    }
}

// ========== RAFRAÎCHISSEMENT ==========

function refreshMemoires() {
    loadMemoires();
}

// ========== DEBUG ==========
console.log('Journal.js chargé - Version ultra-simplifiée');