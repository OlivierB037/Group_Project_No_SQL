package fr.cnam.group.users;

import fr.cnam.group.DataHandler;
import fr.cnam.group.files.Annuaire;
import fr.cnam.group.files.Comptes;
import fr.cnam.group.files.FilesHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.cnam.group.DataHandler.*;


public class Particulier extends Account {

    private String nom;
    private String prenom;

    private String date_naissance;

    private String date_modification;
    public enum TypeParticulier {Enseignant, Auditeur, Direction}
    private TypeParticulier typeParticulier;

    public Particulier(String _nom, String _prenom, String date, String _date_modification,TypeParticulier _typeParticulier, String identifiant, char[] password) throws Exception {
        super(identifiant, password);
        typeParticulier = _typeParticulier;
        nom = formatNames(_nom);
        prenom = formatNames(_prenom);
        date_naissance = formatNames(date);
//       identifiant = nom+'_'+prenom+'_'+date.subSequence(8,10);
        date_modification = _date_modification;


    }





    public static String generateDateModification(){ //génère la date d'aujourd'hui
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }



    public static Particulier[] trouverParticulier(String nom, String prenom,String date, boolean andOperator) {
        //String identifiant;

        Particulier[] particuliersTrouves = new Particulier[DataHandler.annuaire.size()];
        AtomicInteger i = new AtomicInteger();
        if (andOperator){
            DataHandler.annuaire.forEach((identifiant, particulier) -> {

                if (particulier.getNom().equalsIgnoreCase(nom) && particulier.getPrenom().equalsIgnoreCase(prenom) && particulier.getDate_naissance().equals(date)) {
                    System.out.println("correspondance " + nom + " " + prenom);
                    particuliersTrouves[i.get()] = particulier;
                    i.getAndIncrement();
                }

            });
        }
        else {
            DataHandler.annuaire.forEach((identifiant, particulier) -> {

                if (particulier.getNom().equalsIgnoreCase(nom) || particulier.getPrenom().equalsIgnoreCase(prenom) || particulier.getDate_naissance().equals(date)) {
                    System.out.println("correspondance " + nom + " " + prenom);
                    particuliersTrouves[i.get()] = particulier;
                    i.getAndIncrement();
                }

            });
        }
        return (i.get() == 0) ? null : particuliersTrouves;

    }
    public static Particulier[] trouverParticulier(TypeParticulier searchedType) {
        //String identifiant;

        Particulier[] particuliersTrouves = new Particulier[DataHandler.annuaire.size()];
        AtomicInteger i = new AtomicInteger();

        DataHandler.annuaire.forEach((identifiant, particulier) -> {

            if (particulier.getTypeParticulier().equals(searchedType)) {
                System.out.println("correspondance " + searchedType.toString());
                particuliersTrouves[i.get()] = particulier;
                i.getAndIncrement();
            }

        });

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
        if (name.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$")){
            return true;
        }
        else return false;
    }





//    public static boolean modifyParticulier(Particulier particulier, Particulier nouveauParticulier) throws Exception {
//
//
//
//        Particulier[] particuliers = new Particulier[DataHandler.annuaire.size()];
//        System.out.printf("there's %d particuliers in HashMap\n", DataHandler.annuaire.size());
//        DataHandler.annuaire.replace(particulier.getIdentifiant(),nouveauParticulier);
//        System.out.println("particulier replaced in HashMap");
//
//
//        particuliers = DataHandler.annuaire.values().toArray(particuliers);
//        int refClient = 0;
//        for(Particulier p : particuliers) {
//            System.out.println("reading from extracted data : " + p.getIdentifiant());
//            if (p != null){
//                System.out.println("modifying" + p.getIdentifiant() + " to : " + nouveauParticulier.getIdentifiant());
//                DataHandler.addParticulierToFile(p);
//
//
//
//            } else {
//
//                if(refClient ==  0){
//                    System.err.println("particulier read is null");
//                    throw new Exception("no match for particulier");
//                }
//                else{
//                    System.out.println("end of particuliers list");
//
//                }
//
//            }
//            refClient++;
//        }
//        return true;
//    }






    @Override
    public boolean remove() throws Exception {
        Particulier[] particuliers = new Particulier[DataHandler.annuaire.size()];
        System.out.printf("there's %d particuliers in HashMap\n", DataHandler.annuaire.size());
        if (DataHandler.annuaire.remove(getIdentifiant()) == null){
            return false;
        }
        System.out.println("particulier removed from HashMap");


        particuliers = DataHandler.annuaire.values().toArray(particuliers);
        int refClient = 0;
        FilesHandler.clearFile(new Annuaire());
        FilesHandler.clearFile(new Comptes());
        for(Particulier p : particuliers) {
            System.out.println("reading from extracted data : " + p.getIdentifiant());
            if (p != null){

                FilesHandler.addToFile(p);



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

    @Override
    public boolean modify(Account newAccount) throws Exception {
        Particulier[] particuliers = new Particulier[DataHandler.annuaire.size()];
        System.out.printf("there's %d particuliers in HashMap\n", DataHandler.annuaire.size());

        if (getIdentifiant().equals(newAccount.getIdentifiant())){
            annuaire.replace(getIdentifiant(), (Particulier) newAccount);
        }else {
            if (annuaire.putIfAbsent(newAccount.getIdentifiant(), (Particulier) newAccount) == null) {
                annuaire.remove(getIdentifiant());

            } else {
                throw new Exception("erreur lors de l'ajout au système");
            }
        }

        DataHandler.annuaire.replace(getIdentifiant(), (Particulier) newAccount);
        System.out.println("particulier replaced in HashMap");


        particuliers = DataHandler.annuaire.values().toArray(particuliers);
        int refClient = 0;
        FilesHandler.clearFile(new Annuaire());
        FilesHandler.clearFile(new Comptes());
        for(Particulier p : particuliers) {
            System.out.println("reading from extracted data : " + p.getIdentifiant());
            if (p != null){
                System.out.println("modifying" + p.getIdentifiant() + " to : " + newAccount.getIdentifiant());
                FilesHandler.addToFile(p);



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

    @Override
    public boolean ajouter() throws Exception {

        if (DataHandler.annuaire.putIfAbsent(getIdentifiant(), this) == null) {
            System.out.println("particulier créé");
            try  {
                FilesHandler.addToFile(this);
                System.out.println("particulier ajouté. identifiant: : " + getIdentifiant());
                return true;
            } catch (Exception e){

                DataHandler.annuaire.remove(getIdentifiant());
                return false;
            }

        } else {
            System.out.println("erreur lors de l'ajout au système");
            return false;
        }
    }

    public TypeParticulier getTypeParticulier() {
        return typeParticulier;
    }

    public String getDate_modification() {
        return date_modification;
    }

    @Override
    public String getIdentifiant() {
        return super.getIdentifiant();
    }

    @Override
    public char[] getPassword() {
        return super.getPassword();
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
