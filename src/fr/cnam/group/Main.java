package fr.cnam.group;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main{




    public static HashMap<String, Particulier> annuaire = new HashMap<>();
    public static HashMap<String, Administrateur> listeAdmins = new HashMap<>();

    public static final char MAIN_SEPARATOR = '\n';
    public static final char DATA_SEPARATOR = ';';
    public static Administrateur currentUser = null;
    public static Particulier currentParticulier;
    public static final String ROOT_ADMIN_ID = "rootAdmin";
    public static final String ADMIN_FILE_PATH = "Admins.txt";
    public static final String ANNUAIRE_FILE_PATH = "Annuaire.txt";
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




    public static boolean addAdminToDatabase(Administrateur admin) throws Exception {
        if(listeAdmins.putIfAbsent(admin.getIdentifiant(),admin) == null){
            System.out.println("admin ajouté au système");
            return true;
        }
        else{
            throw new Exception("erreur lors de l'ajout au système");
        }
    }

    public static boolean addParticulierToDatabase(Particulier particulier) throws Exception {

        if (annuaire.putIfAbsent(particulier.getIdentifiant(),particulier) == null) {
            System.out.println("particulier ajouté au système");
            return true;

        } else {
            throw new Exception("erreur lors de l'ajout dans l'annuaire");
        }
    }


    public static boolean addParticulierToFile(Particulier particulier) {
        File saveFile = new File(ANNUAIRE_FILE_PATH);

        try {

            FileWriter fileWriter = new FileWriter(saveFile, true);
            fileWriter.write(particulier.getIdentifiant() + DATA_SEPARATOR + particulier.getNom() + DATA_SEPARATOR + particulier.getPrenom() + DATA_SEPARATOR + particulier.getDate_naissance() + MAIN_SEPARATOR);

            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }




    static void addAdminToFile(Administrateur admin) throws Exception {
        File file = new File(ADMIN_FILE_PATH);

        FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
        String encryptedPassword = fileEncryption.encrypt(String.valueOf(admin.getPassword()));
        FileWriter fileWriter = new FileWriter(file,true);
        fileWriter.write(admin.getIdentifiant()+DATA_SEPARATOR);
        fileWriter.write(encryptedPassword);
        fileWriter.write(MAIN_SEPARATOR);
        fileWriter.flush();
        fileWriter.close();


    }




    public static void loadData(File file, Class<?> addedClass ) { //lire les fichiers de sauvegarde et générer les données

        ArrayList<String> dataGroups = new ArrayList<>();
        Scanner scanner = null;
        try {
            FileEncryption fileEncryption = new FileEncryption(encryptionKey,encryptionSalt,encryptionIterations,encryptionKeyLength);
            if ((file.exists() && file.getName().equals(ADMIN_FILE_PATH)) ){
                System.out.println("admin file is present");
                if(new BufferedReader(new FileReader(file)).lines().anyMatch(s ->  s.contains(ROOT_ADMIN_ID+DATA_SEPARATOR) )){
                    System.out.println("root admin found in file");
                }
//                new BufferedReader(new FileReader(file)).lines().forEach(s->{
//                    System.out.println("reading : "+ s);
//                    if (s.contains("rootAdmin"+DATA_SEPARATOR)){
//                        System.out.println("match found");
//                    }
//                });
                else{
                    System.out.println("creating root administrator");

                    String password = fileEncryption.encrypt("rootPassword");
                    System.out.println("encrypted password : " + password);
                    new FileWriter(file) {
                        {
                            write("rootAdmin"+DATA_SEPARATOR+password+MAIN_SEPARATOR);
                            flush();
                            close();
                        }
                    };
                }

            }else{
                System.out.println("file " + file.getName() + " does not exist\n creating file..." );
                if (file.createNewFile()){
                    if(file.getName().equals(ADMIN_FILE_PATH)){
                        System.out.println("creating root administrator");

                        String password = fileEncryption.encrypt("rootPassword");

                        new FileWriter(file) {
                            {
                                write("rootAdmin"+DATA_SEPARATOR+password+MAIN_SEPARATOR);
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
                System.out.println("data group " + i + " : " + dataGroups.get(i));
                i++;

            }





            for (String s : dataGroups) {
                String[] readData = null;
                if (s != null) {
                    System.out.println("data : " + s);
                    readData = s.split(String.valueOf(DATA_SEPARATOR));
                    System.out.println("size of readData: " + readData.length);
                    for (String str : readData) {

                        if (str != null) {
                            System.out.println("reading data : " + str);
                        }
                    }
                    System.out.println("ajout des données");
                    try {
                        if (addedClass == Particulier.class) {

                            addParticulierToDatabase(new Particulier(readData[1], readData[2], readData[3]));
                        }
                        else if (addedClass == Administrateur.class){

                            addAdminToDatabase(new Administrateur(readData[0], fileEncryption.decrypt(readData[1]).toCharArray()));
                            System.out.println("decrypted password : "+ fileEncryption.decrypt(readData[1]) );
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
//
//        System.out.println("mc cormick is : " + Particulier.isNameFormatOk(Particulier.formatNames("mc cormick")));
//        System.out.println(" jean pierre  is : " + Particulier.isNameFormatOk("jean pierre"));
//        System.out.println("saumon is : "+ Administrateur.isPasswordFormatOk("saumon".toCharArray()));
//        System.out.println("saumon length : " + "saumon".toCharArray().length);


        loadData(new File(ADMIN_FILE_PATH),Administrateur.class);
        loadData(new File(ANNUAIRE_FILE_PATH), Particulier.class);
        listeAdmins.forEach((id,admin)->{
            System.out.println("admin : " + id);
        });
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        try {
            MyWindow myWindow = new MyWindow();


            MenuPrincipal menuPrincipal = new MenuPrincipal(myWindow);
            menuPrincipal.init();

            myWindow.setVisible(true);
            myWindow.pack();
            /*ouverture du menu principal*/


            myWindow.setContentPane(menuPrincipal.getMenuPrincipalPanel());
            myWindow.setMinimumSize(new Dimension(600,600));
        } catch (ClassCastException e) {
            System.out.println("class cast Exception caught --------------------");
        }


    }



}
