package fr.cnam.group.files;

import java.io.File;

public class Comptes extends File {
    public static final String ACCOUNT_FILE_PATH = "Accounts.txt";
    public static final String ADMIN_SYMBOL = "#";// symbole différenciant les admins des particuliers dans le fichier des comptes

    public Comptes() {
        super(ACCOUNT_FILE_PATH);
    }
}
