/*
 * Nom de classe : MyWindow
 *
 * Description   : fenêtre contenant les différents menus.
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

public class MyWindow extends JFrame {

    public MyWindow() throws Exception{
        super("Annuaire");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(500,500));
        this.setPreferredSize(new Dimension(800,500));
        this.setLocationRelativeTo(null);
    }
}
