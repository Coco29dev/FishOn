// ========== Initialisation ==========
// DOMContentLoaded = Attente Mise en place HTML avant exécution script
document.addEventListener('DOMContentLoaded', function () {
  // Récupération références des boutons de navigation
  const journalBtn = document.getElementById('journalBtn');
  const feedBtn = document.getElementById('feedBtn');
  const logoutBtn = document.getElementById('logoutBtn');

  // Gestionnaire redirection via bouton nav bar
  if (journalBtn) {
    journalBtn.addEventListener('click', function () {
      window.location.href = 'journal.html';
    });
  }

  if (feedBtn) {
    feedBtn.addEventListener('click', function () {
      window.location.href = 'feed.html';
    });
  }

  if (logoutBtn) {
    logoutBtn.addEventListener('click', async function () {
      // Appel API pour déconnexion
      const result = await logoutAPI();

      // Cas de succès : déconnexion utilisateur
      if (result.success) {
        message('Déconnexion réussi!', 'success');
        // Redirection page de connexion
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
  // Chargement initial du profil
  loadProfile();
});

// ========== Fonctions de gestion du profil ==========
async function loadProfile() {
  // Récupération des éléments d'interface pour la gestion des états
  const loadingMessage = document.getElementById('loadingMessage');
  const errorMessage = document.getElementById('errorMessage');
  const lastPost = document.getElementById('lastPost');
  const postsMemories = document.getElementById('postsMemories');
  try {
    // Affichage état de chargement
    loadingMessage.style.display = 'block';
    errorMessage.style.display = 'none';
    lastPost.style.display = 'none';
    postsMemories.style.display = 'none';

    const result = await getProfileAPI();
  }
}

// ========== Fonctions de gestion appel API ==========
async function getProfileAPI() {
  try {
    // Récupération données utilisateur
    const userResponse = await apiCall('/users/me', { method: 'GET' });
    const userData = await userResponse.json;

    // Récupération publications utilisateurs
    const postsResponse = await apiCall(`/posts/${userData.usrername}`, { method: 'GET' });
    const postsData = await postsResponse.json;

    // Calcul compteur de prises
    const postsCount = postsData.length;

    // Fusion des données
    return {
      success: true,
      data: {
        ...userData,
        posts: postsData,
        postsCount: postsCount
      }
    };
  } catch (error) {
    
  }
}