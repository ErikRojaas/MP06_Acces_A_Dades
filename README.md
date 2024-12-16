# Punt de prartida pràctica PR2.3 #

Organització del projecte

com.project.domain:
-------------------
Conté les entitats JPA
Representa els objectes del domini de negoci
Només conté la lògica relacionada amb les entitats


com.project.dao (Data Access Object):
-------------------------------------
Conté el Manager que gestiona l'accés a dades


### Compilació i funcionament ###

Cal el 'Maven' per compilar el projecte
```bash
mvn clean
mvn compile
mvn test
mvn package
```

Per executar el projecte a Windows cal
```bash
.\run.ps1 com.project.Main
```

Per executar el projecte a Linux/macOS cal
```bash
./run.sh com.project.Main
```