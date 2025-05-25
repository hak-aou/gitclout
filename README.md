# Projet **GitClout**

#### *Date : 2023*
#### *Niveau : M1 S1*
#### *Membres : Hakim AOUDIA, Nafis Basheer Ahamed*
#### *Note obtenue : 13/20*

## Présentation

**GitClout** est une application web d’analyse de dépôts GitHub publics.  
Elle combine un **backend Java 17 / Spring Boot** chargé de cloner, parser et agréger les données des commits, et un **frontend JavaScript (React)** qui affiche :

* la liste des *tags* du dépôt ;
* un **graphique en bâtons empilés.** des contributions (type de fichier par contributeur) ;
* un **graphique radar** individuel pour chaque contributeur.

L’objectif : visualiser la contribution d'une personne dans un dépôt git.

---

## Capture d’écran
<!-- Remplacez l’URL par une vraie capture de l’application -->
![image](https://github.com/user-attachments/assets/c1c7f22e-9350-4dcd-bbfe-c210b32e6f0b)

---

## Fonctionnalités principales

* **Analyse multi-dépôts** : ajoutez n’importe quelle URL GitHub ou GitLab publique.  
* **Extraction des tags** et stockage de leur date.  
* **Visualisations interactives** :  
  * **Barres empilées** – types de fichier (*CODE, BUILD, DOC, RESOURCES…*) par auteur.  
  * **Radars** – top 5 des types de fichier où chaque auteur contribue le plus.  
* **Tri dynamique** : radars ordonnés du plus au moins contributif.  
* **Cache local** : évite de re-télécharger un dépôt déjà analysé.  
* **Interface responsive** (Svelte + Chart.js) et thème sombre/clair.  
* **Script unique** : build & run en une seule commande (`sh run.sh`).  

---

## Stack technique

| Couche        | Technologies                                                  | Rôle principal                                   |
|---------------|--------------------------------------------------------------|--------------------------------------------------|
| Backend       | **Java 17 – Helidon 3 MP Reactive**                          | API + logique d’analyse des dépôts               |
| Persistance   | **JPA / Hibernate**                                          | Mapping objet ↔ relationnel                      |
| Base de données | **HyperSQL** (embedded)                                    | Stockage dépôts, tags, contributions             |
| Frontend      | **Svelte**                                                   | Application monopage réactive                    |
| UI            | **Semantic UI**                                              | Composants CSS & layout                          |
| Graphiques    | **Chart.js**                                                 | Barres empilées & radars                         |

### Lancement du projet
```bash
# À la racine du dépôt
$ sh run.sh
```

### Utilisation du projet

1. **Saisir** ou **sélectionner** une URL Git publique, puis valider (bouton « Analyser » ou *Entrée*).
2. Les **tags** apparaissent sur la page d’accueil avec la date d’ajout du dépôt.
3. **Cliquer** sur un tag ouvre la page d’analyse détaillée.
4. Explorer le **graphique en barres empilées** (contributeurs × types de fichier).
5. Faire défiler les **radars** individuels pour comparer les profils des contributeurs.


