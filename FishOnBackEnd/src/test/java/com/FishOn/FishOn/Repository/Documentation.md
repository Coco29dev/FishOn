# Architecture Test
- Test de la __couche persistance__ avec __BDD__.
- Validation __requête JPA__ génées automatiquement.
- Vérification __mappings__ entre entités `Java` et tables `SQL`.

# Outils et framework
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-test</artifactId>
	<scope>test</scope>
</dependency>
```

__JUnit5__: Framework de test principal
- `@Test`.
- `@DisplayName`: Documentation lisible des test.

__Spring Boot Test__: Intégration écosystème `Spring`
- `@DataJpaTest`: Slice-Testing - charge uniquement la __couche JPA__.
- `@Autowired`: Injection des dépendances(`repository`, `entityManager`).
- `@TestPropertySource`: Configuration spécifique des test.

__AssertJ__: Bibliothèque d'assertions fluides

__H2 Database__: BDD embarquée pour test
- Remplace `PostgreSQL`.
- Base en mémoire rapide et isolé.
- Détruite après chaque test.

# Pattern
__Slice Testing__: Test uniquement la __couche Repository__ sans charger tout le contexte `Spring`.

__AAA__:
- __ARRANGE__: Préparation des données.
- __ACT__: Appel des méthodes. 
- __ASSERT__: Vérification des résultats avec `AssertJ`.

