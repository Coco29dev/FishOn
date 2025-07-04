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

  // ========== DONNÉES UTILISATEURS ==========
  static async loadUserProfile() {
      const result = await APIService.getProfile();

      if (!result.success) {
        throw new Error(result.error);
      }

      const profileData = result.data;

      const profilePicture = document.querySelector('.profile-picture');
      Utils.setupAvatar(profilePicture, profileData.profilePicture, profileData.userName);
      document.getElementById('userName').textContent = profileData.userName;
      document.getElementById('age').textContent = `${profileData.age} ans`;
  }

  // ========== GESTION PUBLICATIONS ==========
  static async loadUserPosts() {
    const result = await APIService.getCurrentUserPosts();
    if (!result.success) {
      throw new Error(result.error);
    }

    const posts = result.data;

    document.getElementById('userPostCount').textContent = posts.length;
    if (posts.length === 0) {
      return;
    }

    // Tri par date (plus récent en premier)
    const sortedPosts = Utils.sortPostsByDate(posts);

    // Affichage de la dernière publication
    this.displayLastPost(sortedPosts[0]);

    // Affichage des souvenirs
    if (sortedPosts.length > 1) {
      this.displayMemories(sortedPosts.slice(1));
    }
  }

  static displayLastPost(post) {
    // Récupération template
    const template = document.getElementById('lastTemplate');
    const lastPost = template.content.cloneNode(true);

    // Récupération image
    const fishPicture = lastPost.querySelector('.fish-picture');
    Utils.setupPostImage(fishPicture, post.photoUrl);

    // Récupération champs obligatoires
    lastPost.querySelector('.post-title').textContent = post.title;
    lastPost.querySelector('.post-description').textContent = post.description;
    lastPost.querySelector('.post-fishname').textContent = `Poisson: ${post.fishName}`;

    // Récupération champs optionnels
    if (post.weight) {
      lastPost.querySelector('.post-weight').textContent = `Poids: ${post.weight} kg`;
    }
    if (post.length) {
      lastPost.querySelector('.post-length').textContent = `Longueur: ${post.length} cm`;
    }
    if (post.location) {
      lastPost.querySelector('.post-location').textContent = `Lieu: ${post.location}`;
    }

    // Date de création
    lastPost.querySelector('.post-date').textContent = Utils.formatDate(post.createdAt);

    // Injection dans le DOM
    const container = document.getElementById('lastPost');
    container.appendChild(lastPost);
  }

  static displayMemories(posts) {
    const container = document.getElementById('postsMemories');
    const template = document.getElementById('memoriesTemplate');
  
    // Boucle sur toutes les publications "souvenirs"
    posts.forEach(post => {
      // Récupération template
      const memory = template.content.cloneNode(true);
  
      // Récupération image
      const fishPicture = memory.querySelector('.fish-picture');
      Utils.setupPostImage(fishPicture, post.photoUrl);
  
      // Récupération champs obligatoires
      memory.querySelector('.post-title').textContent = post.title;
      memory.querySelector('.post-description').textContent = post.description;
      memory.querySelector('.post-fishname').textContent = `Poisson: ${post.fishName}`;
  
      // Récupération champs optionnels
      if (post.weight) {
        memory.querySelector('.post-weight').textContent = `Poids: ${post.weight} kg`;
      }
      if (post.length) {
        memory.querySelector('.post-length').textContent = `Longueur: ${post.length} cm`;
      }
      if (post.location) {
        memory.querySelector('.post-location').textContent = `Lieu: ${post.location}`;
      }
  
      // Date de création
      memory.querySelector('.post-date').textContent = Utils.formatDate(post.createdAt);
  
      // Injection dans le DOM
      container.appendChild(memory);
    });
  }
}
document.addEventListener('DOMContentLoaded', Profile.init);