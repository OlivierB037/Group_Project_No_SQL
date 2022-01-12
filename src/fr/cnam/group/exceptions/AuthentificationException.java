package fr.cnam.group.exceptions;

import java.security.GeneralSecurityException;

public class AuthentificationException extends GeneralSecurityException {
    public AuthentificationException(String msg){
        super(msg);
    }
}
