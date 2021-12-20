package fr.cnam.group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchDialog extends JDialog {
    private JPanel contentPane;
    private JButton searchByNameButton;
    private JButton searchByIdButton;
    public static final String NAME_SEARCH_COMMAND = "nameSearch";
    public static final String ID_SEARCH_COMMAND = "IdSearch";
    public static final String DATE_SEARCH_COMMAND = "dateSearch";
    public static final int NAME_SEARCH_ID = -300;
    public static final int ID_SEARCH_ID = -290;
    public static final int DATE_SEARCH_ID = -280;
    private JButton searchByBirthDateButton;

    //    enum SearchType {
//        searchByName("Rechercher par nom"),
//        searchById("Rechercher par identifiant");
//
//        public final String label;
//
//        private SearchType(String label) {
//            this.label = label;
//        }
//    }
    public SearchDialog(ActionListener listener, Component owner) {
        this.setTitle("type de recherche");
        this.setSize(400, 400);
        this.setLocationRelativeTo(owner);

        setModal(true);
//        getRootPane().setDefaultButton(searchByIdButton);
        setContentPane(contentPane);
        setModal(true);

        searchByNameButton.addActionListener(e -> {
            listener.actionPerformed(new ActionEvent(searchByNameButton, NAME_SEARCH_ID, NAME_SEARCH_COMMAND));
            dispose();
        });
        searchByIdButton.addActionListener(e ->{
            listener.actionPerformed(new ActionEvent(searchByIdButton,ID_SEARCH_ID,ID_SEARCH_COMMAND));
            dispose();
        });
        searchByBirthDateButton.addActionListener(e ->{
            listener.actionPerformed(new ActionEvent(searchByBirthDateButton,DATE_SEARCH_ID,DATE_SEARCH_COMMAND));
            dispose();
        });


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }



    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public JButton getSearchByNameButton() {
        return searchByNameButton;
    }

    public JButton getSearchByIdButton() {
        return searchByIdButton;
    }
}
