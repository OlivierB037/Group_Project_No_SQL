package fr.cnam.group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuConsulter implements PlaceHolder{

    private JPanel consultPane;
    private JPanel userSearchPanel;
    private JTextField nomUserField;
    private JTextField prenomUserField;
    private JTextField identifiantField;
    private JTextField dateNaissanceField;
    private JComboBox userTypeBox;

    private JTable resultsTable;
    private JButton validerButton;

    private JCheckBox selectAllBox;
    private JLabel nomUserLabel;
    private JLabel prenomUserLabel;
    private JLabel identifiantLabel;
    private JLabel dateLabel;
    private JButton searchTypeButton;


    private Particulier[] particulierSearchResult;
    private Account[] accountSearchResult;
    private enum Type {Particulier, Administrateur}
    private Type type;
    private MenuPrincipal menuPrincipal;




    public MenuConsulter() {




        if (DataHandler.currentUser instanceof Administrateur) {

            userTypeBox.setVisible(true);

        }
        else{
            userTypeBox.setSelectedItem("Particulier");
            userTypeBox.setVisible(false);
            showNameFields();
            setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
            setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);


        }
        type = Type.Particulier;
        resultsTable.setVisible(false);
        resultsTable.setAutoCreateRowSorter(true);


        searchTypeButton.addActionListener(e -> {
            hideAllFields();
//            setPlaceHolders();
            manageSearchPanel();
        });



        selectAllBox.addActionListener((listener) ->{
            if (selectAllBox.isSelected()){
                dropPlaceHolder(dateNaissanceField);
                searchTypeButton.setVisible(false);
                userSearchPanel.setVisible(false);
                validerButton.setVisible(false);
                validerButton.doClick();
            }
            else{
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

        userTypeBox.addActionListener((e) -> {
            if (userTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;
                selectAllBox.setVisible(true);

                nomUserField.setVisible(true);
                nomUserLabel.setVisible(true);
                prenomUserField.setVisible(true);
                prenomUserLabel.setVisible(true);
//                dateNaissanceField.setVisible(true);
//                dateLabel.setVisible(true);
//                identifiantField.setVisible(false);
//                identifiantLabel.setVisible(false);
            }
            else {
                type = Type.Administrateur;
                selectAllBox.setSelected(true);
                selectAllBox.setVisible(false);
                validerButton.doClick();

//                nomUserField.setVisible(false);
//                nomUserLabel.setVisible(false);
//                prenomUserField.setVisible(false);
//                prenomUserLabel.setVisible(false);
//                dateNaissanceField.setVisible(false);
//                dateLabel.setVisible(false);
//                identifiantField.setVisible(true);
//                identifiantLabel.setVisible(true);

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
                            accountSearchResult = new Administrateur[DataHandler.listeAdmins.size()+DataHandler.annuaire.size()];
                            accountSearchResult = DataHandler.listeAdmins.values().toArray(accountSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(accountSearchResult);
                            resultsTable.setModel(resultsTableModel);
                        }
                        resultsTable.setVisible(true);
                        consultPane.updateUI();
                    }
                    else {
                        System.out.println("test consulter");
                        if (!(dateNaissanceField.getText().isEmpty()) && !(Particulier.isDateFormatOk(dateNaissanceField.getText()))) {

                            throw new Exception("le format de la date doit être MM/DD/YYYY");
                        }


                        particulierSearchResult = Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateNaissanceField.getText(), false);
                        if (particulierSearchResult != null) {
                            ResultsTableModel resultsTableModel = new ResultsTableModel(particulierSearchResult);
                            resultsTable.setModel(resultsTableModel);
                            resultsTable.setVisible(true);
                            consultPane.updateUI();




                        } else {
                            throw new Exception("aucun résultat");
                        }


                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(consultPane, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }


            }
        });
    }

    public void setPlaceHolders(){
        setPlaceHolder(identifiantField, IDENTIFIANT_PLACEHOLDER);
        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
        setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
//        setPlaceHolder(newPasswordField,PASSWORD_PLACEHOLDER);
//        setPlaceHolder(newPasswordConfirmField,PASSWORD_PLACEHOLDER);


    }

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

                }
            }
        },consultPane);
        dialog.setLocationRelativeTo(consultPane);
        dialog.pack();
        dialog.setVisible(true);
    }
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

    public void showNameFields(){
        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);
        dateNaissanceField.setVisible(false);
        dateLabel.setVisible(false);
        identifiantField.setVisible(false);
        identifiantLabel.setVisible(false);
    }




    public JPanel getConsultPane() {
        return consultPane;
    }



    public JButton getValiderButton() {
        return validerButton;
    }
}
