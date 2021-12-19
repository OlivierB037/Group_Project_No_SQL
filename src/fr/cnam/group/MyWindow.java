package fr.cnam.group;

import javax.swing.*;
import java.awt.*;

public class MyWindow extends JFrame {


    public MyWindow() throws Exception{
        super("Annuaire");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(600,600));
        this.setPreferredSize(new Dimension(600,600));

        this.setLocationRelativeTo(null);



    }

}
