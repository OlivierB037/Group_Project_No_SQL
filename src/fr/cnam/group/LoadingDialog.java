package fr.cnam.group;

import javax.swing.*;
import java.awt.event.*;

public class LoadingDialog extends JDialog {
    private JPanel contentPane;
    private JLabel chargementDesDonnéesEnLabel;
    private JButton buttonOK;
    private JButton buttonCancel;

    public LoadingDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);


    }




}
