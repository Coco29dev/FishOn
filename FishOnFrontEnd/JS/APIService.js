// SERVICE CENTRALISÉ APPEL API
class APIService {
  // AUTHENTIFICATION
  static register(registerData) {
    // Vérification validité paramètres
    if (!registerData.email || !registerData.password || !registerData.username ||
      !registerData.firstname || !registerData.lastname || !registerData.age) {
      return {success: false, error: 'Tous les champs sont obligatoires'};
    }
  }
}