package fr.cnam.group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ConnectDialog extends JDialog {
    private JPanel connexionPane;
    private JButton buttonOK;
    private JButton buttonDisconnect;
    private JTextField userField;
    private JTextPane connexionTextPane;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel userLabel;



    private JButton validerButton;

    public ConnectDialog(MenuPrincipal menuPrincipal) {
        this.setSize(400, 300);
        this.setLocationRelativeTo(menuPrincipal.getMenuPrincipalPanel());
        setContentPane(connexionPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        if (Main.currentUser == null) {
            buttonOK.setEnabled(true);
            buttonDisconnect.setEnabled(false);
        }
        else{
            buttonOK.setEnabled(false);
            buttonDisconnect.setEnabled(true);
        }

        buttonOK.addActionListener(menuPrincipal);

        buttonDisconnect.addActionListener(menuPrincipal);


        setDefaultCloseOperation(DISPOSE_ON_CLOSE);




        // call onCancel() on ESCAPE
        connexionPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }



    public JPanel getConnexionPane() {
        return connexionPane;
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public JButton getButtonDisconnect() {
        return buttonDisconnect;
    }





    public JTextField getUserField() {
        return userField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }



}
