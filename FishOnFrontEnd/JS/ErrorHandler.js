// Gestionnaire centralisé des erreurs
class ErrorHandler {
  // Gestion message d'erreur
  static getErrorMessage(error, context) {
    // Récupération message d'erreur
    const errorMessage = error.message;

    // Erreur Authentification
    if (errorMessage.includes('Identifiants incorrects')) {
      // Conversion message serveur vers notre message personnalisé
      return 'Email ou mot de passe incorrect';
    }
    // Erreur d'inscription
    else if (errorMessage.includes('email') && errorMessage.includes('déjà')) {
      return 'Adresse mail déjà existante';
    }
    // Erreur d'inscription
    else if (errorMessage.includes('username') && errorMessage.includes('déjà')) {
      return 'Nom d\'utilisateur déjà existant';
    }
    // Erreur Ressource
    else if (errorMessage.includes('non trouvée')) {
      // Conversion message serveur vers notre message personnalisé
      return 'Ce que vous recherchez n\'existe pas';
    }
    // Erreur Serveur
    else if (errorMessage.includes('Erreur serveur')) {
      // Conversion message serveur vers notre message personnalisé
      return 'Erreur serveur, veuillez réessayer plus tard';
    }
    // Erreur Réseau
    else if (errorMessage.includes('Impossible de contacter')) {
      // Conversion message serveur vers notre message personnalisé
      return 'Erreur connexion serveur, veuillez réessayer plus tard';
    }
    else if (context === 'auth') {
      return 'Erreur survenue lors de l\'authentification';
    }
    else if (context === 'feed') {
      return 'Erreur survenue lors du chargement du feed';
    }
    else if (context === 'comment') {
      return 'Errreur survenue lors de l\'ajout du commentaires';
    }
    else if (context === 'logout') {
      return 'Erreur survenue lors de la déconnexion';
    }
    else {
      return 'Une erreur innatendue est survenue';
    }
  }
}