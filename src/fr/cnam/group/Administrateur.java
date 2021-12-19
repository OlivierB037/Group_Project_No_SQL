package fr.cnam.group;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.Scanner;

import static fr.cnam.group.Main.*;

public class Administrateur {


    private PasswordAuthentication passwordSet;





    public Administrateur( String identifiant, char[] password) throws Exception {
        if (isIdentifiantFormatOk(identifiant) ){
            if (isPasswordFormatOk(password)){
                this.passwordSet = new PasswordAuthentication(identifiant,password);
            }
            else{
                throw new Exception("format du mot de passe incorrect");
            }
        }
        else{
            throw new Exception("identifiant incorrect ");
        }





    }

    public static boolean modifyAdministrateur(Administrateur administrateur, Administrateur newAdmin) throws Exception {



        Administrateur[] admins = new Administrateur[listeAdmins.size()];
        System.out.printf("there's %d admins in HashMap\n", Main.annuaire.size());
        if (administrateur.getIdentifiant().equals(newAdmin.getIdentifiant())){
            listeAdmins.replace(administrateur.getIdentifiant(),newAdmin);
        }else {
            if (listeAdmins.putIfAbsent(newAdmin.getIdentifiant(), newAdmin) == null) {
                listeAdmins.remove(administrateur.getIdentifiant());

            } else {
                throw new Exception("erreur lors de l'ajout au système");
            }
        }
        System.out.println("administrateur replaced in HashMap");
//        Scanner scanner = new Scanner(adminFile);
//        scanner.useDelimiter(String.valueOf(MAIN_SEPARATOR));
//        int i = 0;
//        while(scanner.hasNext()){
//            admins[i] = scanner.next();
//            System.out.println("reading from file : "+ admins[i]);
//            i++;
//        }
//        scanner.close();

        admins = listeAdmins.values().toArray(admins);



        int refClient = 0;
        for(Administrateur a : admins) {
            System.out.println("reading from extracted data : " + a.getIdentifiant());
            if (a != null){
                System.out.println("checking " + a.getIdentifiant());
                Main.addAdminToFile(a);

            } else {

                if(refClient ==  0){
                    System.err.println("admin read is null");
                    throw new Exception("no match for Administrateur");
                }
                else{
                    System.out.println("end of admins list");

                }

            }
            refClient++;
        }

        return true;
    }
    public static boolean removeAdministrateur(Administrateur administrateur) throws Exception {



        Administrateur[] admins = new Administrateur[listeAdmins.size()];
        System.out.printf("there's %d admins in HashMap\n", Main.annuaire.size());

        listeAdmins.remove(administrateur.getIdentifiant());
        System.out.println("administrateur removed from HashMap");

        admins = listeAdmins.values().toArray(admins);


        int refClient = 0;
        for(Administrateur a : admins) {
            System.out.println("reading from extracted data : " + a.getIdentifiant());
            if (a != null){
                System.out.println("checking " + a.getIdentifiant());
                Main.addAdminToFile(a);

            } else {

                if(refClient ==  0){
                    System.err.println("admin read is null");
                    throw new Exception("no match for Administrateur");
                }
                else{
                    System.out.println("end of admins list");

                }

            }
            refClient++;
        }

        return true;
    }

    public boolean checkPassword(char[] checkedPassword){
        return Arrays.equals(passwordSet.getPassword(), checkedPassword);
    }


    public static boolean isIdentifiantFormatOk(String id)  {
        System.out.println("name checked : " + id);
        if (id.matches("[a-zA-Zéè'_0-9-]{4,30}")){
            return true;
        }
        else return false;
    }

    public static boolean isPasswordFormatOk(char[] password){
        for(char c : password) {
            if (!(Character.toString(c).matches("[0-9a-zA-Z]") ) ) {
                System.out.println("format incorrect");
                return false;
            }
        }

        System.out.println("mot de passe valide");
        return true;

    }


    public static boolean ajouterAdministrateur(Administrateur admin) throws Exception {


            try {
                if (listeAdmins.putIfAbsent(admin.getIdentifiant(), admin) == null){
                    System.out.println("admin créé");
                    addAdminToFile(admin);
                    return true;
                }
                else{
                    System.out.println("error while putting to HashMap");
                    throw new Exception("erreur lors de l'ajout au système");

                }

            }catch (IOException e) {
                System.err.println(e.getMessage());
                System.out.println("removing from HashMap");
                listeAdmins.remove(admin.getIdentifiant());
                throw new Exception("erreur lors de l'inscription dans le fichier");

            }


    }
















    public boolean remove() throws Exception {
        return true;
    }

    public String getIdentifiant() {
        return passwordSet.getUserName();
    }
    public char[] getPassword(){ return passwordSet.getPassword();}


}
