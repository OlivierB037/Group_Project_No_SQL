package fr.cnam.group;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
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

    static boolean loadReady;


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
            System.out.println("writing particulier in file...");
            fileWriter.write(account.getIdentifiant()+ DATA_SEPARATOR+encryptedPassword+MAIN_SEPARATOR);
            File particulierFile = new File(ANNUAIRE_FILE_PATH);
            FileWriter particulierWriter = new FileWriter(particulierFile, true);
            particulierWriter.write(account.getIdentifiant() + DATA_SEPARATOR + ((Particulier) account).getNom() + DATA_SEPARATOR + ((Particulier) account).getPrenom() + DATA_SEPARATOR +
                    ((Particulier) account).getDate_naissance() + DATA_SEPARATOR+ ((Particulier) account).getDate_modification() + DATA_SEPARATOR+ ((Particulier)  account).getTypeParticulier().toString()+ MAIN_SEPARATOR);

            particulierWriter.flush();
            particulierWriter.close();
        }
        fileWriter.flush();
        fileWriter.close();
    }





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
                dataGroups.add(i, scanner.nextLine()); // lit chaque ligne du fichier
//                System.out.println("reading users in file : data group " + i + " : " + dataGroups.get(i));
                i++;

            }




            for (String s : dataGroups) { //parcourt chaque ligne extraite du fichier

                if (s != null) {
                    String[] readData = null;
                    //System.out.println("reading users datas : data : " + s);
                    readData = s.split(String.valueOf(DATA_SEPARATOR)); // sépare chaque donnée utilisateur contenue dans les lignes du fichier
                    //System.out.println("size of readData: " + readData.length);
                    for (String str : readData) { // boucle servant uniquement a afficher l'utilisateur actuellement lu et ajouté dans le HashMap

                        if (str != null) {
                            System.out.println("reading users data : " + str);
                        }
                    }
                    //System.out.println("ajout des données utilisateur");
                    try {
                        if (addedClass == Particulier.class) { //si l'utilisateur est un particulier, on ouvre fichier Accounts pour y trouver l'identifiant et le mot de passe présents dans le fichier Annuaire

                            File accountFile = new File(ACCOUNT_FILE_PATH);
                            scanner = new Scanner(accountFile);
                            String str = "";
                            while(scanner.hasNext()){
                                str = scanner.nextLine();
//                                System.out.println("loadData : particulier : reading in accounts "+str);
                                if (!(str.startsWith("#")) && str.contains(readData[0])){
//                                    System.out.println("identifiant found in file");
                                    break;
                                }
                            }
                            str = fileEncryption.decrypt(str.split(String.valueOf(DATA_SEPARATOR))[1]);
                            System.out.println("password of "+ readData[0]+  " is "+ str);
                            addParticulierToDatabase(new Particulier(readData[1], readData[2], readData[3],readData[4], Particulier.TypeParticulier.valueOf(readData[5]),readData[0],str.toCharArray()));
                        }
                        else if (addedClass == Administrateur.class){
                            if (readData[0].equals("#")) {
                                System.out.println("loadData : admin : adding " + readData[1]);
                                addAdminToDatabase(new Administrateur(readData[1], fileEncryption.decrypt(readData[2]).toCharArray()));
                                System.out.println("decrypted password : " + fileEncryption.decrypt(readData[2]));
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


//        System.out.println("date test 21/11/2005 :" + Particulier.isDateFormatOk("21/11/2005"));
//        System.out.println("date test 10/13/2005 :" + Particulier.isDateFormatOk("10/13/2005"));
//        System.out.println("date test 2005/11/10 :" + Particulier.isDateFormatOk("2005/11/10"));
//        System.out.println("mc cormick is : " + Particulier.isNameFormatOk(Particulier.formatNames("mc cormick")));
//        System.out.println(" jean pierre  is : " + Particulier.isNameFormatOk("jean pierre"));
//        System.out.println("mail sans fin is : "+ Administrateur.isIdentifiantFormatOk("aperikub@hotmail."));

//        System.out.println("mail point is : "+ Administrateur.isIdentifiantFormatOk("aperikubhotmail.fr"));
        //System.out.println("saumon length : " + "saumon".toCharArray().length);

        loadReady = false;
        LoadingDialog loadingDialog = new LoadingDialog();



        new Thread(() -> { // affichage d'une boite de dialogue le temps du chargement des données
            loadingDialog.setLocationRelativeTo(null);
            loadingDialog.setUndecorated(true);
            loadingDialog.pack();
            loadingDialog.setVisible(true);
        }).start();
        System.out.println("loading Admins");
        loadData(new File(ACCOUNT_FILE_PATH), Administrateur.class);
        System.out.println("loading particuliers");
        loadData(new File(ANNUAIRE_FILE_PATH), Particulier.class);

        Thread.sleep(5000); // fichier de l'annuaire trop petit pour avoir un réel temps de chargement

        loadingDialog.dispose();

        listeAdmins.forEach((id, admin)->{
            System.out.println("admin : " + id);
        });
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel()); // application du "skin" Nimbus à l'interface graphique
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        MyWindow myWindow = new MyWindow(); // classe héritant de JFrame, la fenêtre de l'UI, qui contient tous les éléments graphiques affichés


        MenuPrincipal menuPrincipal = new MenuPrincipal(myWindow);
        menuPrincipal.init(); //initialisation du menu principal

        myWindow.setVisible(true);
        myWindow.pack();
        /*ouverture du menu principal*/


        myWindow.setContentPane(menuPrincipal.getMenuPrincipalPanel()); // aplication du menu principal dans la fenêtre
        myWindow.setMinimumSize(new Dimension(600,600));








    }



}
