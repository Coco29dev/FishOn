// SERVICE CENTRALISÉ APPEL API
class APIService {
    // ======== AUTHENTIFICATION ========
    static async register(registerData) {
        // Vérification validité paramètres
        if (!registerData.email || !registerData.password || !registerData.userName ||
            !registerData.firstName || !registerData.lastName || !registerData.age) {
            return {success: false, error: 'Tous les champs sont obligatoires'};
        }

        try {
            // Appel API
            const response = await API.post('/auth/register', registerData); // Attend réponse HTTP
            // Extraction données JSON
            const userData = await response.json(); // Attend que le parsing soit fini
            return { success: true, data: userData };
        } catch (error) {
            // Gestion des erreurs
            const message = ErrorHandler.getErrorMessage(error, 'auth');
            return { success: false, error: message };
        }
    }

    static async login(loginData) {
        // Validation paramètres
        if (!loginData.email || !loginData.password) {
            return { success: false, error: 'Email ou mot de passe manquant' };
        }

        try {
            // Appel API
            const response = await API.post('/auth/login', loginData);
            // Extraction données JSON
            const userData = await response.json();
            return { success: true, data: userData };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'auth');
            return { success: false, error: message };
        }
    }

    static async logout() {
        try {
            // Appel API
            const response = await API.post('/auth/logout');
            // Extraction réponse
            const result = await response.text(); // Récupération string retour logout controller via .text()
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'logout');
            return { success: false, error: message };
        }
    }

    // ======== PUBLICATION ========
    static async getFeed() {
        try {
            // Appel API
            const response = await API.get('/posts/feed');
            // Extraction des données
            const result = await response.json();
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'feed');
            return { success: false, error: message };
        }
    }

    static async getCurrentUserPosts() {
        try {
            const userResult = await this.getProfile();
            if (!userResult.success) return userResult;

            const userName = userResult.data.userName;
            const response = await API.get(`/posts/${userName}`);
            const postsData = await response.json();

            return { success: true, data: postsData };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'feed');
            return { success: false, error: message };
        }
    }

    static async createPost(postData) {
        if (!postData.title || !postData.description || !postData.fishName || !postData.photoUrl) {
            return { success: false, error: 'Tous les champs obligatoires doivent être remplis' };
        }

        try {
            // Appel API
            const response = await API.post('/posts', postData);
            // Extraction des données
            const result = await response.json();
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'feed');
            return { success: false, error: message };
        }
    }

    static async updatePost(postId, postData) {
        // Validation paramètres
        if (!postId) {
            return { success: false, error: 'ID du post requis' };
        }

        if (!postData.title || !postData.description || !postData.fishName) {
            return { success: false, error: 'Titre, description et nom du poisson sont obligatoires' };
        }

        try {
            // Appel API
            const response = await API.put(`/posts/${postId}`, postData);
            // Extraction des données
            const result = await response.json();
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'feed');
            return { success: false, error: message };
        }
    }

    static async deletePost(postId) {
        // Validation paramètre
        if (!postId) {
            return { success: false, error: 'ID du post requis' };
        }

        try {
            // Appel API
            const response = await API.delete(`/posts/${postId}`);
            // Pour delete, souvent juste un message de confirmation
            const result = await response.text();
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'feed');
            return { success: false, error: message };
        }
    }

    // ======== GESTION DES COMMENTAIRES ========

    static async createComment(postId, commentData) {
        // Validation paramètres
        if (!postId) {
            return { success: false, error: 'ID du post requis' };
        }

        if (!commentData.content || commentData.content.trim() === '') {
            return { success: false, error: 'Le contenu du commentaire est obligatoire' };
        }

        try {
            // Appel API
            const response = await API.post(`/comments/post/${postId}`, commentData);
            // Extraction des données
            const result = await response.json();
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'comment');
            return { success: false, error: message };
        }
    }

    // ======== GESTION DU PROFIL ========

    static async getProfile() {
        try {
            const response = await API.get('/users/me');  // ou '/users/profile' selon ton API
            const userData = await response.json();
            return { success: true, data: userData };
        } catch (error) {
            // Gestion d'erreur plus générique avec ErrorHandler
            const message = ErrorHandler.getErrorMessage(error, 'profile');

            return { success: false, error: message };
        }
    }

    static async updateProfile(profileData) {
        // Validation des champs obligatoires
        if (!profileData.userName || !profileData.email || !profileData.firstName ||
            !profileData.lastName || !profileData.age) {
            return { success: false, error: 'Nom d\'utilisateur, email, prénom, nom et âge sont obligatoires' };
        }

        try {
            // Appel API
            const response = await API.put('/users/profile', profileData);
            // Extraction des données
            const result = await response.json();
            return { success: true, data: result };
        } catch (error) {
            const message = ErrorHandler.getErrorMessage(error, 'auth');
            return { success: false, error: message };
        }
    }
}