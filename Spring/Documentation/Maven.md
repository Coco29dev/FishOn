# Maven
__Outil de gestion__ projet __Java__ qui __automatise__ la construction des applications.

- __Gestion des dépendances__: Télécharge automatiquement les __bibliothèques__.
- __Structures standardisée__: Organisation projet selon __architecture commune__.
```bash
src/main/java        → Code source
src/main/resources   → Fichiers de config
src/test/java        → Tests
target/              → Fichiers compilés
```
- __Compilation automatique__: Transforme le code en _bytecode_.
- __Packaging__: Crée fichier `JAR/WAR` exécutables.

# Configuration
```xml
<!-- Dans pom.xml, vous écrivez juste : -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Maven fait automatiquement :
✅ Télécharge Spring Boot
✅ Télécharge toutes les dépendances de Spring Boot (30+ fichiers)
✅ Gère les versions compatibles
✅ Compile votre code
✅ Crée le .jar final
-->
```

# Structure Projet Maven
![Structure](img/StructureProjet.png "Structure Projety Maven")

# Fichier pom.xml
__Coeur de Maven__ contient:
- Informations projet.
- __Dépendances__ nécessaires.
- Plugins à utliser.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    
    <!-- INFORMATIONS DU PROJET -->
    <modelVersion>4.0.0</modelVersion>           <!-- Version de Maven -->
    <groupId>com.monnom</groupId>                <!-- Votre organisation -->
    <artifactId>mon-site-web</artifactId>       <!-- Nom du projet -->
    <version>1.0.0</version>                    <!-- Version de votre app -->
    <packaging>jar</packaging>                   <!-- Type de fichier final -->
    
    <!-- CONFIGURATION JAVA -->
    <properties>
        <maven.compiler.source>17</maven.compiler.source>    <!-- Version Java -->
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    
    <!-- DÉPENDANCES (librairies) -->
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
        
        <!-- Base de données H2 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.220</version>
        </dependency>
    </dependencies>
    
</project>
```

# Commandes Essentieles
```bash
mvn clean          # Nettoie le projet
mvn compile        # Compile le code
mvn test           # Lance les tests
mvn package        # Crée le JAR
mvn spring-boot:run # Lance l'app Spring Boot
```

# Cycle de Vie Maven
![Cycle](img/CycleVie.png "Cycle de vie Maven")