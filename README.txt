--------------------------------Projet Annuaire NFA 032---------------------------------
--- Auteurs: ------------------------------------
------- Steven Besnard, ------------------------- 
------- Agnes Laurencon, ------------------------
------- Olivier Baylac, -------------------------
------- Benjamin Launay -------------------------
-------------------------------------------------

notes sur la réalisation du projet : 

* Nous avons essayé, malgré le peu de temps libre dont chacun de nous disposait 
  et l'absence totale de créneaux temporels durant lequel nous etions tous disponible, 
  de suivre le cahier des charges au plus près tout en nous efforçant de couvrir la
  totalité du programme.

* Les suppléments (interface graphique swing, cryptage des mots de passe, utilisation de AbstractTableModel)
bien que non demandés nous ont permis d'apprendre énormément, n'est-ce pas la le plus important ? 

* Nous avons également essayé de nous répartir le travail le plus équitablement possible
  cependant lors du partage des taches nous avons du prendre en compte nos situations
  familiales, professionnelles et la disparité dans le nombre d'UE que chacun de nous suit.

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
