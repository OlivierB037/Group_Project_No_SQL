/*
 * Nom de classe : Comptes
 *
 * Description   : gère le fichier des comptes
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

public class Comptes extends File {
    public static final String ACCOUNT_FILE_PATH = "resources/Accounts.txt";
    public static final String ADMIN_SYMBOL = "#";// symbole différenciant les admins des particuliers dans le fichier des comptes

    public Comptes() {
        super(ACCOUNT_FILE_PATH);
    }
}
