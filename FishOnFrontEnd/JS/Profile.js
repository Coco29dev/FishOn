class Profile {
  static async init() {
    try {
      // Masquage message d'état erreur
      document.getElementById('errorMessage').style.display = 'none';
      // Affichage message d'état chargement
      document.getElementById('loadingMessage').style.display = 'block';
      // Configuration bouton de navigation(navbar)
      this.setupNavigation();
      // Chargement des données utilisateur
      await this.loadUserProfile();
      // Chargement des publications
      await this.loadUserPosts();
      // Masquage message d'état chargement
      document.getElementById('loadingMessage').style.display = 'none';
    } catch (error) {
      // Masquage message d'état chargement
      document.getElementById('loadingMessage').style.display = 'none';
      // Gestion erreurs d'authentification
      if (error.message.includes('authentification') || error.message.includes('401')) {
        Utils.message('Erreur survenue lors de l\'authentification.');
        Utils.redirectTo('login.html', 2000);
      } else {
        Utils.message(error.message, 'error');
      }
    }
  }
  // ========== NAVIGATION ==========
  static setupNavigation() {
    const feedBtn = document.getElementById('feedBtn');
    const journalBtn = document.getElementById('journalBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (feedBtn) feedBtn.onclick = () => Utils.redirectTo('feed.html');
    if (journalBtn) journalBtn.onclick = () => Utils.redirectTo('journal.html');
    if (logoutBtn) logoutBtn.onclick = this.handleLogout;  
  }

  static async handleLogout() {
    const result = await APIService.logout();

    if (result.success) {
        Utils.message('Déconnexion réussie!', 'success');
        Utils.redirectTo('login.html', 1000);
    } else {
        Utils.message(result.error, 'error');
    }
  }
}

document.addEventListener('DOMContentLoaded', Profile.init);