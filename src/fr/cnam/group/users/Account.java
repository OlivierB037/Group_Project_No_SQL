package fr.cnam.group.users;

import fr.cnam.group.exceptions.FileException;
import fr.cnam.group.exceptions.UserDataInputException;

import java.net.PasswordAuthentication;
import java.util.Arrays;

public abstract class Account {


    private PasswordAuthentication passwordSet;





    public Account(String identifiant, char[] password) throws Exception {


            this.passwordSet = new PasswordAuthentication(identifiant,password);


    }




    public boolean checkPasswordEquality(char[] checkedPassword){
        return Arrays.equals(passwordSet.getPassword(), checkedPassword);
    }


    public static void checkIdentifiantFormat(String id) throws UserDataInputException {
        System.out.println("name checked : " + id);
        if (id.matches("[a-z0-9*\\-\\._]{1,30}@[a-z]{1,30}\\.[a-z]{2,20}")){
            System.out.println("format de l'identifiant ok");
        }
        else throw new UserDataInputException("format de l'identifiant incorrect\n");
    }


    public static void checkPasswordFormat(char[] password) throws UserDataInputException {
        if (password.length < 6 || password.length > 20)
        for(char c : password) {
            if (!(Character.toString(c).matches("[0-9a-zA-Z]") ) ) {
                System.out.println("format incorrect");
                throw new UserDataInputException("format du mot de passe incorrect\nformat requis : chiffres et lettres, 6 à 20 caractères");
            }
        }

        System.out.println("format du mot de passe valide");


    }


    public abstract boolean modify(Account newAccount) throws Exception;

    public abstract boolean ajouter() throws FileException;


    public abstract boolean remove() throws Exception ;

    public String getIdentifiant() {
        return passwordSet.getUserName();
    }
    public char[] getPassword(){ return passwordSet.getPassword();}


}
