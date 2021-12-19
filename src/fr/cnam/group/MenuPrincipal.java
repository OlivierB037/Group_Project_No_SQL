package fr.cnam.group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.PasswordAuthentication;
import java.sql.SQLException;
import java.util.Arrays;

public class MenuPrincipal extends WindowAdapter implements ActionListener {
    private JPanel menuPrincipalPanel;
    private JButton consulterButton;
    private JTextArea title;
    private JButton modifierButton;
    private JButton ajouterButton;
    private JButton requestButton;
    private JButton quitterButton;
    private JButton connecterButton;
    ConnectDialog dialog;
    MyWindow myWindow;
    TopMenu topMenu;

    public MenuPrincipal(MyWindow _myWindow){
        myWindow = _myWindow;

    }
    public void init(){
        topMenu = new TopMenu();
        topMenu.getReturnToMain().setVisible(false);
        myWindow.setJMenuBar(topMenu);

        myWindow.setVisible(true);
        myWindow.pack();

        myWindow.setContentPane(menuPrincipalPanel);
        myWindow.setMinimumSize(new Dimension(600,600));

        myWindow.addWindowStateListener(e -> {
            if (e.getNewState() == WindowEvent.WINDOW_CLOSING){
                System.out.println("window closing");

            }
            else if(e.getNewState() == WindowEvent.WINDOW_CLOSED){
                System.out.println("window closed");
            }
            else{
                System.out.println("window event : " + e.getNewState());
            }
        });


        myWindow.addWindowListener(this);

        connecterButton.addActionListener(e -> {
            openConnect();
        });

        modifierButton.addActionListener(e -> {
            try {
                if (Main.currentUser == null) {
                    JOptionPane.showMessageDialog(myWindow,"only administrateurs can access this section","Accès Refusé",JOptionPane.ERROR_MESSAGE);

                } else {
                    topMenu.getReturnToMain().setVisible(true);


                    MenuModifier menuModifier = new MenuModifier(this);

                    myWindow.setContentPane(menuModifier.getConsultPane());
                }
            }catch (Exception err){
                JOptionPane.showMessageDialog(myWindow,err.getMessage());
            }
        });

        consulterButton.addActionListener(e -> {
            try {

                topMenu.getReturnToMain().setVisible(true);


                MenuConsulter menuConsulter = new MenuConsulter();

                myWindow.setContentPane(menuConsulter.getConsultPane());

            }catch (Exception err){
                JOptionPane.showMessageDialog(myWindow,err.getMessage());
            }
        });






        ajouterButton.addActionListener(e -> {
            try {
                if (Main.currentUser != null ) {

                    topMenu.getReturnToMain().setVisible(true);


                    /*ouverture du menu ajouter un utilisateur*/
                    AjouterUser ajouterUser = new AjouterUser();
                    myWindow.setContentPane(ajouterUser.getPanelAjouterUser());


                }
                else{
                    JOptionPane.showMessageDialog(myWindow,"only administrateurs can access this section","Accès Refusé",JOptionPane.ERROR_MESSAGE);
                }





            }catch (Exception err){
                JOptionPane.showMessageDialog(null,err.toString());
            }
        });//fin du listener menu ajouter




        quitterButton.addActionListener(e -> System.exit(0));
        topMenu.getQuit().addActionListener(e -> System.exit(0));

        topMenu.getReturnToMain().addActionListener(this);
    }

    public void openConnect(){
        dialog = new ConnectDialog(this);
        dialog.pack();
        dialog.setVisible(true);
    }

    public JButton getConnecterButton() {
        return connecterButton;
    }

    public void setConnecterButton(JButton connecterButton) {
        this.connecterButton = connecterButton;
    }

    public JPanel getMenuPrincipalPanel() {
        return menuPrincipalPanel;
    }

    public JButton getConsulterButton() {
        return consulterButton;
    }

    public JTextArea getTitle() {
        return title;
    }

    public JButton getModifierButton() {
        return modifierButton;
    }

    public JButton getAjouterButton() {
        return ajouterButton;
    }

    public JButton getRequestButton() {
        return requestButton;
    }

    public JButton getQuitterButton() {
        return quitterButton;
    }


    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("window closing");




    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("action performed");
        try {

            if(e.getSource() == topMenu.getReturnToMain()|| e.getActionCommand().equals("returnToMainFromModifier")){
                myWindow.setContentPane(menuPrincipalPanel);
                topMenu.getReturnToMain().setVisible(false);
            }
            if (dialog != null) {
                if (e.getSource().equals(dialog.getButtonOK())){
                   String id = dialog.getUserField().getText();
                   if (Main.listeAdmins.get(id) != null){
                       if (Arrays.equals(dialog.getPasswordField().getPassword(), Main.listeAdmins.get(id).getPassword())){
                           Main.currentUser = Main.listeAdmins.get(id);
                           if (Main.currentUser != null){
                               dialog.dispose();
                               JOptionPane.showMessageDialog(myWindow,"connexion réussie","connexion réussie",JOptionPane.INFORMATION_MESSAGE);

                           }
                       }
                       else{
                           throw new Exception("mot de passe incorrect");
                       }
                   }
                   else {
                       throw new Exception("identifiant incorrect");
                   }
               }
               else if (e.getSource() == dialog.getButtonDisconnect()){
                   Main.currentUser = null;
                   JOptionPane.showMessageDialog(myWindow,"déconnexion réussie","déconnexion réussie",JOptionPane.INFORMATION_MESSAGE);
                   dialog.dispose();

               }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(myWindow,ex.getMessage(),"erreur",JOptionPane.ERROR_MESSAGE);
        }
    }
}
