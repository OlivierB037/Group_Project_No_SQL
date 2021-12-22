package fr.cnam.group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public interface PlaceHolder {

    String DATE_PLACEHOLDER = "JJ/MM/AAAA";
    String PASSWORD_PLACEHOLDER = "a-Z 0-9, 6 à 20 caractères";
    String IDENTIFIANT_PLACEHOLDER = "Adresse mail";
    String NOM_PLACEHOLDER = "Nom";
    String PRENOM_PLACEHOLDER = "Prenom";

    default void setPlaceHolder(JTextField textField, String text){
        if (textField instanceof JPasswordField){
            ((JPasswordField) textField).setEchoChar((char) 0);
        }
        textField.setText(text);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
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
//        textField.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                if (textField instanceof JPasswordField){
//                    ((JPasswordField) textField).setEchoChar('*');
//                }
//                textField.setForeground(Color.DARK_GRAY);
//                textField.setText("");
//            }
//        });
    }

    default void dropPlaceHolder(JTextField textField){
        textField.setText("");
    }
}
