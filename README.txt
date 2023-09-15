--------------------------------Projet Annuaire NFA 032---------------------------------
--- Auteur: ------------------------------------
------- Olivier Baylac, -------------------------
-------------------------------------------------

IMPORTANT: I modified the original readme file I uploaded to be graded (It was a group project), but now that it's done I can say that I actually did it all by Myself because the other members of the group were way too busy (doing a project at four persons takes a lot of time (way more than we had available) to synchronize and distribute the work) 


notes sur le projet:

* pour vous simplifier l'utilisation, la boite de dialogue de connection est pré-remplie
  avec les identifiants du root Administrateur.

* pour tester le programme en tant que particulier vous pouvez utiliser l'identifiant suivant:
  user : test@hotmail.fr
  mot de passe : testeur

* Les mots de passe dans les fichiers sont cryptés via un alogorithme AES par
  enchainement des blocs (Cipher Block Chaining) avec remplissage (AES/CBC/PKCS5Padding)
  (ce n'était pas indispensable, mais c'est très cool!)

* l'apparence des éléments graphiques a été  gérée via le générater d'UI swing d'intelliJ. 
  Par défaut, ce générateur ordonne les éléments graphiques et règle leurs propriétés via des fichier .form (de type XML).
  Afin d'avoir un code source également utilisable sous Eclipse, nous avons utilisé le générateur en mode java,
  d'ou la présence de code java auto-généré au bas des fichiers contenus dans le package fr.cnam.group.gui.menus.

* La recherche par nom ou prénom et par date de naissance demande un résultat exact,
  alors que la recherche par identifiant (adresse mail)  recherche la valeur saisie dans les identifiants
  enregistrés.
