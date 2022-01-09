package fr.cnam.group.gui.menus;

import fr.cnam.group.*;
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

    private Step tacheStep;

    MenuPrincipal menuPrincipal;




    public MenuModifier(MenuPrincipal _menuPrincipal) {

        menuPrincipal = _menuPrincipal;
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
                manageSearchPanel();
            });

            selectAllBox.addActionListener(f -> {
                if (selectAllBox.isSelected()) {
                    System.out.println("select all validated, modified type is : " + modifiedType.name());
                    tacheStep = Step.Search;
                    resultsTable.setVisible(true);
                    userSearchPanel.setVisible(false);
                    dropPlaceHolder(dateNaissanceField);
                    validerButton.doClick();

                    modifiedTypeBox.setEnabled(false);

                } else {
                    userSearchPanel.setVisible(true);
                    try {
                        resultsTable.setModel(new ResultsTableModel(null));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    modifiedTypeBox.setEnabled(true);
                    validerButton.setEnabled(true);
                    stepField.setText("remplissez le formulaire pour chercher un particulier");
                    modifierPane.updateUI();
                    tacheStep = Step.Search;
                }
            });
        }

        changePasswordButton.addActionListener(e -> {
            newPasswordField.setVisible(true);
            newPasswordLabel.setVisible(true);
            newPasswordConfirmField.setVisible(true);
            newPasswordConfirmLabel.setVisible(true);
            changePasswordButton.setVisible(false);
        });

        /*
        Listener de la modification du type de compte modifié (Particulier/Administrateur)
         */

        modifiedTypeBox.addActionListener((e) -> {
            if (userType == Type.Administrateur) { // type d'utilisateur : Administrateur

                if (modifiedTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())) {
                    modifiedType = Type.Particulier;
                    resultsTable.setModel(new ResultsTableModel(null));
                    tacheStep = Step.Search;
                    validerButton.setEnabled(true);

                    searchTypeButton.setVisible(true);
                    userSearchPanel.setVisible(true);
                    selectAllBox.setSelected(false);
                    selectAllBox.setVisible(true);
                    manageSearchPanel();

                    identifiantField.setVisible(false);
                    identifiantLabel.setVisible(false);

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
                System.out.println("action performed()");
                try {
                    ResultsTableModel resultsTableModel;
                    if (modifiedType == Type.Particulier) {


                        if (tacheStep == Step.Search) {
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

                                if (!(dateNaissanceField.getText().isEmpty()) && !(Particulier.isDateFormatOk(dateNaissanceField.getText()))) {

                                    throw new Exception("le format de la date doit être MM/DD/YYYY");
                                }
                                if (typeParticulierBox.getSelectedItem().toString().isEmpty()) {
                                    searchResult = Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText(), false);
                                }
                                else{
                                    searchResult = Particulier.trouverParticulier(Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString()));
                                }
                                if (searchResult != null) {
                                    resultsTableModel = new ResultsTableModel(searchResult);
                                    resultsTable.setModel(resultsTableModel);
                                    resultsTable.setVisible(true);
                                    modifierPane.updateUI();

                                } else {
                                    throw new Exception("aucun résultat");
                                }

                            }

                            tacheStep = Step.Select;
                            searchTypeButton.setEnabled(false);
                            validerButton.setEnabled(false);
                            stepField.setText("selectionnez un des résultat");

                            resultsTable.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    super.mouseClicked(e);

                                    if (tacheStep == Step.Select){
                                        System.out.println("mouse clicked on result");
                                        try {
                                            if (selectResult() != -1) {
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
                            System.out.println("select step validated");
                            int row = selectResult();
                            resultsTable.setVisible(false);
                            setPasswordFields(true);
                            setSearchFields(true);
                            if (row != -1) {

                                System.out.println("selection is number "+ row);
                                particulier = (Particulier) searchResult[row];

                                // bloc if de test
                                if (particulier == null) {
                                    throw new Exception("particulier is null");
                                }
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

                            if (userType == Type.Particulier){
                                particulier = (Particulier) DataHandler.currentUser;
                            }

                            System.out.println("identifiant particulier = " + particulier.getIdentifiant());


                            identifiantField.setText(identifiantField.getText().toLowerCase());
                            String nouvelIdentifiant = identifiantField.getText();
                            String nouveauNom = Particulier.formatNames(nomUserField.getText());
                            String nouveauPrenom = Particulier.formatNames(prenomUserField.getText());
                            String nouvelleDateNaissance = dateNaissanceField.getText();
                            char[] finalPassword;


                            if (typeParticulierBox.getSelectedItem().toString().isEmpty()){
                                throw new Exception("vous devez selectionner un type de compte");
                            }

                            if (Account.isIdentifiantFormatOk(nouvelIdentifiant)) {
                                if (!(particulier.getIdentifiant().equals(nouvelIdentifiant))) {
                                    if (!(DataHandler.isIdentifiantavailable(nouvelIdentifiant))) {
                                        throw new Exception("l'identifiant nest pas disponible");
                                    }
                                }
                                if (particulier.checkPassword(currentPasswordField.getPassword())) {
                                    if (newPasswordField.isVisible() && newPasswordField.getPassword().length !=0 && newPasswordConfirmField.getPassword().length != 0 ) {
                                        if (Account.isPasswordFormatOk(newPasswordField.getPassword()) && Account.isPasswordFormatOk(newPasswordConfirmField.getPassword())) {
                                            if (confirmPassword(newPasswordField.getPassword(), newPasswordConfirmField.getPassword())) {
                                                finalPassword = newPasswordField.getPassword();
                                            }
                                            else{
                                                throw new Exception("nouveau mot de passe non confirmé");
                                            }
                                        }else {
                                            throw  new Exception("format du nouveau mot de passe incorrect");
                                        }


                                    }
                                    else {
                                        finalPassword = currentPasswordField.getPassword();
                                    }

                                    if (Particulier.isNameFormatOk(nouveauNom) && Particulier.isNameFormatOk(nouveauPrenom) && Particulier.isDateFormatOk(nouvelleDateNaissance)) {
                                        if (particulier.modify(new Particulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText(), Particulier.generateDateModification(), Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString()), identifiantField.getText(), finalPassword))) {
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
                                            throw new Exception("erreur lors de la modification");
                                        }
                                    } else {
                                        throw new Exception("saisie incorrecte");
                                    }
                                }
                                else{
                                    throw new Exception("mot de passe incorrect, pour effectuer les modifications vous devez saisir votre mot de passe actuel");
                                }
                            }
                        }
                    } else {


                        if (tacheStep == Step.Search) {
                            searchResult = new Administrateur[DataHandler.listeAdmins.size()+DataHandler.annuaire.size()];
                            searchResult = DataHandler.listeAdmins.values().toArray(searchResult);
                            resultsTableModel = new ResultsTableModel(searchResult);
                            resultsTable.setModel(resultsTableModel);
                            resultsTable.setVisible(true);
                            modifierPane.updateUI();

                            searchTypeButton.setEnabled(false);
                            tacheStep = Step.Select;
                            validerButton.setEnabled(false);
                            stepField.setText("selectionnez un des résultat");

                            resultsTable.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    super.mouseClicked(e);

                                    if (tacheStep == Step.Select){
                                        System.out.println("mouse clicked on result");
                                        try {
                                            if (selectResult() != -1) {
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
                            int row = selectResult();
                            setPasswordFields(true);
                            setSearchFields(true);
                            if (!(searchResult[row].getIdentifiant().equals(DataHandler.ROOT_ADMIN_ID))) {
                                System.out.println("id is : " + identifiantField.getText());
                                administrateur = (Administrateur) searchResult[row];
                                identifiantField.setText(administrateur.getIdentifiant());
                                validateSelection();
                                activateDelete(administrateur);

                            } else {
                                throw new Exception("le compte root Administrateur n'est pas modifiable");
                            }

                        } else if (tacheStep == Step.change) {
                            identifiantField.setText(identifiantField.getText().toLowerCase());
                            String nouvelIdentifiant = identifiantField.getText();
                            char[] finalPassword;
                            if (!(administrateur.getIdentifiant().equals(nouvelIdentifiant))) {
                                if (!(DataHandler.isIdentifiantavailable(nouvelIdentifiant))) {
                                    throw new Exception("l'identifiant nest pas disponible");
                                }
                            }

                            if (administrateur.checkPassword(currentPasswordField.getPassword())) {
                                if (newPasswordField.isVisible() && newPasswordField.getPassword().length !=0 && newPasswordConfirmField.getPassword().length != 0 ) {
                                    if (Account.isPasswordFormatOk(newPasswordField.getPassword()) && Account.isPasswordFormatOk(newPasswordConfirmField.getPassword())) {
                                        if (confirmPassword(newPasswordField.getPassword(), newPasswordConfirmField.getPassword())) {
                                            finalPassword = newPasswordField.getPassword();
                                        }
                                        else{
                                            throw new Exception("nouveau mot de passe non confirmé");
                                        }
                                    }else {
                                        throw  new Exception("format du nouveau mot de passe incorrect");
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

                                        tacheStep = Step.Search;
                                        validerButton.setText("chercher");
                                        supprimerButton.setEnabled(false);
                                        modifiedTypeBox.setEnabled(true);

                                        clearTextFields();
                                        setPlaceHolders();
                                        tacheStep = Step.Search;

                                        modifierPane.updateUI();
                                    } else {
                                        menuPrincipal.actionPerformed(new ActionEvent(modifierPane, RETURN_TO_MAIN_EVENT_ID, RETURN_TO_MAIN_EVENT_COMMAND));
                                    }
                                }
                            } else {
                                throw new Exception("l'ancien mot de passe est incorrect");
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

    public void loadParticuliersDatas(Particulier particulier){
        System.out.println("loading selected particulier's datas in text fields");
        dropPlaceHolder(identifiantField);
        dropPlaceHolder(prenomUserField);
        dropPlaceHolder(nomUserField);
        dropPlaceHolder(dateNaissanceField);
        typeParticulierBox.setSelectedItem(particulier.getTypeParticulier().toString());
        identifiantField.setText(particulier.getIdentifiant());
        nomUserField.setText(particulier.getNom());
        prenomUserField.setText(particulier.getPrenom());
        dateNaissanceField.setText(particulier.getDate_naissance());

    }

    public void manageSearchPanel(){
        setParticulierFields(false);

        SearchDialog dialog = new SearchDialog(e -> {
            dropPlaceHolder(dateNaissanceField);

            switch (e.getID()){
                case SearchDialog.NAME_SEARCH_ID -> {
                    showNameFields();
                    setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
                    setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
                }
                case SearchDialog.ID_SEARCH_ID -> {
                    setSearchFields(false);
                    setPasswordFields(false);
                    setPlaceHolder(identifiantField,IDENTIFIANT_PLACEHOLDER);
                    identifiantField.setVisible(true);
                    identifiantLabel.setVisible(true);
                }
                case SearchDialog.DATE_SEARCH_ID -> {
                    setSearchFields(false);
                    setPasswordFields(false);
                    setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
                    dateLabel.setVisible(true);
                    dateNaissanceField.setVisible(true);
                }
                case SearchDialog.TYPE_SEARCH_ID -> {
                    setSearchFields(false);
                    setPasswordFields(false);
                    typeParticulierBox.setVisible(true);
                    typeParticulierLabel.setVisible(true);
                }
            }
        },modifierPane);

        dialog.pack();
        dialog.setLocationRelativeTo(modifierPane);
        dialog.setVisible(true);
    }
    public void setParticulierFields(boolean b){
        nomUserField.setVisible(b);
        nomUserLabel.setVisible(b);
        prenomUserField.setVisible(b);
        prenomUserLabel.setVisible(b);
        dateNaissanceField.setVisible(b);
        dateLabel.setVisible(b);
        typeParticulierBox.setVisible(b);
        typeParticulierLabel.setVisible(b);

    }
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
    public void setPasswordFields(boolean b){
        currentPasswordField.setVisible(b);
        currentPasswordLabel.setVisible(b);
        changePasswordButton.setVisible(b);

    }




    public void showNameFields(){
        setPasswordFields(false);
        setSearchFields(false);
        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);

    }

    public void activateDelete(Account account) {
        supprimerButton.addActionListener(e -> {

            if ((tacheStep == Step.change)) {
                try {

                    int modifyAnother;
                    int response;
                    if(account instanceof Particulier){

                        Particulier particulier = (Particulier) account;
                        if (userType == Type.Administrateur) {
                            response = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous supprimer " + particulier.getPrenom() + " " +
                                    particulier.getNom() + " ?", "confirmer suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        }
                        else{
                            response = JOptionPane.showConfirmDialog(modifierPane, "Cette action est irréversible. \nVoulez vous vraiment supprimer  votre compte ?","suppression du compte", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        }

                        if (response == JOptionPane.YES_OPTION) {
                            if (particulier.remove()) {
                                JOptionPane.showMessageDialog(modifierPane, "Compte supprimé", "succès", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                throw new Exception("echec de la suppression");
                            }

                        }
                        clearTextFields();
                        if (userType == Type.Administrateur) {
                            modifyAnother = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous modifier un autre particulier", "recommencer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        } else {
                            modifyAnother = JOptionPane.NO_OPTION;
                            menuPrincipal.actionPerformed(new ActionEvent(modifierPane,DISCONNECT_EVENT_ID,DISCONNECT_EVENT_COMMAND));
                        }

                    }  else {
                        if (account.checkPassword(currentPasswordField.getPassword())) {
                            response = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous supprimer " +
                                    account.getIdentifiant(), "confirmer suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (response == JOptionPane.YES_OPTION) {
                                if (account.remove()) {
                                    JOptionPane.showMessageDialog(modifierPane, "Administrateur supprimé", "succès", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    throw new Exception("echec de la suppression");
                                }

                            }
                        } else {
                            throw new Exception("l'ancien mot de passe est incorrect");
                        }
                        clearTextFields();
                        modifyAnother = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous modifier un autre administrateur", "recommencer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    }
                    if (modifyAnother == JOptionPane.YES_OPTION) {

                        tacheStep = Step.Search;

                        validerButton.setText("chercher");
                        supprimerButton.setEnabled(false);
                        identifiantField.setEditable(true);
                        modifiedTypeBox.setEnabled(true);
                        clearTextFields();
                        setPlaceHolders();
                        tacheStep = Step.Search;

                        modifierPane.updateUI();
                    } else {
                        menuPrincipal.actionPerformed(new ActionEvent(modifierPane, 370, "returnToMainFromModifier"));

                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(modifierPane, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
                validerButton.setEnabled(true);
            }
        });
    }



    public void validateSelection(){
        changePasswordButton.setVisible(true);
        currentPasswordField.setVisible(true);
        validerButton.setEnabled(true);
        selectAllBox.setEnabled(false);
        modifiedTypeBox.setEnabled(false);
        tacheStep = Step.change;
        supprimerButton.setEnabled(true);
        stepField.setText("entrer les valeurs à modifier");

        validerButton.setText("modifier");
        System.out.println("step vaut: " + tacheStep.toString());
        userSearchPanel.setVisible(true);
    }



    public boolean confirmPassword(char[] password, char[] passwordConfirm){

        if (!Arrays.equals(password, passwordConfirm)) {
            setPlaceHolder(newPasswordField,PASSWORD_PLACEHOLDER);
            newPasswordConfirmField.setText("");
            return false;
        }
        else {
            return true;
        }
    }


    public int selectResult(){

        int selectedRow = resultsTable.getSelectedRow();

        return selectedRow;
        //chosenField.setText(resultsTable.getModel().getValueAt(selectedRow,1).toString());

        /*
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tacheSearchPanel.setVisible(true);
                materielSearchPanel.setVisible(false);
                retourButton.setEnabled(false);
                resultsTable.setVisible(false);
                stepField.setText("remplir le formulaire de recherche de tache et valider.");
                step = AjoutMaterielAssocie.Step.tacheSearch;
            }
        });

         */
    }


    public void clearTextFields(){

        identifiantField.setText("");
        nomUserField.setText("");
        prenomUserField.setText("");
        dateNaissanceField.setText("");
        newPasswordField.setText("");
        newPasswordConfirmField.setText("");
        currentPasswordField.setText("");
    }

    public void setPlaceHolders(){
        setPlaceHolder(identifiantField, IDENTIFIANT_PLACEHOLDER);
        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
        setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
        setPlaceHolder(currentPasswordField,PASSWORD_PLACEHOLDER);
    }

    public JPanel getModifierPane() {
        return modifierPane;
    }

    public JButton getValiderButton() {
        return validerButton;
    }
}
