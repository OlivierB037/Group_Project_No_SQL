package fr.cnam.group.files;

import fr.cnam.group.users.Account;
import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;

import java.io.File;
import java.io.FileWriter;

import static fr.cnam.group.DataHandler.DATA_SEPARATOR;
import static fr.cnam.group.DataHandler.MAIN_SEPARATOR;

public class FilesHandler {
    //constantes de réglage du cryptage des mots de passe
    public static final char[] encryptionKey = "rootEncryptKey".toCharArray();
    public static final byte[] encryptionSalt = {-42, 94, -104, -16, -123, 42, 121, 6, -72, 30};
    public static final int encryptionIterations = 4000;
    public static final int encryptionKeyLength = 128;

    public static void clearFile(File file) throws Exception {
        FileWriter writer = new FileWriter(file);
        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
        String password = fileEncryption.encrypt("rootPassword");
        if(file instanceof Comptes) {
            writer.write(Comptes.ADMIN_SYMBOL + DATA_SEPARATOR + "rootAdmin" + DATA_SEPARATOR + password + MAIN_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }

    public static void addToFile(Account account) throws Exception {
        File comptes = new Comptes();
        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
        String encryptedPassword = fileEncryption.encrypt(String.valueOf(account.getPassword()));
        FileWriter fileWriter = new FileWriter(comptes,true);
        if (account instanceof Administrateur){
            fileWriter.write(Comptes.ADMIN_SYMBOL+account.getIdentifiant()+DATA_SEPARATOR);
            fileWriter.write(encryptedPassword);
            fileWriter.write(MAIN_SEPARATOR);
        }
        else if (account instanceof Particulier){
            System.out.println("writing particulier in file...");
            fileWriter.write(account.getIdentifiant()+ DATA_SEPARATOR+encryptedPassword+MAIN_SEPARATOR);
            Annuaire annuaire = new Annuaire();
            FileWriter particulierWriter = new FileWriter(annuaire, true);
            particulierWriter.write(account.getIdentifiant() + DATA_SEPARATOR + ((Particulier) account).getNom() + DATA_SEPARATOR + ((Particulier) account).getPrenom() + DATA_SEPARATOR +
                    ((Particulier) account).getDate_naissance() + DATA_SEPARATOR+ ((Particulier) account).getDate_modification() + DATA_SEPARATOR+ ((Particulier)  account).getTypeParticulier().toString()+ MAIN_SEPARATOR);

            particulierWriter.flush();
            particulierWriter.close();
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
