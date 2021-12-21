package fr.cnam.group;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataHandler {




    public static HashMap<String, Particulier> annuaire = new HashMap<>();
    public static HashMap<String, Administrateur> listeAdmins = new HashMap<>();

    //constantes contenant les infos d'écriture et d'ouverture des fichiers
    public static final char MAIN_SEPARATOR = '\n';
    public static final char DATA_SEPARATOR = ';';
    public static final String ROOT_ADMIN_ID = "rootAdmin";
    public static final String ACCOUNT_FILE_PATH = "Accounts.txt";
    public static final String ADMIN_SYMBOL = "#";// symbole différenciant les admins des particuliers dans le fichier des comptes
    public static final String ANNUAIRE_FILE_PATH = "Annuaire.txt";

    public static Account currentUser = null; // utilisateur connecté

    //constantes de réglage du cryptage des mots de passe
    static final char[] encryptionKey = "rootEncryptKey".toCharArray();
    static final byte[] encryptionSalt = {-42, 94, -104, -16, -123, 42, 121, 6, -72, 30};
    static final int encryptionIterations = 4000;
    static final int encryptionKeyLength = 128;


    public static boolean isIdentifiantavailable(String identifiant){
        AtomicBoolean available = new AtomicBoolean(true);
        listeAdmins.forEach((id, admin) ->{
            if(identifiant.equals(id)){
                System.out.println("l'identifiant " + identifiant + " est déja utilisé");
                available.set(false);
            }
        });
        return available.get();

    }




    public static boolean addAdminToDatabase(Administrateur admin) throws Exception { //enregistre l'admninistrateur dans le HashMap listeAdmins
        if(listeAdmins.putIfAbsent(admin.getIdentifiant(),admin) == null){
            System.out.println("admin ajouté au système");
            return true;
        }
        else{
            throw new Exception("erreur lors de l'ajout au système");
        }
    }

    public static boolean addParticulierToDatabase(Particulier particulier) throws Exception { // enregistre le Particulier dans le HashMap annuaire

        if (annuaire.putIfAbsent(particulier.getIdentifiant(),particulier) == null) {
            System.out.println("particulier ajouté au système");
            return true;

        } else {
            throw new Exception("erreur lors de l'ajout dans l'annuaire");
        }
    }


//    public static boolean addParticulierToFile(Particulier particulier) { //inscrit le Particulier dans le fichier de sauvegarde des comptes et de l'annuaire
//        File saveFile = new File(ANNUAIRE_FILE_PATH);
//
//        try {
//
//            FileWriter fileWriter = new FileWriter(saveFile, true);
//            fileWriter.write(particulier.getIdentifiant() + DATA_SEPARATOR + particulier.getNom() + DATA_SEPARATOR + particulier.getPrenom() + DATA_SEPARATOR +
//                    particulier.getDate_naissance() + DATA_SEPARATOR+ particulier.getDate_modification() + MAIN_SEPARATOR);
//
//            fileWriter.flush();
//            fileWriter.close();
//            FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
//            String encryptedPassword = fileEncryption.encrypt(String.valueOf(particulier.getPassword()));
//            fileWriter = new FileWriter(ACCOUNT_FILE_PATH,true);
//            fileWriter.write(particulier.getIdentifiant()+ DATA_SEPARATOR+encryptedPassword+MAIN_SEPARATOR);
//            fileWriter.flush();
//            fileWriter.close();
//
//            return true;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
//
//    }

    static void clearFile(File file) throws Exception {
        FileWriter writer = new FileWriter(file);
        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
        String password = fileEncryption.encrypt("rootPassword");
        if(file.getPath().equals(ACCOUNT_FILE_PATH)) {
            writer.write(ADMIN_SYMBOL + DATA_SEPARATOR + "rootAdmin" + DATA_SEPARATOR + password + MAIN_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
    static void addToFile(Account account) throws Exception {
        File file = new File(ACCOUNT_FILE_PATH);
        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
        String encryptedPassword = fileEncryption.encrypt(String.valueOf(account.getPassword()));
        FileWriter fileWriter = new FileWriter(file,true);
        if (account instanceof Administrateur){
            fileWriter.write(ADMIN_SYMBOL+account.getIdentifiant()+DATA_SEPARATOR);
            fileWriter.write(encryptedPassword);
            fileWriter.write(MAIN_SEPARATOR);
        }
        else if (account instanceof Particulier){
            System.out.println("wrtiting particulier in file...");
            fileWriter.write(account.getIdentifiant()+ DATA_SEPARATOR+encryptedPassword+MAIN_SEPARATOR);
            File particulierFile = new File(ANNUAIRE_FILE_PATH);
            FileWriter particulierWriter = new FileWriter(particulierFile, true);
            particulierWriter.write(account.getIdentifiant() + DATA_SEPARATOR + ((Particulier) account).getNom() + DATA_SEPARATOR + ((Particulier) account).getPrenom() + DATA_SEPARATOR +
                    ((Particulier) account).getDate_naissance() + DATA_SEPARATOR+ ((Particulier) account).getDate_modification() + MAIN_SEPARATOR);

            particulierWriter.flush();
            particulierWriter.close();
        }



        fileWriter.flush();
        fileWriter.close();
    }




//    static void addAdminToFile(Account admin) throws Exception { //inscrit l'Administrateur dans le fichier de sauvegarde des comptes
//        File file = new File(ACCOUNT_FILE_PATH);
//
//        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
//        String encryptedPassword = fileEncryption.encrypt(String.valueOf(admin.getPassword()));
//        FileWriter fileWriter = new FileWriter(file,true);
//        fileWriter.write(ADMIN_SYMBOL+admin.getIdentifiant()+DATA_SEPARATOR);
//        fileWriter.write(encryptedPassword);
//        fileWriter.write(MAIN_SEPARATOR);
//        fileWriter.flush();
//        fileWriter.close();
//
//
//    }





    public static void loadData(File file, Class<? extends Account> addedClass ) { //lire les fichiers de sauvegarde et générer les données

        ArrayList<String> dataGroups = new ArrayList<>();
        Scanner scanner = null;
        try {
            FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);// classe générant le cryptage
            if ((file.exists() ) ){ // vérification de la présence du fichier
                if (file.getName().equals(ACCOUNT_FILE_PATH)) {
                    System.out.println("rootAdmin verification : admin file is present");
                    if(new BufferedReader(new FileReader(file)).lines().anyMatch(s ->  s.contains(ROOT_ADMIN_ID+DATA_SEPARATOR) )){// vérification de la présence du superUtilisateur rootAdmin
                        System.out.println("rootAdmin verification :root admin found in file");
                    }
    //                new BufferedReader(new FileReader(file)).lines().forEach(s->{
    //                    System.out.println("reading : "+ s);
    //                    if (s.contains("rootAdmin"+DATA_SEPARATOR)){
    //                        System.out.println("match found");
    //                    }
    //                });
                    else{
                        System.out.println("rootAdmin verification : Root admin not in file, creating root administrator");

                        String password = fileEncryption.encrypt("rootPassword");
                        //System.out.println("encrypted password : " + password);
                        new FileWriter(file,true) {
                            {
                                write(ADMIN_SYMBOL+DATA_SEPARATOR+"rootAdmin"+DATA_SEPARATOR+password+MAIN_SEPARATOR);
                                flush();
                                close();
                            }
                        };
                    }
                }

            }else{
                System.out.println("rootAdmin verification : file " + file.getName() + " does not exist\n creating file..." );
                if (file.createNewFile()){
                    if(file.getName().equals(ACCOUNT_FILE_PATH)){
                        System.out.println("rootAdmin verification : creating root administrator");

                        String password = fileEncryption.encrypt("rootPassword");

                        new FileWriter(file) {
                            {
                                write(ADMIN_SYMBOL+DATA_SEPARATOR+"rootAdmin"+DATA_SEPARATOR+password+MAIN_SEPARATOR);
                                flush();
                                close();
                            }
                        };
                    }
                }
            }


            scanner  = new Scanner(file);
            int i = 0;
            while (scanner.hasNext()) {
                dataGroups.add(i, scanner.nextLine());
                System.out.println("reading users in file : data group " + i + " : " + dataGroups.get(i));
                i++;

            }

//            String[] readData = null;
//
//            if (addedClass == Particulier.class){
//                for (String s : dataGroups) {
//
//                    if (s != null) {
//                        System.out.println("reading users datas : data : " + s);
//                        readData = s.split(String.valueOf(DATA_SEPARATOR));
//                        try{
//                            File accountFile = new File(ACCOUNT_FILE_PATH);
//                            scanner = new Scanner(accountFile);
//                            String str = "";
//                            while(scanner.hasNext()){
//                                str = scanner.nextLine();
//                                System.out.println("loadData : particulier : reading in accounts "+str);
//                                if (!(str.startsWith("#")) && str.contains(readData[0])){
//                                    System.out.println("identifiant found in file");
//                                    break;
//                                }
//                                else{
//                                    throw new Exception("can't find identifiant"+readData[0]+" in account file");
//                                }
//
//
//                            }
//                            str = fileEncryption.decrypt(str.split(String.valueOf(DATA_SEPARATOR))[1]);
//                            //System.out.println("password is "+ str);
//                            addParticulierToDatabase(new Particulier(readData[1], readData[2], readData[3],readData[4],readData[0],str.toCharArray()));
//                        }catch (Exception e){
//
//                        }
//                    }
//                }
//            }
//            else if (addedClass == Administrateur.class){
//
//            }


            for (String s : dataGroups) {

                if (s != null) {
                    String[] readData = null;
                    //System.out.println("reading users datas : data : " + s);
                    readData = s.split(String.valueOf(DATA_SEPARATOR));
                    //System.out.println("size of readData: " + readData.length);
                    for (String str : readData) {

                        if (str != null) {
                            System.out.println("reading users data : " + str);
                        }
                    }
                    //System.out.println("ajout des données utilisateur");
                    try {
                        if (addedClass == Particulier.class) {
//                            if (readData[0].equals("#")){
//                                System.out.println("loadData : particulier : skipping admin " + readData[1]);
//                                continue;
//                            }
                            File accountFile = new File(ACCOUNT_FILE_PATH);
                            scanner = new Scanner(accountFile);
                            String str = "";
                            while(scanner.hasNext()){
                                str = scanner.nextLine();
                                System.out.println("loadData : particulier : reading in accounts "+str);
                                if (!(str.startsWith("#")) && str.contains(readData[0])){
                                    System.out.println("identifiant found in file");
                                    break;
                                }



                            }
                            str = fileEncryption.decrypt(str.split(String.valueOf(DATA_SEPARATOR))[1]);
                            //System.out.println("password is "+ str);
                            addParticulierToDatabase(new Particulier(readData[1], readData[2], readData[3],readData[4],readData[0],str.toCharArray()));
                        }
                        else if (addedClass == Administrateur.class){
                            if (readData[0].equals("#")) {
                                System.out.println("loadData : admin : adding " + readData[1]);
                                addAdminToDatabase(new Administrateur(readData[1], fileEncryption.decrypt(readData[2]).toCharArray()));
                                //System.out.println("decrypted password : " + fileEncryption.decrypt(readData[2]));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }



    }



    public static void main(String[] args) throws Exception {

//      try {
//          currentUser = new Administrateur(new PasswordAuthentication("rootAdmin","rootPassword".toCharArray()));
//
//
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
        System.out.println("date test 21/11/2005 :" + Particulier.isDateFormatOk("21/11/2005"));
        System.out.println("date test 10/13/2005 :" + Particulier.isDateFormatOk("10/13/2005"));
        System.out.println("date test 2005/11/10 :" + Particulier.isDateFormatOk("2005/11/10"));
        System.out.println("mc cormick is : " + Particulier.isNameFormatOk(Particulier.formatNames("mc cormick")));
        System.out.println(" jean pierre  is : " + Particulier.isNameFormatOk("jean pierre"));
        System.out.println("mail sans fin is : "+ Administrateur.isIdentifiantFormatOk("aperikub@hotmail."));
        System.out.println("mail sans point : "+ Administrateur.isIdentifiantFormatOk("aperikub@hotmailfr"));
        System.out.println("mail point is : "+ Administrateur.isIdentifiantFormatOk("aperikubhotmail.fr"));
        //System.out.println("saumon length : " + "saumon".toCharArray().length);
        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);

//        for (int i = 0;i<100;i++) {
//            System.out.println(fileEncryption.encrypt("tentative de debusquage du symbole"));
//        }
        new Thread(() -> {
            System.out.println("loading Admins");
            loadData(new File(ACCOUNT_FILE_PATH), Administrateur.class);
            System.out.println("loading particuliers");
            loadData(new File(ANNUAIRE_FILE_PATH), Particulier.class);
        }).start();

        listeAdmins.forEach((id, admin)->{
            System.out.println("admin : " + id);
        });
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel()); // application du "skin" Nimbus à l'interface graphique
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        try {
            MyWindow myWindow = new MyWindow(); // classe héritant de JFrame, la fenêtre de l'UI, qui contient tous les éléments graphiques affichés


            MenuPrincipal menuPrincipal = new MenuPrincipal(myWindow);
            menuPrincipal.init(); //initialisation du menu principal

            myWindow.setVisible(true);
            myWindow.pack();
            /*ouverture du menu principal*/


            myWindow.setContentPane(menuPrincipal.getMenuPrincipalPanel()); // aplication du menu principal dans la fenêtre
            myWindow.setMinimumSize(new Dimension(600,600));
        } catch (ClassCastException e) {
            System.out.println("class cast Exception caught --------------------");
        }


    }



}