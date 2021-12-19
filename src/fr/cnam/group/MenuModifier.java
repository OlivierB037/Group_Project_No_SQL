package fr.cnam.group;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;


import static fr.cnam.group.Main.currentParticulier;

public class MenuModifier {

    private JPanel modifierPane;
    private JPanel userSearchPanel;
    private JTextField nomUserField;
    private JTextField prenomUserField;
    private JTextField identifiantField;
    private JFormattedTextField dateNaissanceField;
    private JComboBox statutUserBox;
    private JTable resultsTable;
    private JButton validerButton;
    private JTextField thanksField;
    private JCheckBox selectAllBox;
    private JButton supprimerButton;
    private JTextField stepField;
    private JLabel nomUserLabel;
    private JLabel prenomUserLabel;
    private JLabel dateLabel;
    private JLabel identifiantLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JPasswordField passwordConfirmField;
    private JLabel passwordConfirmLabel;
    private JPasswordField previousPasswordField;
    private JLabel previousPasswordLabel;

    private final int RESULT_TABLE_EVENT_ID = -372;
    private final int SELECT_ALL_EVENT_ID = -382;
    private final int RETURN_TO_MAIN_EVENT_ID = 370;

    private Particulier particulier;
    private Particulier[] searchResult;

    private Administrateur administrateur;
    private Administrateur[] adminSearchresult;

    private enum Step {Search, Select, change, disabled}

    private enum Type {Particulier, Administrateur}

    private Type type;

    private Step tacheStep;

    private int selectedRow;

    MenuPrincipal menuPrincipal;




    public MenuModifier(MenuPrincipal _menuPrincipal) {

        menuPrincipal = _menuPrincipal;
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        validerButton.setText("chercher");
        supprimerButton.setEnabled(false);

        setFieldsForParticulier();
        System.out.println("test 1");
        resultsTable.setVisible(true);
        type = Type.Particulier;

        statutUserBox.addActionListener((e) -> {
            if (statutUserBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;

                tacheStep = Step.Search;
                userSearchPanel.setVisible(true);
                selectAllBox.setSelected(false);
                selectAllBox.setVisible(true);
                setFieldsForParticulier();

//                identifiantField.setVisible(false);
//                identifiantLabel.setVisible(false);
            }
            else {
                type = Type.Administrateur;
                tacheStep = Step.Search;
                setFieldsForAdmins();
                userSearchPanel.setVisible(false);
                selectAllBox.setSelected(true);
                selectAllBox.setVisible(false);
                validerButton.doClick();




            }
        });



        ActionListener validerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {


                        if (tacheStep == Step.Search) {
                            System.out.println("if step search validé");
                            if (selectAllBox.isSelected()) {

                                ResultsTableModel resultsTableModel;
                                if (type == Type.Particulier) {
                                    searchResult = new Particulier[Main.annuaire.size()];
                                    searchResult = Main.annuaire.values().toArray(searchResult);
                                    resultsTableModel = new ResultsTableModel(searchResult);
                                } else {
                                    System.out.println("test 3");
                                    adminSearchresult = new Administrateur[Main.listeAdmins.size()];
                                    adminSearchresult = Main.listeAdmins.values().toArray(adminSearchresult);
                                    System.out.println("test 4");
                                    resultsTableModel =new ResultsTableModel(adminSearchresult);
                                }
                                resultsTable.setModel(resultsTableModel);
                                resultsTable.setVisible(true);
                                modifierPane.updateUI();

                            } else {

                                if (!(dateNaissanceField.getText().isEmpty()) && !(Particulier.isDateFormatOk(dateNaissanceField.getText()))) {

                                    throw new Exception("le format de la date doit être MM/DD/YYYY");
                                }
                                searchResult = Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText(), false);
                                if (searchResult != null) {
                                    ResultsTableModel resultsTableModel = new ResultsTableModel(searchResult);
                                    resultsTable.setModel(resultsTableModel);
                                    resultsTable.setVisible(true);
                                    modifierPane.updateUI();

                                } else {
                                    throw new Exception("aucun résultat");
                                }



                            }
                            tacheStep = Step.Select;
                            identifiantField.setEditable(false);

                            //validerButton.setText("Sélectionner");
                            validerButton.setEnabled(false);
                            stepField.setText("selectionnez un des résultat et validez");
                            resultsTable.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    super.mouseClicked(e);
                                    if (tacheStep == Step.Select){
                                        try {
                                            if (selectResult() != -1) {
                                                actionPerformed(new ActionEvent(resultsTable,RESULT_TABLE_EVENT_ID,"triggerValidation"));
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            });


                        } else if (tacheStep == Step.Select) {


                            System.out.println("selectTache activé");
                            int row = selectResult();

                            if (row != -1) {

                                if (type == Type.Particulier) {
                                    particulier = searchResult[row];

                                    identifiantField.setText(particulier.getIdentifiant());
                                    nomUserField.setText(particulier.getNom());
                                    prenomUserField.setText(particulier.getPrenom());
                                    dateNaissanceField.setText(particulier.getDate_naissance());
                                } else {
                                    if (!(adminSearchresult[row].getIdentifiant().equals(Main.ROOT_ADMIN_ID))) {
                                        System.out.println("id is : " + identifiantField.getText());
                                        administrateur = adminSearchresult[row];
                                        identifiantField.setText(administrateur.getIdentifiant());
                                    } else {
                                        throw new Exception("le compte root Administrateur n'est pas modifiable");
                                    }

                                }

                                validerButton.setEnabled(true);
                                selectAllBox.setEnabled(false);
                                statutUserBox.setEnabled(false);
                                tacheStep = Step.change;
                                supprimerButton.setEnabled(true);
                                stepField.setText("entrer les valeurs à modifier");

                                validerButton.setText("modifier");
                                System.out.println("step vaut: " + tacheStep.toString());
                                userSearchPanel.setVisible(true);
                            } else {
                                throw new Exception("veuillez sélectionner un particulier");
                            }

                        } else if (tacheStep == Step.change) {
                            System.out.println("modify activé");
                            if (type == Type.Particulier) {
                                String nouveauNom = Particulier.formatNames(nomUserField.getText());
                                String nouveauPrenom = Particulier.formatNames(prenomUserField.getText());
                                String nouvelleDateNaissance = dateNaissanceField.getText();


                                if (Particulier.isNameFormatOk(nouveauNom) && Particulier.isNameFormatOk(nouveauPrenom) && Particulier.isDateFormatOk(nouvelleDateNaissance)) {
                                    if (Particulier.modifyParticulier(particulier, new Particulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText()))) {
                                        supprimerButton.setEnabled(false);
                                        clearTextFields();
                                        int response = JOptionPane.showConfirmDialog(modifierPane, "Modification effectuée.\nVoulez vous modifier un autre particulier", "modification effectuée", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                        if (response == JOptionPane.YES_OPTION) {

                                            tacheStep = Step.Search;
                                            validerButton.setText("chercher");
                                            supprimerButton.setEnabled(false);
                                            identifiantField.setEditable(true);
                                            statutUserBox.setEnabled(true);
                                            clearTextFields();

                                            tacheStep = Step.Search;

                                            modifierPane.updateUI();
                                        } else {
                                            menuPrincipal.actionPerformed(new ActionEvent(modifierPane, RETURN_TO_MAIN_EVENT_ID, "returnToMainFromModifier"));

                                        }
                                    }
                                    else{
                                        throw new Exception("erreur lors de la modification");
                                    }
                                } else {
                                    throw new Exception("saisie incorrecte");


                                }
                            } else {

                                    String nouvelIdentifiant = identifiantField.getText();
                                    if(Administrateur.isIdentifiantFormatOk(nouvelIdentifiant)){
                                        if (Main.isIdentifiantavailable(nouvelIdentifiant)){

                                            if (administrateur.checkPassword(previousPasswordField.getPassword())) {
                                                if (confirmPassword(passwordField.getPassword(),passwordConfirmField.getPassword())) {
                                                    if (Administrateur.modifyAdministrateur(administrateur, new Administrateur(nouvelIdentifiant, passwordField.getPassword()))){
                                                        supprimerButton.setEnabled(false);
                                                        clearTextFields();
                                                        int response = JOptionPane.showConfirmDialog(modifierPane, "Modification effectuée.\nVoulez vous modifier un autre particulier", "modification effectuée", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                                        if (response == JOptionPane.YES_OPTION) {

                                                            tacheStep = Step.Search;
                                                            validerButton.setText("chercher");
                                                            supprimerButton.setEnabled(false);
                                                            statutUserBox.setEnabled(true);

                                                            clearTextFields();
                                                            tacheStep = Step.Search;

                                                            modifierPane.updateUI();
                                                        } else {
                                                            menuPrincipal.actionPerformed(new ActionEvent(modifierPane, RETURN_TO_MAIN_EVENT_ID, "returnToMainFromModifier"));

                                                        }
                                                    }


                                                }
                                                else{
                                                    throw new Exception("mot de passe non confirmé");
                                                }
                                            } else {
                                                throw new Exception("l'ancien mot de passe est incorrect");
                                            }
                                        }else{
                                            throw new Exception("identifiant déja pris");
                                        }
                                    }
                                    else{
                                        throw new Exception("identifiant non valide");
                                    }

                            }
                        }



                } catch(Exception ex){
                    JOptionPane.showMessageDialog(modifierPane, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };


        if (currentParticulier != null) {
            int response = JOptionPane.showConfirmDialog(modifierPane, "le particulier concerné est-il " + currentParticulier.getNom() +
                    " " + currentParticulier.getPrenom() + " ?", "current User", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                nomUserField.setText(currentParticulier.getNom());
                prenomUserField.setText(currentParticulier.getPrenom());
                dateNaissanceField.setText(currentParticulier.getDate_naissance());
                tacheStep = Step.change;
            } else {
                nomUserField.setText("");
                prenomUserField.setText("");
                dateNaissanceField.setText("");
                tacheStep = Step.Search;
            }

        } else {



            tacheStep = Step.Search;
            stepField.setText("remplir le formulaire de recherche");

            System.out.println("test 1");
            selectAllBox.addActionListener(f -> {
                if (selectAllBox.isSelected()){
                    userSearchPanel.setVisible(false);
                    validerListener.actionPerformed(new ActionEvent(selectAllBox,SELECT_ALL_EVENT_ID,"SearchAllParticuliers"));
                    tacheStep = Step.Select;
                }
                else {
                    userSearchPanel.setVisible(true);
                    try {
                        resultsTable.setModel(new ResultsTableModel(null));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    validerButton.setEnabled(true);
                    stepField.setText("remplissez le formulaire pour chercher un particulier");
                    modifierPane.updateUI();
                    tacheStep = Step.Search;
                }
            });



            validerButton.addActionListener(validerListener);


            supprimerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if ((tacheStep == Step.change)) {
                        try {
                            int returnToMain;
                            if (type == Type.Particulier) {
                                int response = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous supprimer " + particulier.getPrenom()+ " "+
                                        particulier.getNom()+ " ?", "confirmer suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (response == JOptionPane.YES_OPTION){
                                    if (Particulier.deleteParticulier(particulier)){
                                        JOptionPane.showMessageDialog(modifierPane,"Particulier supprimé","succès",JOptionPane.INFORMATION_MESSAGE);
                                    }
                                    else{
                                        throw new Exception("echec de la suppression");
                                    }

                                }
                                clearTextFields();
                                returnToMain = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous modifier un autre particulier", "recommencer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            } else {
                                if (administrateur.checkPassword(previousPasswordField.getPassword()) ) {
                                    int response = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous supprimer " +
                                            administrateur.getIdentifiant(), "confirmer suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    if (response == JOptionPane.YES_OPTION){
                                        if (Administrateur.removeAdministrateur(administrateur)){
                                            JOptionPane.showMessageDialog(modifierPane,"Administrateur supprimé","succès",JOptionPane.INFORMATION_MESSAGE);
                                        }
                                        else{
                                            throw new Exception("echec de la suppression");
                                        }

                                    }
                                } else {
                                    throw new Exception("l'ancien mot de passe est incorrect");
                                }
                                clearTextFields();
                                returnToMain = JOptionPane.showConfirmDialog(modifierPane, "Voulez vous modifier un autre administrateur", "recommencer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            }

                            if (returnToMain == JOptionPane.YES_OPTION) {

                                tacheStep = Step.Search;

                                validerButton.setText("chercher");
                                supprimerButton.setEnabled(false);
                                identifiantField.setEditable(true);
                                statutUserBox.setEnabled(true);
                                clearTextFields();
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
                }
            });
        }
    }

    public boolean confirmPassword(char[] password, char[] passwordConfirm) throws Exception {

        if (!Arrays.equals(password, passwordConfirm)) {
            return false;
        }
        else {
            return true;
        }
    }


    public void setFieldsForAdmins(){

        identifiantField.setEditable(true);
        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);
        previousPasswordField.setVisible(true);
        previousPasswordLabel.setVisible(true);
        passwordField.setVisible(true);
        passwordLabel.setVisible(true);
        passwordConfirmField.setVisible(true);
        passwordConfirmLabel.setVisible(true);

        nomUserField.setVisible(false);
        nomUserLabel.setVisible(false);
        prenomUserField.setVisible(false);
        prenomUserLabel.setVisible(false);
        dateNaissanceField.setVisible(false);
        dateLabel.setVisible(false);



    }
    public void setFieldsForParticulier(){
        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);
        identifiantField.setEditable(false);
        previousPasswordField.setVisible(false);
        previousPasswordLabel.setVisible(false);
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);
        passwordConfirmField.setVisible(false);
        passwordConfirmLabel.setVisible(false);

        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);
        dateNaissanceField.setVisible(true);
        dateLabel.setVisible(true);
    }



    public int selectResult() throws Exception {

        selectedRow = resultsTable.getSelectedRow();

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
        passwordField.setText("");
        passwordConfirmField.setText("");
        previousPasswordField.setText("");
    }







    public JPanel getConsultPane() {
        return modifierPane;
    }



    public JButton getValiderButton() {
        return validerButton;
    }
}
