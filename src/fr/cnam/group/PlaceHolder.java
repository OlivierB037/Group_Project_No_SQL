package fr.cnam.group;

import javax.swing.*;
import java.awt.*;

public interface PlaceHolder {

    default void setPlaceHolder(JTextField textField, String text){
        textField.setText(text);
        textField.setBackground(Color.GRAY);

    }
}
