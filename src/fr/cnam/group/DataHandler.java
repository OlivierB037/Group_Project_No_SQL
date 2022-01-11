/*
 * Nom de classe : DataHandler
 *
 * Description   : gère les données utilisateurs dans l'application
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group;

import fr.cnam.group.files.Annuaire;
import fr.cnam.group.files.Comptes;
import fr.cnam.group.files.FileEncryption;
import fr.cnam.group.files.FilesHandler;
import fr.cnam.group.gui.dialogs.LoadingDialog;
import fr.cnam.group.gui.menus.MenuPrincipal;
import fr.cnam.group.gui.MyWindow;
import fr.cnam.group.users.Account;
import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;




public class DataHandler {




    public static HashMap<String, Particulier> annuaire = new HashMap<>();
    public static HashMap<String, Administrateur> listeAdmins = new HashMap<>();

    //constantes contenant les infos d'écriture et d'ouverture des fichiers
    public static final char MAIN_SEPARATOR = '\n';
    public static final char DATA_SEPARATOR = ';';
    public static final String ROOT_ADMIN_ID = "rootAdmin";




    public static Account currentUser = null; // utilisateur connecté






    public static boolean isIdentifiantavailable(String identifiant){
        AtomicBoolean available = new AtomicBoolean(true);
        listeAdmins.forEach((id, admin) ->{
            System.out.printf("isidentifiantAvailable() : testing %s vs %s\n",id,identifiant);
            if(identifiant.equals(id)){
                System.out.println("l'identifiant " + identifiant + " est déja utilisé (admin)");
                available.set(false);
            }
        });
        annuaire.forEach((id, admin) ->{
            System.out.printf("isidentifiantAvailable() : testing %s vs %s\n",id,identifiant);
            if(identifiant.equals(id)){
                System.out.println("l'identifiant " + identifiant + " est déja utilisé (particulier");
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











    public static void loadData(File file, Class<? extends Account> addedClass ) { //lire les fichiers de sauvegarde et générer les données

        ArrayList<String> dataGroups = new ArrayList<>();
        Scanner scanner = null;
        try {
            FileEncryption fileEncryption = new FileEncryption(FilesHandler.encryptionKey,FilesHandler.encryptionSalt,FilesHandler.encryptionIterations,FilesHandler.encryptionKeyLength);// classe générant le cryptage
            if ((file.exists() ) ){ // vérification de la présence du fichier
                if (file instanceof Comptes) {
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
                                write(Comptes.ADMIN_SYMBOL+DATA_SEPARATOR+"rootAdmin"+DATA_SEPARATOR+password+MAIN_SEPARATOR);
                                flush();
                                close();
                            }
                        };
                    }
                }

            }else{
                System.out.println("rootAdmin verification : file " + file.getName() + " does not exist\n creating file..." );
                if (file.createNewFile()){
                    if(file instanceof Comptes){
                        System.out.println("rootAdmin verification : creating root administrator");

                        String password = fileEncryption.encrypt("rootPassword");

                        new FileWriter(file) {
                            {
                                write(Comptes.ADMIN_SYMBOL+DATA_SEPARATOR+"rootAdmin"+DATA_SEPARATOR+password+MAIN_SEPARATOR);
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

                            File comptes = new Comptes();
                            scanner = new Scanner(comptes);
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
                            addParticulierToDatabase(new Particulier(readData[1], readData[2], readData[3],readData[4],readData[5], Particulier.TypeParticulier.valueOf(readData[6]),readData[0],str.toCharArray()));
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

        //System.out.println("test 3 parts - 1 rue de la paix,09240,la-bastide de Serou : " + Particulier.isAdresseFormatOk("1 rue de la paix", "09240", "la bastide de Serou"));
        //System.out.println("test 1 rue de la paix,09240,la bastide de Serou : " + Particulier.isAdresseFormatOk("1 rue de la paix, 09240, la bastide de Serou"));

        LoadingDialog loadingDialog = new LoadingDialog();

        new Thread(() -> { // affichage d'une boite de dialogue le temps du chargement des données
            loadingDialog.pack();
            loadingDialog.setVisible(true);
        }).start();
        System.out.println("loading Admins");
        loadData(new Comptes(), Administrateur.class);
        System.out.println("loading particuliers");
        loadData(new Annuaire(), Particulier.class);

        Thread.sleep(2000); // fichier de l'annuaire trop petit pour avoir un réel temps de chargement

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
