package fr.cnam.group.users;

import java.net.PasswordAuthentication;
import java.util.Arrays;

import static fr.cnam.group.DataHandler.*;

public abstract class Account {


    private PasswordAuthentication passwordSet;





    public Account(String identifiant, char[] password) throws Exception {
        if (identifiant.equals(ROOT_ADMIN_ID) || isIdentifiantFormatOk(identifiant) ){
            if (isPasswordFormatOk(password)){
                this.passwordSet = new PasswordAuthentication(identifiant,password);
            }
            else{
                throw new Exception("format du mot de passe incorrect");
            }
        }
        else{
            System.out.println("identifiant isn't rootAdmin");
            throw new Exception("identifiant incorrect ");
        }





    }




    public boolean checkPassword(char[] checkedPassword){
        return Arrays.equals(passwordSet.getPassword(), checkedPassword);
    }


    public static boolean isIdentifiantFormatOk(String id)  {
        System.out.println("name checked : " + id);
        if (id.matches("[a-z0-9*\\-\\._]{1,30}@[a-z]{1,30}\\.[a-z]{2,20}")){
            return true;
        }
        else return false;
    }


    public static boolean isPasswordFormatOk(char[] password){
        if (password.length < 6 || password.length > 20)
        for(char c : password) {
            if (!(Character.toString(c).matches("[0-9a-zA-Z]") ) ) {
                System.out.println("format incorrect");
                return false;
            }
        }

        System.out.println("mot de passe valide");
        return true;

    }


    public abstract boolean modify(Account newAccount) throws Exception;

    public abstract boolean ajouter() throws Exception;


    public abstract boolean remove() throws Exception ;

    public String getIdentifiant() {
        return passwordSet.getUserName();
    }
    public char[] getPassword(){ return passwordSet.getPassword();}


}
