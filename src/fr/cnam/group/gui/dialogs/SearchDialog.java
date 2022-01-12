/*
 * Nom de classe : SearchDialog
 *
 * Description   : boite de dialogue permettant la selection du type de recherche
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group.gui.dialogs;

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
    public static final String TYPE_SEARCH_COMMAND = "typeSearch";
    public static final int NAME_SEARCH_ID = -300;
    public static final int ID_SEARCH_ID = -290;
    public static final int DATE_SEARCH_ID = -280;
    public static final int TYPE_SEARCH_ID = -270;
    private JButton searchByBirthDateButton;
    private JButton searchByTypeButton;
    private ActionListener listener;


    public SearchDialog(ActionListener _listener, Component owner) {
        listener = _listener;
        this.setTitle("type de recherche");
        this.setSize(400, 400);
        this.setLocationRelativeTo(owner);


//        getRootPane().setDefaultButton(searchByIdButton);
        setContentPane(contentPane);
        setModal(true);

        /*
        * ces listeners modifient l'affichage des zones de saisies selon le type de recherche
        */
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
        searchByTypeButton.addActionListener(e -> {
            listener.actionPerformed(new ActionEvent(searchByTypeButton,TYPE_SEARCH_ID,TYPE_SEARCH_COMMAND));
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
        listener.actionPerformed(new ActionEvent(searchByNameButton, NAME_SEARCH_ID, NAME_SEARCH_COMMAND)); //en l'absence de choix, la recherche par nom est définie par défaut
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

    public JButton getSearchByBirthDateButton() {
        return searchByBirthDateButton;
    }

    public JButton getSearchByTypeButton() {
        return searchByTypeButton;
    }
}
