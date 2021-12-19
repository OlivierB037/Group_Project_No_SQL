package fr.cnam.group;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.cnam.group.Main.DATA_SEPARATOR;
import static fr.cnam.group.Main.MAIN_SEPARATOR;


public class Particulier {

    private String nom;
    private String prenom;

    private String date_naissance;
    private String identifiant;

    public Particulier(String _nom, String _prenom, String date) throws Exception {
       nom = formatNames(_nom);
       prenom = formatNames(_prenom);
       date_naissance = formatNames(date);
       identifiant = nom+'_'+prenom+'_'+date.subSequence(8,10);
    }



    public static String ajouterParticulier(String nom, String prenom, String date_naissance) throws Exception { // ajout d'un particulier dans le système






            Particulier nouveauParticulier = new Particulier(nom, prenom, date_naissance);
            if (Main.annuaire.putIfAbsent(nouveauParticulier.getIdentifiant(), nouveauParticulier) == null) {
                System.out.println("particulier créé");
                if (Main.addParticulierToFile(nouveauParticulier)) {
                    System.out.println("particulier ajouté. identifiant: : " + nouveauParticulier.getIdentifiant());
                    return nouveauParticulier.getIdentifiant();
                } else {
                    Main.annuaire.remove(nouveauParticulier.getIdentifiant());
                    return null;
                }

            } else {
                System.out.println("erreur lors de l'ajout au système");
                return null;
            }

    }



    public static Particulier[] trouverParticulier(String nom, String prenom,String date, boolean andOperator) {
        //String identifiant;

        Particulier[] particuliersTrouves = new Particulier[Main.annuaire.size()];
        AtomicInteger i = new AtomicInteger();
        if (andOperator){
            Main.annuaire.forEach((identifiant, particulier) -> {

                if (particulier.getNom().equalsIgnoreCase(nom) && particulier.getPrenom().equalsIgnoreCase(prenom) && particulier.getDate_naissance().equals(date)) {
                    System.out.println("correspondance " + nom + " " + prenom);
                    particuliersTrouves[i.get()] = particulier;
                    i.getAndIncrement();
                }

            });
        }
        else {
            Main.annuaire.forEach((identifiant, particulier) -> {

                if (particulier.getNom().equalsIgnoreCase(nom) || particulier.getPrenom().equalsIgnoreCase(prenom) || particulier.getDate_naissance().equals(date)) {
                    System.out.println("correspondance " + nom + " " + prenom);
                    particuliersTrouves[i.get()] = particulier;
                    i.getAndIncrement();
                }

            });
        }
        return (i.get() == 0) ? null : particuliersTrouves;

    }

    public static String formatNames(String name) {
        name = name.toLowerCase();
        String[] names = name.split("\\s");

        if (names.length == 1) {
            return name.replaceFirst("[a-zA-Z]", String.valueOf(Character.toUpperCase(name.charAt(0))));
        } else {
            return names[0].replaceFirst("[a-zA-Z]", String.valueOf(Character.toUpperCase(names[0].charAt(0)))) + " " +
                    names[1].replaceFirst("[a-zA-Z]", String.valueOf(Character.toUpperCase(names[1].charAt(0))));
        }
    }

    public static boolean isNameFormatOk(String date)  {
        System.out.println("name checked : " + date);
        if (date.matches("[a-zA-Zéè']{3,20}|[a-zA-Zéè']{1,20}[- ][a-zA-Zéè]{3,20}")){
            return true;
        }
        else return false;
    }

    public static boolean isDateFormatOk(String name)  {
        System.out.println("date checked : " + name);
        if (name.matches("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$")){
            return true;
        }
        else return false;
    }





    public static boolean modifyParticulier(Particulier particulier, Particulier nouveauParticulier) throws Exception {



        Particulier[] particuliers = new Particulier[Main.annuaire.size()];
        System.out.printf("there's %d particuliers in HashMap\n", Main.annuaire.size());
        Main.annuaire.replace(particulier.getIdentifiant(),nouveauParticulier);
        System.out.println("particulier replaced in HashMap");


        particuliers = Main.annuaire.values().toArray(particuliers);
        int refClient = 0;
        for(Particulier p : particuliers) {
            System.out.println("reading from extracted data : " + p.getIdentifiant());
            if (p != null){
                System.out.println("modifying" + p.getIdentifiant() + " to : " + nouveauParticulier.getIdentifiant());
                Main.addParticulierToFile(p);



            } else {

                if(refClient ==  0){
                    System.err.println("particulier read is null");
                    throw new Exception("no match for particulier");
                }
                else{
                    System.out.println("end of particuliers list");

                }

            }
            refClient++;
        }
        return true;
    }

    public static boolean deleteParticulier(Particulier particulier) throws Exception {
        Particulier[] particuliers = new Particulier[Main.annuaire.size()];
        System.out.printf("there's %d particuliers in HashMap\n", Main.annuaire.size());
        if (Main.annuaire.remove(particulier.getIdentifiant()) == null){
            return false;
        }
        System.out.println("particulier removed from HashMap");


        particuliers = Main.annuaire.values().toArray(particuliers);
        int refClient = 0;
        for(Particulier p : particuliers) {
            System.out.println("reading from extracted data : " + p.getIdentifiant());
            if (p != null){

                Main.addParticulierToFile(p);



            } else {

                if(refClient ==  0){
                    System.err.println("particulier read is null");
                    throw new Exception("no match for particulier");
                }
                else{
                    System.out.println("end of particuliers list");

                }

            }
            refClient++;
        }
        return true;
    }




    public String getIdentifiant(){
        return identifiant;
    }
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(String _date_naissance) throws Exception {
        if (isDateFormatOk(_date_naissance)) {
            this.date_naissance = date_naissance;
        }
        else{
            throw new Exception("mauvais format de date: format accepté : MM/DD/YYYY");
        }
    }
}
