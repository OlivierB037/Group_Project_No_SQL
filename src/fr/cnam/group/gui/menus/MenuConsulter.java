/*
 * Nom de classe : MenuConsulter
 *
 * Description   : pilote le menu permettant de consulter l'annuaire
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
import fr.cnam.group.exceptions.DataException;
import fr.cnam.group.gui.PlaceHolder;
import fr.cnam.group.gui.dialogs.SearchDialog;
import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuConsulter implements PlaceHolder {

    private JPanel consultPane;
    private JPanel userSearchPanel;
    private JTextField nomUserField;
    private JTextField prenomUserField;
    private JTextField identifiantField;
    private JTextField dateNaissanceField;
    private JComboBox searchedUserTypeBox;
    private JTable resultsTable;
    private JButton validerButton;
    private JCheckBox selectAllBox;
    private JLabel nomUserLabel;
    private JLabel prenomUserLabel;
    private JLabel identifiantLabel;
    private JLabel dateLabel;
    private JButton searchTypeButton;
    private JLabel typeParticulierLabel;
    private JComboBox typeParticulierBox;

    private Particulier[] particulierSearchResult;
    private Administrateur[] adminSearchResult;
    private enum Type {Particulier, Administrateur}
    private Type type;
    private MenuPrincipal menuPrincipal;

    public MenuConsulter() {
        /*
         * vérifie le statut de l'utilisateur (administrateur ou particulier)
         */

        showNameFields();
        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
        if (DataHandler.currentUser instanceof Administrateur) {
            searchedUserTypeBox.setVisible(true);

        }
        else{
            searchedUserTypeBox.setSelectedItem("Particulier");
            searchedUserTypeBox.setVisible(false);

        }

        type = Type.Particulier; // par défaut, le type d'utilisateur recherché est particulier
        resultsTable.setVisible(false);
        resultsTable.setAutoCreateRowSorter(true);
        /*
         * affiche une boite de dialogue servant à choisir le type de recherche
         */
        searchTypeButton.addActionListener(e -> {
            hideAllFields();
//            setPlaceHolders();
            manageSearchPanel();
        });

        /*
         * listener sur la checkbox permettant d'afficher l'intégralité des utilisateurs
         */
        selectAllBox.addActionListener((listener) ->{
            if (selectAllBox.isSelected()){
                dropPlaceHolder(dateNaissanceField);
                searchTypeButton.setVisible(false);
                userSearchPanel.setVisible(false);
                typeParticulierLabel.setVisible(false);
                typeParticulierBox.setVisible(false);
                validerButton.setVisible(false);
                validerButton.doClick();
            }
            else{
                typeParticulierLabel.setVisible(true);
                typeParticulierBox.setVisible(true);
                searchTypeButton.setVisible(true);
                validerButton.setVisible(true);
                userSearchPanel.setVisible(true);
                try {
                    resultsTable.setModel(new ResultsTableModel(null));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });

        /*
         * listener sur le type d'utilisateur recherché
         */
        searchedUserTypeBox.addActionListener((e) -> {
            if (searchedUserTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;
                selectAllBox.setVisible(true);
                validerButton.setVisible(false);
                searchTypeButton.setEnabled(false);
                userSearchPanel.setVisible(true);
                nomUserField.setVisible(true);
                nomUserLabel.setVisible(true);
                prenomUserField.setVisible(true);
                prenomUserLabel.setVisible(true);
                typeParticulierBox.setVisible(false);
                typeParticulierLabel.setVisible(false);
            }
            else {
                type = Type.Administrateur;
                selectAllBox.setSelected(true);
                selectAllBox.setVisible(false);
                userSearchPanel.setVisible(false);
                searchTypeButton.setEnabled(false);
                validerButton.setVisible(false);
                validerButton.doClick();
            }
        });


        validerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (selectAllBox.isSelected()){
                        if (type == Type.Particulier) {
                            particulierSearchResult = new Particulier[DataHandler.annuaire.size()+DataHandler.listeAdmins.size()];
                            particulierSearchResult = DataHandler.annuaire.values().toArray(particulierSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(particulierSearchResult);
                            resultsTable.setModel(resultsTableModel);
                        } else {
                            adminSearchResult = new Administrateur[DataHandler.listeAdmins.size()+DataHandler.annuaire.size()];
                            adminSearchResult = DataHandler.listeAdmins.values().toArray(adminSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(adminSearchResult);
                            resultsTable.setModel(resultsTableModel);
                        }
                        resultsTable.setVisible(true);
                        consultPane.updateUI();
                    }
                    else {
                        if (!(dateNaissanceField.getText().isEmpty())) {
                            Particulier.checkDateFormat(dateNaissanceField.getText());
                        }

                        if (typeParticulierBox.getSelectedItem().toString().isEmpty()) {
                            particulierSearchResult = Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText(),identifiantField.getText(), false);
                        }
                        else {
                            particulierSearchResult = Particulier.trouverParticulier(Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString()));
                        }
                        if (particulierSearchResult != null) {
                            ResultsTableModel resultsTableModel = new ResultsTableModel(particulierSearchResult);
                            resultsTable.setModel(resultsTableModel);
                            resultsTable.setVisible(true);
                            consultPane.updateUI();
                        } else {
                            throw new DataException("aucun résultat");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(consultPane, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    /*
     * place des indices dans les zones de saisie
     */
    public void setPlaceHolders(){
        setPlaceHolder(identifiantField, IDENTIFIANT_PLACEHOLDER);
        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
        setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
    }

    /*
     * affiche la boite de dialogue de choix du type de recherche et gère le choix de l'utilisateur
     */
    public void manageSearchPanel(){
        hideAllFields();
        resultsTable.setModel(new ResultsTableModel(null));
        SearchDialog dialog = new SearchDialog(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dropPlaceHolder(dateNaissanceField);

                switch (e.getID()){
                    case SearchDialog.NAME_SEARCH_ID -> {
                        showNameFields();
                        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
                        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
                    }
                    case SearchDialog.ID_SEARCH_ID -> {
                        hideAllFields();
                        setPlaceHolder(identifiantField,IDENTIFIANT_PLACEHOLDER);
                        identifiantField.setVisible(true);
                        identifiantLabel.setVisible(true);
                    }
                    case SearchDialog.DATE_SEARCH_ID -> {
                        hideAllFields();
                        setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
                        dateLabel.setVisible(true);
                        dateNaissanceField.setVisible(true);
                    }
                    case SearchDialog.TYPE_SEARCH_ID -> {
                        hideAllFields();
                        typeParticulierBox.setVisible(true);
                        typeParticulierLabel.setVisible(true);
                    }

                }
            }
        },consultPane);
        dialog.setLocationRelativeTo(consultPane);
        dialog.pack();
        dialog.setVisible(true);
    }
    /*
     * cache les JTextfield et leur JLabel concernant les particulier
     */
    public void hideAllFields(){
        nomUserField.setVisible(false);
        nomUserLabel.setVisible(false);
        prenomUserField.setVisible(false);
        prenomUserLabel.setVisible(false);
        dateNaissanceField.setVisible(false);
        dateLabel.setVisible(false);
        identifiantField.setVisible(false);
        identifiantLabel.setVisible(false);

    }
    /*
     * affiche uniquement les zones de saisies concernant la recherche par nom
     */
    public void showNameFields(){
        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);
        dateNaissanceField.setVisible(false);
        dateLabel.setVisible(false);
        identifiantField.setVisible(false);
        identifiantLabel.setVisible(false);
        typeParticulierBox.setVisible(false);
        typeParticulierLabel.setVisible(false);
    }

    public JPanel getConsultPane() {
        return consultPane;
    }

    public JButton getValiderButton() {
        return validerButton;
    }
}
