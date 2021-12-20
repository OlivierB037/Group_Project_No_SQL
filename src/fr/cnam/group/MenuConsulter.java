package fr.cnam.group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuConsulter {

    private JPanel consultPane;
    private JPanel userSearchPanel;
    private JTextField nomUserField;
    private JTextField prenomUserField;
    private JTextField identifiantField;
    private JFormattedTextField dateUserField;
    private JComboBox statutUserBox;

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


    private boolean firstQuery;


    public MenuConsulter() {


        if (DataHandler.currentUser instanceof Administrateur) {

            statutUserBox.setVisible(true);

        }
        else{
            statutUserBox.setSelectedItem("Particulier");
            statutUserBox.setVisible(false);

        }
        type = Type.Particulier;
        resultsTable.setVisible(false);
        resultsTable.setAutoCreateRowSorter(true);


        searchTypeButton.addActionListener(e -> {
            manageSearchPanel();
        });


        selectAllBox.addActionListener((listener) ->{
            if (selectAllBox.isSelected()){
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

        statutUserBox.addActionListener((e) -> {
            if (statutUserBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;

//                nomUserField.setVisible(true);
//                nomUserLabel.setVisible(true);
//                prenomUserField.setVisible(true);
//                prenomUserLabel.setVisible(true);
//                dateUserField.setVisible(true);
//                dateLabel.setVisible(true);
//                identifiantField.setVisible(false);
//                identifiantLabel.setVisible(false);
            }
            else {
                type = Type.Administrateur;
//                nomUserField.setVisible(false);
//                nomUserLabel.setVisible(false);
//                prenomUserField.setVisible(false);
//                prenomUserLabel.setVisible(false);
//                dateUserField.setVisible(false);
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
                            particulierSearchResult = new Particulier[DataHandler.annuaire.size()];
                            particulierSearchResult = DataHandler.annuaire.values().toArray(particulierSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(particulierSearchResult);
                            resultsTable.setModel(resultsTableModel);
                        } else {
                            accountSearchResult = new Administrateur[DataHandler.listeComptes.size()];
                            accountSearchResult = DataHandler.listeComptes.values().toArray(accountSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(accountSearchResult);
                            resultsTable.setModel(resultsTableModel);
                        }
                        resultsTable.setVisible(true);
                        consultPane.updateUI();
                    }
                    else {
                        System.out.println("test consulter");
                        if (!(dateUserField.getText().isEmpty()) && !(Particulier.isDateFormatOk(dateUserField.getText()))) {

                            throw new Exception("le format de la date doit être MM/DD/YYYY");
                        }


                        particulierSearchResult = Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateUserField.getText(), false);
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



    public void manageSearchPanel(){
        hideAllFields();
        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);
        SearchDialog dialog = new SearchDialog(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                switch (e.getID()){
                    case SearchDialog.NAME_SEARCH_ID -> { showNameFields();}
                    case SearchDialog.ID_SEARCH_ID -> {
                        hideAllFields();
                        identifiantField.setVisible(true);
                        identifiantLabel.setVisible(true);
                    }
                    case SearchDialog.DATE_SEARCH_ID -> {
                        hideAllFields();
                        dateLabel.setVisible(true);
                        dateUserField.setVisible(true);
                    }
                }
            }
        },consultPane);
        dialog.pack();
        dialog.setVisible(true);
    }
    public void hideAllFields(){
        nomUserField.setVisible(false);
        nomUserLabel.setVisible(false);
        prenomUserField.setVisible(false);
        prenomUserLabel.setVisible(false);
        dateUserField.setVisible(false);
        dateLabel.setVisible(false);
        identifiantField.setVisible(false);
        identifiantLabel.setVisible(false);
    }

    public void showNameFields(){
        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);
        dateUserField.setVisible(false);
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
