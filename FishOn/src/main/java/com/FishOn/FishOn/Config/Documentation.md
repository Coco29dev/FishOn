# Vue d'ensemble
__Couche__ `Config` constitue le __système de configuration de `Spring Security`__.

Définit les __règles d'authentification__, les __politiques de sécurité__ et __l'intégration__ entre le modèle utilisateur métier et le __module__ `Spring Security`.

Assure la __sécurisation__ de l'`API REST` et la __gestion des sessions__ utilisateurs.

## Configuration Spring Security
- __Définition__ des règles d'accès aux __endpoints__.
- __Configuration__ de l'authentification par __session `HTTP`__.
- __Intégration__ avec le système utilisateur métier.
- __Gestion__ des politiques de session.

## Intégration métier/sécurité
- __Pont__ entre `UserModel` et `UserDetails Spring Security`.
Service d'authentification personnalisé
Chargement des utilisateurs depuis la base de données
Adaptation du modèle métier aux interfaces Spring