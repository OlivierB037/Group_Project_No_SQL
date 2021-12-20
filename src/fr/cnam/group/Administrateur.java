package fr.cnam.group;

import java.io.IOException;

import static fr.cnam.group.DataHandler.*;

public class Administrateur extends Account{
    public Administrateur(String identifiant, char[] password) throws Exception {
        super(identifiant, password);
    }

    @Override
    public boolean checkPassword(char[] checkedPassword) {
        return super.checkPassword(checkedPassword);
    }

    @Override
    public boolean modify(Account newAccount) throws Exception {



        Administrateur[] accounts = new Administrateur[listeComptes.size()+ annuaire.size()];
        System.out.printf("there's %d accounts in HashMap\n", listeComptes.size());
        if (getIdentifiant().equals(newAccount.getIdentifiant())){
            listeComptes.replace(getIdentifiant(), (Administrateur) newAccount);
        }else {
            if (listeComptes.putIfAbsent(newAccount.getIdentifiant(), (Administrateur) newAccount) == null) {
                listeComptes.remove(getIdentifiant());

            } else {
                throw new Exception("erreur lors de l'ajout au système");
            }
        }
        System.out.println("Account replaced in HashMap");


        accounts = listeComptes.values().toArray(accounts);



        int refClient = 0;
        for(Account a : accounts) {
            System.out.println("reading from extracted data : " + a.getIdentifiant());
            if (a != null){
                System.out.println("checking " + a.getIdentifiant());
                DataHandler.addAdminToFile(a);

            } else {

                if(refClient ==  0){
                    System.err.println("admin read is null");
                    throw new Exception("no match for Account");
                }
                else{
                    System.out.println("end of accounts list");

                }

            }
            refClient++;
        }

        return true;
    }

    @Override
    public boolean remove() throws Exception {
        Administrateur[] accounts = new Administrateur[listeComptes.size()+DataHandler.annuaire.size()];
        System.out.printf("there's %d accounts in HashMap\n", DataHandler.annuaire.size());

        listeComptes.remove(getIdentifiant());
        System.out.println("administrateur removed from HashMap");

        accounts = listeComptes.values().toArray(accounts);


        int refClient = 0;
        for(Account a : accounts) {
            System.out.println("reading from extracted data : " + a.getIdentifiant());
            if (a != null){
                System.out.println("checking " + a.getIdentifiant());
                DataHandler.addAdminToFile(a);

            } else {

                if(refClient ==  0){
                    System.err.println("admin read is null");
                    throw new Exception("no match for Administrateur");
                }
                else{
                    System.out.println("end of accounts list");

                }

            }
            refClient++;
        }

        return true;
    }

    @Override
    public boolean ajouter() throws Exception {
        try {
            if (listeComptes.putIfAbsent(getIdentifiant(), this) == null){
                System.out.println("admin créé");
                addAdminToFile(this);
                return true;
            }
            else{
                System.out.println("error while putting to HashMap");
                throw new Exception("erreur lors de l'ajout au système");

            }

        }catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("removing from HashMap");
            listeComptes.remove(getIdentifiant());
            throw new Exception("erreur lors de l'inscription dans le fichier");

        }
    }

    @Override
    public String getIdentifiant() {
        return super.getIdentifiant();
    }

    @Override
    public char[] getPassword() {
        return super.getPassword();
    }
}
