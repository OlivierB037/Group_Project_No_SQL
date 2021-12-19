package fr.cnam.group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MenuConsulter {

    private JPanel consultPane;
    private JPanel userSearchPanel;
    private JTextField nomUserField;
    private JTextField prenomUserField;
    private JTextField identifiantField;
    private JFormattedTextField dateUserField;
    private JComboBox statutUserBox;
    private JCheckBox andPrenomUser;
    private JCheckBox andNomUser;
    private JCheckBox andDateUser;
    private JTable resultsTable;
    private JButton validerButton;
    private JTextField thanksField;
    private JCheckBox selectAllBox;
    private JLabel nomUserLabel;
    private JLabel prenomUserLabel;
    private JLabel identifiantLabel;
    private JLabel dateLabel;

    private Particulier particulier;
    private Particulier[] particulierSearchResult;
    private Administrateur[] administrateurSearchResult;
    private enum Type {Particulier, Administrateur}
    private Type type;


    private boolean firstQuery;


    public MenuConsulter() {

        if (Main.currentUser != null) {

            statutUserBox.setVisible(true);

        }
        else{
            statutUserBox.setSelectedItem("Particulier");
            statutUserBox.setVisible(false);

        }
        type = Type.Particulier;
        resultsTable.setVisible(false);
        resultsTable.setAutoCreateRowSorter(true);

        andDateUser.setVisible(false);
        andPrenomUser.setVisible(false);


        selectAllBox.addActionListener((listener) ->{
            if (selectAllBox.isSelected()){
                userSearchPanel.setVisible(false);
                validerButton.setVisible(false);
                validerButton.doClick();
            }
            else{
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

                nomUserField.setVisible(true);
                nomUserLabel.setVisible(true);
                prenomUserField.setVisible(true);
                prenomUserLabel.setVisible(true);
                dateUserField.setVisible(true);
                dateLabel.setVisible(true);
                identifiantField.setVisible(false);
                identifiantLabel.setVisible(false);
            }
            else {
                type = Type.Administrateur;
                nomUserField.setVisible(false);
                nomUserLabel.setVisible(false);
                prenomUserField.setVisible(false);
                prenomUserLabel.setVisible(false);
                dateUserField.setVisible(false);
                dateLabel.setVisible(false);
                identifiantField.setVisible(true);
                identifiantLabel.setVisible(true);

            }
        });

        validerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (selectAllBox.isSelected()){
                        if (type == Type.Particulier) {
                            particulierSearchResult = new Particulier[Main.annuaire.size()];
                            particulierSearchResult = Main.annuaire.values().toArray(particulierSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(particulierSearchResult);
                            resultsTable.setModel(resultsTableModel);
                            resultsTable.setVisible(true);
                            consultPane.updateUI();
                        } else {
                            administrateurSearchResult = new Administrateur[Main.listeAdmins.size()];
                            administrateurSearchResult = Main.listeAdmins.values().toArray(administrateurSearchResult);
                            ResultsTableModel resultsTableModel = new ResultsTableModel(administrateurSearchResult);
                            resultsTable.setModel(resultsTableModel);
                            resultsTable.setVisible(true);
                            consultPane.updateUI();
                        }
                    }
                    else {
                        System.out.println("test consulter");
                        if (!(dateUserField.getText().isEmpty()) && !(Particulier.isDateFormatOk(dateUserField.getText()))) {

                            throw new Exception("le format de la date doit être MM/DD/YYYY");
                        }


                        particulierSearchResult = (andNomUser.isSelected()) ? Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateUserField.getText(), true) :
                                Particulier.trouverParticulier(nomUserField.getText(), prenomUserField.getText(), dateUserField.getText(), false);
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









    public void showThanks() {
        if (firstQuery) {
            Timer t = new Timer(5000, null);
            t.setRepeats(false);
            thanksField.setVisible(true);
            t.start();
            t.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    thanksField.setVisible(false);
                    firstQuery = false;
                }
            });
        }
    }



    public JPanel getConsultPane() {
        return consultPane;
    }



    public JButton getValiderButton() {
        return validerButton;
    }
}
