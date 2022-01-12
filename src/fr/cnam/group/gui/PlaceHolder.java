/*
 * Nom de classe : PlaceHolder
 *
 * Description   : interface permettant d'afficher des indices dans les zones de saisie de texte (JTextField)
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public interface PlaceHolder {

    String DATE_PLACEHOLDER = "JJ/MM/AAAA";
    String PASSWORD_PLACEHOLDER = "a-Z 0-9, 6 à 20 caractères";
    String IDENTIFIANT_PLACEHOLDER = "Adresse mail";
    String NOM_PLACEHOLDER = "Nom";
    String PRENOM_PLACEHOLDER = "Prenom";
    String STREET_PLACEHOLDER = "ex: 1 rue de la paix";
    String POSTAL_CODE_PLACEHOLDER = "ex: 75000";
    String CITY_PLACEHOLDER = "ex: Toulouse";

    default void setPlaceHolder(JTextField textField, String text){
//        System.out.println("setPlaceHolder called");
        if (textField instanceof JPasswordField){
            ((JPasswordField) textField).setEchoChar((char) 0);
        }
        textField.setText(text);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {

            // supprime le placeHolder lorsque l'utilisateur clique sur le TextField
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (textField.getText().isEmpty() || textField.getText().equals(text)) {
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar('*');
                    }
                    textField.setForeground(Color.DARK_GRAY);
                    textField.setText("");
                }
            }

            //ré-installe le placeHolder lorsque l'utilisateur clique ailleurs
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (textField.getText().isEmpty()){
                    if (textField instanceof JPasswordField){
                        ((JPasswordField) textField).setEchoChar((char) 0);
                    }
                    textField.setText(text);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
    //retire le placeHolder
    default void dropPlaceHolder(JTextField textField){
        textField.setForeground(Color.DARK_GRAY);
        textField.setText("");
    }
}
