/*
 * Nom de classe : MenuPrincipal
 *
 * Description   : pilote le menu principal
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group.gui.menus;

import fr.cnam.group.DataHandler;
import fr.cnam.group.exceptions.AuthentificationException;
import fr.cnam.group.gui.dialogs.ConnectDialog;
import fr.cnam.group.gui.MyWindow;
import fr.cnam.group.gui.MenuBar;
import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class MenuPrincipal implements ActionListener { // implémente ActionListener pour être à l'écoute de certains évènements
    private JPanel menuPrincipalPanel;
    private JButton consulterButton;
    private JTextArea title;
    private JButton modifierButton;
    private JButton ajouterButton;
    private JButton quitterButton;
    private JButton connecterButton;
    private JLabel statutTextField;

    ConnectDialog dialog;
    MyWindow myWindow;
    MenuBar menuBar;

    public MenuPrincipal(MyWindow myWindow){
        this.myWindow = myWindow;

    }
    /*
    * initialisation du menu principal*/
    public void init(){
        menuBar = new MenuBar();
        menuBar.getReturnToMain().setVisible(false);
        myWindow.setJMenuBar(menuBar);
        myWindow.setVisible(true);
        myWindow.pack();
        myWindow.setContentPane(menuPrincipalPanel);
        myWindow.setMinimumSize(new Dimension(600,600));
        composeMenuPrincipal();
        connecterButton.addActionListener(e ->{
            if (DataHandler.currentUser == null) {
                openConnect();
            }else{
                DataHandler.currentUser = null;
                composeMenuPrincipal();
            }
        }); // execution de openConnect() si clic sur le bouton connecter

        modifierButton.addActionListener(e -> {
            try {
                if (DataHandler.currentUser == null) {
                    JOptionPane.showMessageDialog(myWindow,"only administrateurs can access this section","Accès Refusé",JOptionPane.ERROR_MESSAGE);
                } else {
                    menuBar.getReturnToMain().setVisible(true);
                    MenuModifier menuModifier = new MenuModifier(this);
                    myWindow.setContentPane(menuModifier.getModifierPane());
                }
            }catch (Exception err){
                JOptionPane.showMessageDialog(myWindow,err.getMessage());
            }
        });

        consulterButton.addActionListener(e -> {
            try {
                menuBar.getReturnToMain().setVisible(true); // on rend visible l'option de retour au menu principal dans le menu "fichier"
                MenuConsulter menuConsulter = new MenuConsulter();
                myWindow.setContentPane(menuConsulter.getConsultPane()); // setContent Pane applique un contenu à la fenetre
            }catch (Exception err){
                JOptionPane.showMessageDialog(myWindow,err.getMessage());
            }
        });


        ajouterButton.addActionListener(e -> {
            try {
                if (DataHandler.currentUser != null ) {
                    menuBar.getReturnToMain().setVisible(true);
                    //ouverture du menu ajouter un utilisateur
                    MenuAjouter menuAjouter = new MenuAjouter();
                    myWindow.setContentPane(menuAjouter.getMenuAjouterPanel());
                }else{
                    JOptionPane.showMessageDialog(myWindow,"only administrateurs can access this section","Accès Refusé",JOptionPane.ERROR_MESSAGE);
                }
            }catch (Exception err){
                JOptionPane.showMessageDialog(null,err.toString());
            }
        });//fin du listener menu ajouter

        quitterButton.addActionListener(e -> System.exit(0));
        menuBar.getQuit().addActionListener(e -> System.exit(0));
        menuBar.getReturnToMain().addActionListener(this);
    }

    private void composeMenuPrincipal(){// méthode réarrangeant le menu selon le statut de l'utilisateur
        if (DataHandler.currentUser == null){ //mode invité
            title.setText("Menu Principal");
            String statut = "Vous n'êtes pas connecté(e).";
            statutTextField.setText(statut);
            menuPrincipalPanel.updateUI();
            connecterButton.setVisible(true);
            connecterButton.setText("Connection");
            ajouterButton.setVisible(false);
            consulterButton.setVisible(true);
            modifierButton.setVisible(false);
        }else if (DataHandler.currentUser instanceof Administrateur){     //mode admin
            title.setText("Menu Administrateur");
            String statut = "Vous êtes connecté(e) en tant que : "+DataHandler.currentUser.getIdentifiant();
            statutTextField.setText(statut);
            menuPrincipalPanel.updateUI();
            connecterButton.setVisible(true);
            connecterButton.setText("Déconnection");
            ajouterButton.setVisible(true);
            consulterButton.setVisible(true);
            modifierButton.setVisible(true);
            modifierButton.setText("Modifier un Utilisateur ou un Administrateur");
        }else if (DataHandler.currentUser instanceof Particulier){ // mode particulier
            title.setText("Menu Principal");
            String statut = "Vous êtes connecté(e) en tant que : "+DataHandler.currentUser.getIdentifiant();
            menuPrincipalPanel.updateUI();
            statutTextField.setText(statut);
            connecterButton.setVisible(true);
            connecterButton.setText("Déconnection");
            ajouterButton.setVisible(false);
            consulterButton.setVisible(true);
            modifierButton.setVisible(true);
            modifierButton.setText("Modifier votre compte");
        }
    }

    public void openConnect(){
        dialog = new ConnectDialog(this); //créé le boite de dialogue gérant la connexion
        dialog.pack(); //ajuste les dimensions et la position du Dialog
        dialog.setVisible(true);
    }

    public JButton getConnecterButton() {
        return connecterButton;
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

    public JButton getQuitterButton() {
        return quitterButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getSource() == menuBar.getReturnToMain()|| e.getActionCommand().equals(MenuModifier.RETURN_TO_MAIN_EVENT_COMMAND)){ // capture de l'event retourner au menu principal de la barre des taches ou de l'event retourner au menu principal du menu modifier
                composeMenuPrincipal();
                myWindow.setContentPane(menuPrincipalPanel);
                menuBar.getReturnToMain().setVisible(false);
            }else if (e.getActionCommand().equals(MenuModifier.MODIFY_ANOTHER_EVENT_COMMAND)){
                MenuModifier menuModifier = new MenuModifier(this);
                myWindow.setContentPane(menuModifier.getModifierPane());
            }
            if (dialog != null) {
                if (e.getSource().equals(dialog.getConnectButton())){ //capture de l'event du bouton connecter de la boite de dialogue de connexion
                   String id = dialog.getUserField().getText();
                   if (DataHandler.listeAdmins.get(id) != null){ // recherche de l'identifiant admin saisi dans le Dialog Connecter
                       System.out.println("id found in listeAdmins");
                       if (Arrays.equals(dialog.getPasswordField().getPassword(), DataHandler.listeAdmins.get(id).getPassword())){ //vérification du mot de passe
                           DataHandler.currentUser = DataHandler.listeAdmins.get(id);
                           if (DataHandler.currentUser != null){
                               composeMenuPrincipal();
                               dialog.dispose(); // fermeture de la boite de dialogue connecter
                               JOptionPane.showMessageDialog(myWindow,"connexion réussie","connexion réussie",JOptionPane.INFORMATION_MESSAGE);// affichage d'une boite de dialogue rapide
                           }
                       }else{
                           throw new AuthentificationException("mot de passe incorrect");// permet de capturer l'exception dans la classe appelante avec un message décrivant la raison
                       }
                   }else if (DataHandler.annuaire.get(id) != null){ // recherche de l'identifiant Particulier saisi dans le Dialog Connecter
                       if (Arrays.equals(dialog.getPasswordField().getPassword(), DataHandler.annuaire.get(id).getPassword())){
                           DataHandler.currentUser = DataHandler.annuaire.get(id);
                           if (DataHandler.currentUser != null){
                               composeMenuPrincipal();
                               dialog.dispose();
                               JOptionPane.showMessageDialog(myWindow,"connexion réussie","connexion réussie",JOptionPane.INFORMATION_MESSAGE);
                           }
                       }else{
                           throw new AuthentificationException("mot de passe incorrect");
                       }
                   }else{
                       System.out.println("connection: identifiant incorrect");
                       throw new AuthentificationException("identifiant incorrect");
                   }
               }
            }
        } catch (AuthentificationException ex) {
            JOptionPane.showMessageDialog(myWindow,ex.getMessage(),"erreur",JOptionPane.ERROR_MESSAGE);
        }
    }
}
