/*
 * Nom de classe : Annuaire
 *
 * Description   : gère le fichier Annuaire
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */
package fr.cnam.group.files;

import java.io.File;

public class Annuaire extends File {

    public static final String ANNUAIRE_FILE_PATH = "resources/Annuaire.txt";

    public Annuaire() {
        super(ANNUAIRE_FILE_PATH);
    }
}
