class ErrorHandler {
  static getErrorMessage(error, context) {
    const errorMessage = error.message;

    // Erreurs d'authentification
    if (errorMessage.includes('Email incorrect')) {
      return 'Adresse email incorrecte';
    }
    else if (errorMessage.includes('Mot de passe incorrect')) {
      return 'Mot de passe incorrect';
    }
    
    // Erreurs d'inscription
    else if (errorMessage.includes('est déjà pris')) {
      if (errorMessage.includes('email')) {
        return 'Cette adresse email est déjà utilisée';
      } else if (errorMessage.includes('username')) {
        return 'Ce nom d\'utilisateur est déjà pris';
      }
    }
    
    // Erreurs de validation posts
    else if (errorMessage.includes('Titre obligatoire')) {
      return 'Le titre est requis';
    }
    else if (errorMessage.includes('Description obligatoire')) {
      return 'La description est requise';
    }
    else if (errorMessage.includes('fishName obligatoire')) {
      return 'Le nom du poisson est requis';
    }
    else if (errorMessage.includes('Photo obligatoire')) {
      return 'Une photo est requise';
    }
    
    // Erreurs génériques API.js
    else if (errorMessage.includes('Ressource non trouvée')) {
      return 'Ce que vous recherchez n\'existe pas';
    }
    else if (errorMessage.includes('Erreur serveur')) {
      return 'Erreur serveur, veuillez réessayer plus tard';
    }
    else if (errorMessage.includes('Impossible de contacter')) {
      return 'Impossible de contacter le serveur';
    }
    
    // Cas par défaut
    else if (context === 'auth') {
      return 'Problème de connexion';
    }
    else if (context === 'feed') {
      return 'Erreur lors du chargement du feed';
    } else if (context === 'logout') {
      return 'Erreur survenu lors de la déconnexion';
    }
    else {
      return 'Une erreur inattendue est survenue';
    }
  }
}