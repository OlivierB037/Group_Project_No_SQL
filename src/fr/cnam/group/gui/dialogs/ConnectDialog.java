/*
 * Nom de classe : ConnectDialog
 *
 * Description   : boite de dialogue gérant la connection
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group.gui.dialogs;

import fr.cnam.group.DataHandler;
import fr.cnam.group.gui.menus.MenuPrincipal;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ConnectDialog extends JDialog {
    private JPanel connexionPane;
    private JButton connectButton;

    private JTextField userField;
    private JTextPane connexionTextPane;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel userLabel;


    public ConnectDialog(MenuPrincipal menuPrincipal) {
        this.setSize(300, 200);
        this.setLocationRelativeTo(menuPrincipal.getMenuPrincipalPanel());
        setContentPane(connexionPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(connectButton);
        if (DataHandler.currentUser == null) {
            connectButton.setEnabled(true);

        }


        connectButton.addActionListener(menuPrincipal);



        // ferme le Jdialog en cas d'appui sur la touche esc
        connexionPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public JPanel getConnexionPane() {
        return connexionPane;
    }

    public JButton getConnectButton() {
        return connectButton;
    }



    public JTextField getUserField() {
        return userField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }
}
