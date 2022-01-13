/*
 * Nom de classe : MenuModifier
 *
 * Description   : pilote le menu permettant de modifier ou supprimer un compte
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

import fr.cnam.group.*;
import fr.cnam.group.exceptions.AuthentificationException;
import fr.cnam.group.exceptions.DataException;
import fr.cnam.group.exceptions.UserDataInputException;
import fr.cnam.group.gui.PlaceHolder;
import fr.cnam.group.gui.dialogs.SearchDialog;
import fr.cnam.group.users.Account;
import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class MenuModifier implements PlaceHolder {

    private JPanel modifierPane;
    private JPanel userSearchPanel;
    private JTextField nomUserField;
    private JTextField prenomUserField;
    private JTextField identifiantField;
    private JTextField dateNaissanceField;
    private JComboBox modifiedTypeBox;
    private JTable resultsTable;
    private JButton validerButton;
    private JCheckBox selectAllBox;
    private JButton supprimerButton;
    private JTextField stepField;
    private JLabel nomUserLabel;
    private JLabel prenomUserLabel;
    private JLabel dateLabel;
    private JLabel identifiantLabel;
    private JPasswordField newPasswordField;
    private JLabel newPasswordLabel;
    private JPasswordField newPasswordConfirmField;
    private JLabel newPasswordConfirmLabel;
    private JPasswordField currentPasswordField;
    private JLabel currentPasswordLabel;
    private JButton searchTypeButton;
    private JButton changePasswordButton;
    private JComboBox typeParticulierBox;
    private JLabel typeParticulierLabel;
    private JTextField streetTextField;
    private JLabel streetLabel;
    private JTextField codePostalField;
    private JLabel codePostalLabel;
    private JTextField villeField;
    private JLabel villeLabel;
    private JLabel menuAjouterTitle;

    public static final int RESULT_TABLE_EVENT_ID = -372;
    public static final int RETURN_TO_MAIN_EVENT_ID = 370;
    public static final int MODIFY_ANOTHER_EVENT_ID = 360;
    public static final int DISCONNECT_EVENT_ID = 350;
    public static final String MODIFY_ANOTHER_EVENT_COMMAND = "ModifyAnother";
    public static final String RETURN_TO_MAIN_EVENT_COMMAND = "returnToMainFromModifier";
    public static final String DISCONNECT_EVENT_COMMAND = "disconnectUser";

    private Particulier particulier;
    private Administrateur administrateur;
    private Account[] searchResult;
    private enum Step {Search, Select, change, disabled}
    private enum Type {Particulier, Administrateur}
    private Type modifiedType;
    private Type userType;
    private enum SearchType{Id,Name, Date, Statut, all}
    private SearchType searchType;

    private Step tacheStep;

    MenuPrincipal menuPrincipal;

    public MenuModifier(MenuPrincipal menuPrincipal) {

        this.menuPrincipal = menuPrincipal;
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        validerButton.setText("chercher");
        newPasswordField.setVisible(false);
        newPasswordLabel.setVisible(false);
        newPasswordConfirmField.setVisible(false);
        newPasswordConfirmLabel.setVisible(false);

        if (DataHandler.currentUser instanceof Particulier){ // vérification du type d'utilisateur connecté

            /*
            si l'utilisateur est un particulier :
              * tous les éléments graphiques servant à la recherche d'utilisateur sont cachés.
              * la selection du type de compte a modifier est réglée sur Particulier et cachée
              *

             */

            userType = Type.Particulier;
            modifiedType = Type.Particulier;
            System.out.println("menu modifier : connecté en tant que particulier");

            setSearchFields(true);
            setPasswordFields(true);

            modifiedTypeBox.setVisible(false);
            modifiedTypeBox.setSelectedItem("Particulier");

            selectAllBox.setSelected(false);
            selectAllBox.setVisible(false);
            resultsTable.setVisible(false);

            searchTypeButton.setVisible(false);
            tacheStep = Step.change;
            supprimerButton.setVisible(true);
            try {
                activateDelete(DataHandler.currentUser);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(modifierPane, e.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
            }

            validerButton.setText("valider modifications");
            loadParticuliersDatas((Particulier) DataHandler.currentUser);
        }
        else if (DataHandler.currentUser instanceof Administrateur){
            userType = Type.Administrateur;
            modifiedType = Type.Particulier;
            searchType = SearchType.Name;
            System.out.println("menu modifier : connecté en tant qu'Administrateur'");

            selectAllBox.setVisible(true);
            setSearchFields(false);
            supprimerButton.setVisible(false);
            setPasswordFields(false);
            showNameFields();
            resultsTable.setVisible(true);
            tacheStep = Step.Search;
            stepField.setText("remplir le formulaire de recherche");


            searchTypeButton.addActionListener(e -> {
                setSearchFields(false);
                modifySearchType();
            });
            /*
             * listener sur la checkbox permettant d'afficher l'intégralité des utilisateurs
             */
            selectAllBox.addActionListener(f -> {
                validerButton.setEnabled(true);
                if (selectAllBox.isSelected()) {
                    System.out.println("select all validated, modified type is : " + modifiedType.name());
                    tacheStep = Step.Search;
                    resultsTable.setVisible(true);
                    userSearchPanel.setVisible(false);
                    dropPlaceHolder(dateNaissanceField);
                    validerButton.doClick();
                    modifiedTypeBox.setEnabled(false);

                } else {
                    manageSearchPanel();
                    try {
                        resultsTable.setModel(new ResultsTableModel(null));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    modifiedTypeBox.setEnabled(true);

                    stepField.setText("remplissez le formulaire pour chercher un particulier");
                    modifierPane.updateUI();
                    tacheStep = Step.Search;
                }
            });
        }
        /*
         * listener sur le bouton "changer le mot de passe"
         */
        changePasswordButton.addActionListener(e -> {
            newPasswordField.setVisible(true);
            newPasswordLabel.setVisible(true);
            newPasswordConfirmField.setVisible(true);
            newPasswordConfirmLabel.setVisible(true);
            changePasswordButton.setVisible(false);
        });

        /*
        Listener du choix du type de compte modifié (Particulier/Administrateur)
         */

        modifiedTypeBox.addActionListener((e) -> {
            if (userType == Type.Administrateur) { // type d'utilisateur : Administrateur

                if (modifiedTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())) {
                    modifiedType = Type.Particulier;
                    resultsTable.setModel(new ResultsTableModel(null));
                    tacheStep = Step.Search;
                    validerButton.setEnabled(true);
                    searchTypeButton.setVisible(true);
                    selectAllBox.setSelected(false);
                    selectAllBox.setVisible(true);
                    manageSearchPanel();


                } else {
                    modifiedType = Type.Administrateur;
                    tacheStep = Step.Search;
                    searchTypeButton.setVisible(false);
                    resultsTable.setVisible(true);
                    userSearchPanel.setVisible(false);
                    selectAllBox.setSelected(true);
                    selectAllBox.setVisible(false);
                    validerButton.doClick();
                }
            }
        });


        ActionListener validerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
//                    System.out.println("valider clicked");
                    ResultsTableModel resultsTableModel;
                    if (modifiedType == Type.Particulier) {
                        /* modification d'un particulier */
                        if (tacheStep == Step.Search) {

                            /* étape de recherche d'utilisateur */
                            if (selectAllBox.isSelected()){

                                System.out.println("showing all particuliers");
                                searchResult = new Particulier[DataHandler.annuaire.size()+DataHandler.listeAdmins.size()];
                                searchResult = DataHandler.annuaire.values().toArray(searchResult);
                                resultsTableModel = new ResultsTableModel(searchResult);
                                resultsTable.setModel(resultsTableModel);
                                resultsTable.setVisible(true);
                                modifierPane.updateUI();
                            }
                            else {
                                if (!(dateNaissanceField.getText().isEmpty())){
                                    Particulier.checkDateFormat(dateNaissanceField.getText());

                                }
                                try {
                                    switch (searchType){
                                        case Id ->  searchResult = Particulier.trouverParticulier(null, null, null,identifiantField.getText(), false);
                                        case Name -> searchResult = Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText(),null, false);
                                        case Date -> searchResult = Particulier.trouverParticulier(null, null, dateNaissanceField.getText(), null, false);
                                        case Statut -> searchResult = Particulier.trouverParticulier(Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString()));
                                    }
                                } catch (NullPointerException ex) {
                                    throw new UserDataInputException("veuillez remplir un des formulaires ou cliquer sur \"tout consulter\".");
                                }
                                if (searchResult != null) {
                                    resultsTableModel = new ResultsTableModel(searchResult);
                                    resultsTable.setModel(resultsTableModel);
                                    resultsTable.setVisible(true);
                                    modifierPane.updateUI();
                                } else {
                                    throw new DataException("aucun résultat");
                                }
                            }

                            tacheStep = Step.Select;
                            searchTypeButton.setVisible(false);
                            validerButton.setEnabled(false);
                            stepField.setText("selectionnez un des résultat");

                            /* listener permettant de sélectionner un résultat de recherche en cliquant dessus */
                            resultsTable.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    super.mouseClicked(e);

                                    if (tacheStep == Step.Select){
//                                        System.out.println("mouse clicked on result");
                                        try {
                                            if (resultsTable.getSelectedRow() != -1) {
                                                actionPerformed(new ActionEvent(resultsTable,RESULT_TABLE_EVENT_ID,"validateSelect"));
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            });

                        } else if (tacheStep == Step.Select) {

                            /* étape de selection d'utilisateur */

                            System.out.println("select step validated");
                            int row = resultsTable.getSelectedRow();
                            resultsTable.setModel(new ResultsTableModel(null));
                            resultsTable.setVisible(false);
                            setPasswordFields(true);
                            setSearchFields(true);
                            setAddressFields(true);
                            if (row != -1) {
//                                System.out.println("selection is number "+ row);
                                particulier = (Particulier) searchResult[row];

//                                // bloc if de test
//                                if (particulier == null) {
//                                    throw new DataException("particulier is null");
//                                }
                                userSearchPanel.setVisible(true);
                                loadParticuliersDatas(particulier);
                                if (userType == Type.Administrateur){
                                    currentPasswordField.setText(String.valueOf(particulier.getPassword()));
                                    currentPasswordField.setEditable(false);
                                }
                                validateSelection();
                                activateDelete(particulier);
                            }

                        } else if (tacheStep == Step.change) {
                            /* étape de modification */

                            if (userType == Type.Particulier){
                                particulier = (Particulier) DataHandler.currentUser;
                            }
//                            System.out.println("identifiant particulier = " + particulier.getIdentifiant());
                            identifiantField.setText(identifiantField.getText().toLowerCase());
                            String nouvelIdentifiant = identifiantField.getText();
                            String nouveauNom = Particulier.formatNames(nomUserField.getText());
                            String nouveauPrenom = Particulier.formatNames(prenomUserField.getText());
                            String nouvelleDateNaissance = dateNaissanceField.getText();
                            String nouvelleRue = streetTextField.getText();
                            String nouveauPostalCode = codePostalField.getText();
                            String nouvelleVille = villeField.getText();
                            char[] finalPassword;

                            if (typeParticulierBox.getSelectedItem().toString().isEmpty()){
                                throw new UserDataInputException("vous devez selectionner un type de compte");
                            }

                            Particulier.checkIdentifiantFormat(nouvelIdentifiant);
                            if (!(particulier.getIdentifiant().equals(nouvelIdentifiant))) {
                                if (!(DataHandler.isIdentifiantavailable(nouvelIdentifiant))) {
                                    throw new DataException("l'identifiant nest pas disponible");
                                }
                            }
                            if (particulier.checkPasswordEquality(currentPasswordField.getPassword())) {
                                if (newPasswordField.isVisible() && newPasswordField.getPassword().length !=0 && newPasswordConfirmField.getPassword().length != 0 ) {
                                    if (confirmPassword(newPasswordField.getPassword(), newPasswordConfirmField.getPassword())) {
                                        Particulier.checkPasswordFormat(newPasswordField.getPassword());
                                        finalPassword = newPasswordField.getPassword();
                                    }
                                    else{
                                        throw new AuthentificationException("nouveau mot de passe non confirmé");
                                    }
                                }
                                else {
                                    finalPassword = currentPasswordField.getPassword();
                                }

                                Particulier.checkNameFormat(nouveauNom);
                                Particulier.checkNameFormat(nouveauPrenom);
                                Particulier.checkDateFormat(nouvelleDateNaissance);
                                Particulier.checkAdresseFormat(nouvelleRue,nouveauPostalCode,nouvelleVille);
                                String adresse = Particulier.formatAdresse(nouvelleRue,nouveauPostalCode,nouvelleVille);

                                if (particulier.modify(new Particulier(nouveauNom, nouveauPrenom, nouvelleDateNaissance,adresse, Particulier.generateDateModification(), Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString()), nouvelIdentifiant, finalPassword))) {
                                    supprimerButton.setEnabled(false);
                                    clearTextFields();
                                    int response;
                                    if (userType == Type.Administrateur) {
                                        response = JOptionPane.showConfirmDialog(modifierPane, "Modification effectuée.\nVoulez vous modifier un autre particulier", "modification effectuée", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    }
                                    else{
                                        response = JOptionPane.NO_OPTION;
                                        JOptionPane.showMessageDialog(modifierPane,"Modification effectuée, retour au menu Principal","Succès",JOptionPane.INFORMATION_MESSAGE);
                                        System.out.println("modification réussie ");
                                    }
                                    if (response == JOptionPane.YES_OPTION) {
                                        menuPrincipal.actionPerformed(new ActionEvent(modifierPane,MODIFY_ANOTHER_EVENT_ID,MODIFY_ANOTHER_EVENT_COMMAND));
                                    } else {
                                        menuPrincipal.actionPerformed(new ActionEvent(modifierPane, RETURN_TO_MAIN_EVENT_ID, RETURN_TO_MAIN_EVENT_COMMAND));
                                    }
                                } else {
                                    throw new DataException("erreur lors de la modification");
                                }
                            }
                            else{
                                throw new AuthentificationException("mot de passe incorrect, pour effectuer les modifications vous devez saisir votre mot de passe actuel");
                            }
                        }
                    } else {
                        /* modification d'un administrateur */
                        if (tacheStep == Step.Search) {
                            /* étape de recherche d'utilisateur (validée automatiquement en raison du petit nombre d'administrateurs)*/
                            searchResult = new Administrateur[DataHandler.listeAdmins.size()+DataHandler.annuaire.size()];
                            searchResult = DataHandler.listeAdmins.values().toArray(searchResult);
                            resultsTableModel = new ResultsTableModel(searchResult);
                            resultsTable.setModel(resultsTableModel);
                            resultsTable.setVisible(true);
                            modifierPane.updateUI();

                            searchTypeButton.setVisible(false);
                            tacheStep = Step.Select;
                            validerButton.setEnabled(false);
                            stepField.setText("selectionnez un des résultat");

                            /* listener permettant de sélectionner un résultat de recherche en cliquant dessus */
                            resultsTable.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                super.mouseClicked(e);

                                if (tacheStep == Step.Select){
//                                    System.out.println("mouse clicked on result");
                                    try {
                                        if (resultsTable.getSelectedRow() != -1) {
                                            System.out.println("clicking valider");
                                            actionPerformed(new ActionEvent(resultsTable,RESULT_TABLE_EVENT_ID,"validateSelect"));
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                }
                            });

                        } else if (tacheStep == Step.Select) {

                            /* étape de selection d'utilisateur */
                            int row = resultsTable.getSelectedRow();
                            setPasswordFields(true);
                            setSearchFields(true);
                            if (!(searchResult[row].getIdentifiant().equals(DataHandler.ROOT_ADMIN_ID))) {
                                System.out.println("id is : " + identifiantField.getText());
                                administrateur = (Administrateur) searchResult[row];
                                identifiantField.setText(administrateur.getIdentifiant());
                                validateSelection();
                                activateDelete(administrateur);

                            } else {
                                throw new AuthentificationException("le compte root Administrateur n'est pas modifiable");
                            }

                        } else if (tacheStep == Step.change) {

                            /* étape de modification */
                            identifiantField.setText(identifiantField.getText().toLowerCase());
                            String nouvelIdentifiant = identifiantField.getText();
                            char[] finalPassword;
                            if (!(administrateur.getIdentifiant().equals(nouvelIdentifiant))) {
                                Administrateur.checkIdentifiantFormat(nouvelIdentifiant);
                                if (!(DataHandler.isIdentifiantavailable(nouvelIdentifiant))) {
                                    throw new DataException("l'identifiant nest pas disponible");
                                }
                            }

                            if (administrateur.checkPasswordEquality(currentPasswordField.getPassword())) {
                                if (newPasswordField.isVisible() && newPasswordField.getPassword().length !=0 && newPasswordConfirmField.getPassword().length != 0 ) {
                                    Account.checkPasswordFormat(newPasswordField.getPassword());
                                    Account.checkPasswordFormat(newPasswordConfirmField.getPassword());
                                    if (confirmPassword(newPasswordField.getPassword(), newPasswordConfirmField.getPassword())) {
                                        finalPassword = newPasswordField.getPassword();
                                    }
                                    else{
                                        throw new AuthentificationException("nouveau mot de passe non confirmé");
                                    }
                                }
                                else {
                                    finalPassword = currentPasswordField.getPassword();
                                }
                                if (administrateur.modify(new Administrateur(nouvelIdentifiant, finalPassword))) {
                                    supprimerButton.setEnabled(false);
                                    clearTextFields();

                                    int response = JOptionPane.showConfirmDialog(modifierPane, "Modification effectuée.\nVoulez vous modifier un autre particulier", "modification effectuée", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                    if (response == JOptionPane.YES_OPTION) {
                                        menuPrincipal.actionPerformed(new ActionEvent(modifierPane,MODIFY_ANOTHER_EVENT_ID,MODIFY_ANOTHER_EVENT_COMMAND));
                                    } else {
                                        menuPrincipal.actionPerformed(new ActionEvent(modifierPane, RETURN_TO_MAIN_EVENT_ID, RETURN_TO_MAIN_EVENT_COMMAND));
                                    }
                                }
                            } else {
                                throw new AuthentificationException("l'ancien mot de passe est incorrect");
                            }
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(modifierPane, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        validerButton.addActionListener(validerListener);
    }

    /*
     * charge les données de l'utilisateur choisi dans les zones de saisie
     */
    public void loadParticuliersDatas(Particulier particulier){
//        System.out.println("loading selected particulier's datas in text fields");

        dropPlaceHolder(dateNaissanceField);


        typeParticulierBox.setSelectedItem(particulier.getTypeParticulier().toString());
        identifiantField.setText(particulier.getIdentifiant());
        nomUserField.setText(particulier.getNom());
        prenomUserField.setText(particulier.getPrenom());
        dateNaissanceField.setText(particulier.getDate_naissance());
        streetTextField.setText(particulier.getAdresse().split(",")[0]);
        String postal = particulier.getAdresse().split(",")[1].substring(1,6);
        codePostalField.setText(postal);
        villeField.setText(particulier.getAdresse().split(",")[1].substring(7));
    }

    public void manageSearchPanel(){
        switch (searchType){
            case Id -> {
                setSearchFields(false);
                setPlaceHolder(identifiantField,IDENTIFIANT_PLACEHOLDER);
                identifiantField.setVisible(true);
                identifiantLabel.setVisible(true);
                userSearchPanel.setVisible(true);
            }
            case Name -> {
                showNameFields();
                setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
                setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
                userSearchPanel.setVisible(true);
            }
            case Date -> {
                setSearchFields(false);
                setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
                dateLabel.setVisible(true);
                dateNaissanceField.setVisible(true);
                userSearchPanel.setVisible(true);
            }
            case Statut -> {
                setSearchFields(false);
                typeParticulierBox.setVisible(true);
                typeParticulierLabel.setVisible(true);
                userSearchPanel.setVisible(true);
            }

        }
    }


    /*
     * affiche la boite de dialogue de choix du type de recherche et gère le choix de l'utilisateur
     */
    public void modifySearchType(){
        setParticulierFields(false);

        SearchDialog dialog = new SearchDialog(e -> {
            dropPlaceHolder(dateNaissanceField);
            switch (e.getID()){
                case SearchDialog.NAME_SEARCH_ID -> {
                    searchType = SearchType.Name;
                }
                case SearchDialog.ID_SEARCH_ID -> {
                    searchType = SearchType.Id;
                }
                case SearchDialog.DATE_SEARCH_ID -> {
                    searchType = SearchType.Date;
                }
                case SearchDialog.TYPE_SEARCH_ID -> {
                    searchType = SearchType.Statut;
                }
            }
            manageSearchPanel();
        },modifierPane);
        dialog.pack();
        dialog.setLocationRelativeTo(modifierPane);
        dialog.setVisible(true);
    }

    /*
     * modifie la visibilité des éléments graphiques nécéssaires liés aux particuliers
     */
    public void setParticulierFields(boolean b){
        nomUserField.setVisible(b);
        nomUserLabel.setVisible(b);
        prenomUserField.setVisible(b);
        prenomUserLabel.setVisible(b);
        dateNaissanceField.setVisible(b);
        dateLabel.setVisible(b);
        streetTextField.setVisible(b);
        streetLabel.setVisible(b);
        codePostalField.setVisible(b);
        codePostalLabel.setVisible(b);
        villeField.setVisible(b);
        villeLabel.setVisible(b);
        typeParticulierBox.setVisible(b);
        typeParticulierLabel.setVisible(b);

    }

    /*
     * modifie la visibilité des éléments graphiques nécessaires aux recherches d'utilisateur
     */
    public void setSearchFields(boolean b){
        nomUserField.setVisible(b);
        nomUserLabel.setVisible(b);
        prenomUserField.setVisible(b);
        prenomUserLabel.setVisible(b);
        dateNaissanceField.setVisible(b);
        dateLabel.setVisible(b);
        identifiantField.setVisible(b);
        identifiantLabel.setVisible(b);
        typeParticulierBox.setVisible(b);
        typeParticulierLabel.setVisible(b);
    }

    /*
     * modifie la visibilité des éléments graphiques liés aux mots de passe
     */
    public void setPasswordFields(boolean b){
        currentPasswordField.setVisible(b);
        currentPasswordLabel.setVisible(b);
        changePasswordButton.setVisible(b);

    }

    /*
     * modifie la visibilité des éléments graphiques liés aux adresses
     */
    public void setAddressFields(boolean b){
        streetTextField.setVisible(b);
        streetLabel.setVisible(b);
        codePostalField.setVisible(b);
        codePostalLabel.setVisible(b);
        villeField.setVisible(b);
        villeLabel.setVisible(b);
    }

    /*
     * affiche uniquement les zones de saisies concernant la recherche par nom
     */
    public void showNameFields(){
        setPasswordFields(false);
        setSearchFields(false);
        streetTextField.setVisible(false);
        streetLabel.setVisible(false);
        codePostalField.setVisible(false);
        codePostalLabel.setVisible(false);
        villeField.setVisible(false);
        villeLabel.setVisible(false);
        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);

    }

    /*
     * active et gère la fonction de suppression du compte
     */
    public void activateDelete(Account account) {
        supprimerButton.setVisible(true);
        supprimerButton.addActionListener(e -> {
            if ((tacheStep == Step.change)) {
                try {
                    int modifyAnother;
                    int response;
                    if ((account.checkPasswordEquality(currentPasswordField.getPassword()))) {
                        if (account instanceof Particulier) {
                            Particulier particulier = (Particulier) account;
                            if (userType == Type.Administrateur) {
                                response = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous supprimer " + particulier.getPrenom() + " " +
                                        particulier.getNom() + " ?", "confirmer suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            } else {
                                response = JOptionPane.showConfirmDialog(modifierPane, "Cette action est irréversible. \nVoulez vous vraiment supprimer  votre compte ?", "suppression du compte", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            }

                            if (response == JOptionPane.YES_OPTION) {
                                if (particulier.remove()) {
                                    JOptionPane.showMessageDialog(modifierPane, "Compte supprimé", "succès", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    throw new DataException("echec de la suppression");
                                }
                            }
                            clearTextFields();
                            if (userType == Type.Administrateur) {
                                modifyAnother = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous modifier un autre particulier", "recommencer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            } else {
                                modifyAnother = JOptionPane.NO_OPTION;
                                DataHandler.currentUser = null;
                            }

                        } else {
                            response = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous supprimer " +
                                    account.getIdentifiant(), "confirmer suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (response == JOptionPane.YES_OPTION) {
                                if (account.remove()) {
                                    JOptionPane.showMessageDialog(modifierPane, "Administrateur supprimé", "succès", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    throw new DataException("echec de la suppression");
                                }
                            }
                            clearTextFields();
                            modifyAnother = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous modifier un autre administrateur", "recommencer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        }
                        if (modifyAnother == JOptionPane.YES_OPTION) {
                            menuPrincipal.actionPerformed(new ActionEvent(modifierPane,MODIFY_ANOTHER_EVENT_ID,MODIFY_ANOTHER_EVENT_COMMAND));
                        } else {
                            menuPrincipal.actionPerformed(new ActionEvent(modifierPane, 370, "returnToMainFromModifier"));
                        }
                    }
                    else {
                        throw new AuthentificationException("l'ancien mot de passe est incorrect");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(modifierPane, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
                validerButton.setEnabled(true);
            }
        });
    }

    /*
     * affiche les éléments nécessaires à la modification
     */
    public void validateSelection(){
        changePasswordButton.setVisible(true);
        currentPasswordField.setVisible(true);
        validerButton.setEnabled(true);
        selectAllBox.setVisible(false);
        modifiedTypeBox.setVisible(false);
        tacheStep = Step.change;
        supprimerButton.setEnabled(true);
        stepField.setText("entrer les valeurs à modifier");
        validerButton.setText("modifier");
        userSearchPanel.setVisible(true);
    }

    /*
     * vérifie la confirmation du mot de passe
     */
    public boolean confirmPassword(char[] password, char[] passwordConfirm){

        if (!Arrays.equals(password, passwordConfirm)) {
            newPasswordField.setText("");
            newPasswordConfirmField.setText("");
            return false;
        }
        else {
            return true;
        }
    }


    /*
     * vide les zone de saisie de texte
     */
    public void clearTextFields(){

        identifiantField.setText("");
        nomUserField.setText("");
        prenomUserField.setText("");
        dateNaissanceField.setText("");
        newPasswordField.setText("");
        newPasswordConfirmField.setText("");
        currentPasswordField.setText("");
        streetTextField.setText("");
        codePostalField.setText("");
        villeField.setText("");
    }



    public JPanel getModifierPane() {
        return modifierPane;
    }

    public JButton getValiderButton() {
        return validerButton;
    }
}
