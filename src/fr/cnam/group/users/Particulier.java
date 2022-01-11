package fr.cnam.group.users;

import fr.cnam.group.DataHandler;
import fr.cnam.group.exceptions.FileException;
import fr.cnam.group.exceptions.UserDataInputException;
import fr.cnam.group.files.Annuaire;
import fr.cnam.group.files.Comptes;
import fr.cnam.group.files.FilesHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.cnam.group.DataHandler.*;


public class Particulier extends Account {

    private String nom;
    private String prenom;

    private String date_naissance;
    private String adresse;
    private String date_modification;
    public enum TypeParticulier {Enseignant, Auditeur, Direction}
    private TypeParticulier typeParticulier;

    public Particulier(String _nom, String _prenom, String date,String _adresse, String _date_modification,TypeParticulier _typeParticulier, String identifiant, char[] password) throws Exception {
        super(identifiant, password);
        typeParticulier = _typeParticulier;
        adresse = _adresse;
        nom = formatNames(_nom);
        prenom = formatNames(_prenom);
        date_naissance = date;
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

    public static String formatAdresse(String street,String postalCode,String city) {
        return street + ", "+ postalCode + " " + city;
    }

    public static void checkAdresseFormat(String street,String postalCode, String city) throws UserDataInputException {
        System.out.println("adresse checked : " + street);
        String regex1 = "[0-9]{1,3}\\s(\\s?[a-zA-Zéè'-çà]{1,20}){1,8},[0-9]{5},(\\s?[a-zA-Zéè']{1,20}){1,5}";
        if (street.matches("[0-9]{1,3}([a-zA-Zéè'-çà\\s ]{1,20}){1,8}") ){
            if (postalCode.matches("[0-9]{5}")){
                if (city.matches("([a-zA-Zéè'-çà\\s ]{1,20}){1,8}")){
                    System.out.println("adresse valide");
                }
                else{
                    throw new UserDataInputException("ville incorrecte");
                }
            }
            else{
                throw new UserDataInputException("code postal incorrect");
            }
        }
        else{
            throw new UserDataInputException("rue saisie incorrecte\nformat requis: n° rue");
        }
    }



    public static void checkNameFormat(String name) throws UserDataInputException {
        System.out.println("name checked : " + name);
        if (name.matches("[a-zA-Zéè']{3,20}|[a-zA-Zéè']{1,20}[- ][a-zA-Zéè]{3,20}")){
            System.out.println("nom valide");
        }
        else throw new UserDataInputException("nom ou prénom incorrect");
    }

    public static void checkDateFormat(String date) throws UserDataInputException {
        System.out.println("date checked : " + date);
        if (date.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$")){
            System.out.println("date valide");
        }
        else throw new UserDataInputException("format de la date incorrect\nformat requis: JJ/MM/AAAA");
    }


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

            if (p != null){
                System.out.println("reading from extracted data : " + p.getIdentifiant());

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
    public boolean ajouter() throws FileException {

        if (DataHandler.annuaire.putIfAbsent(getIdentifiant(), this) == null) {
            System.out.println("particulier créé");
            try  {
                FilesHandler.addToFile(this);
                System.out.println("particulier ajouté. identifiant: : " + getIdentifiant());
                return true;
            } catch (Exception e){

                DataHandler.annuaire.remove(getIdentifiant());
                throw new FileException("erreur lors de l'ajout au fichier");
            }

        } else {
            throw new FileException("erreur lors de l'ajout au système");

        }
    }



    public String getAdresse() {
        return adresse;
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

        this.date_naissance = date_naissance;

    }
}
