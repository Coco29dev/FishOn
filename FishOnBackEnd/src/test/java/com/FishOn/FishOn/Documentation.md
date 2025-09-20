# Test Unitaires
Vérification qu'__une seule unité de code__(__méthode__,__classe__) fonctionne correctement en __isolation__, sans dépendances exetrnes.

# FIRST
- __Fast__: Rapide à exécuter.
- __Independant__: Indépendant des autres tests.
- __Repeatable__: Résultat identiue à chaque exécution.
- __Self-validating__: Résultat binaire(succès/échec).
- __Timely__: Écrit au bon moment.

# Strucuture Test Unitaire

## Pattern AAA(Arange-Act-Assert)
```java
@Test
void nomDuTest() {
    // ARRANGE : Préparer les données de test
    
    // ACT : Exécuter la méthode à tester
    
    // ASSERT : Vérifier le résultat
}
```

# Mocking `@Mock`
Les `Services` dépendent des `repositories`, pour seulement tester le `Service` -> Simulation (mocker) le `repository`.

# Annotations JUnit5
- `@Test`: Marque une méthode de test.
- `@BeforeEach`: Exécuté avant chaque test.
- `@DisplayName`: Nom lisible du test.
- `@ParameterizedTest`: Test avec plusieurs jeux de données.

# Annotations Mockito
- `@Mock`: Crée un mock de la classe.
- `@InjectMocks`: Injecte les mocks dans la classe testée.
- `@ExtendWith(MockitoExtension.class)`: Active Mockito.

# Méthodes Mockito Essentielles
- `when().thenReturn()`: Définit le comportement du mock.
- `when().thenThrow()`: Simule une exception.
- `verify()`: Vérifie qu'une méthode a été appelée.
- `verifyNoInteractions()`: Vérifie qu'aucune interaction n'a eu lieu.