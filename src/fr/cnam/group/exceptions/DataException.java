/*
 * Nom de classe : DataException
 *
 * Description   : exception levée lors d'une erreur lors de l'écriture/lecture des données de l'application
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */
package fr.cnam.group.exceptions;

public class DataException extends Exception{
    public DataException(String msg){
        super(msg);
    }
}
