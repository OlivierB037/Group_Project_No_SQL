/*
 * Nom de classe : MenuAjouter
 *
 * Description   : Pilote le menu permettant d'ajouter des utilisateurs.
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay.
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group.gui.menus;

import fr.cnam.group.DataHandler;
import fr.cnam.group.exceptions.DataException;
import fr.cnam.group.exceptions.UserDataInputException;
import fr.cnam.group.gui.PlaceHolder;
import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;
import javax.swing.*;
import java.util.Arrays;

public class MenuAjouter implements PlaceHolder {
    private JPanel menuAjouterPanel;
    private JLabel menuAjouterTitle;
    private JTextField nomUserField;
    private JButton validerButton;
    private JLabel nomUserLabel;
    private JTextField prenomUserField;
    private JLabel prenomUserLabel;
    private JTextField dateNaissanceField;
    private JLabel dateLabel;
    private JComboBox addedTypeBox;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JTextField identifiantField;
    private JLabel identifiantLabel;
    private JPasswordField passwordConfirmField;
    private JLabel passwordConfirmLabel;
    private JComboBox typeParticulierBox;
    private JLabel typeParticulierLabel;
    private JTextField streetTextField;
    private JLabel streetLabel;
    private JTextField codePostalField;
    private JLabel codePostalLabel;
    private JTextField villeField;
    private JLabel villeLabel;

    private enum Type {Particulier, Administrateur}
    private Type type;



    public MenuAjouter() {
        type = Type.Particulier;
        setPlaceHolders();
        /*
         * listener du choix du type d'utilisateur ajouté
         */
        addedTypeBox.addActionListener(e -> {
            if (addedTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;
                menuAjouterTitle.setText("Ajouter un utilisateur");
                setFieldsForParticulier();

            }
            else {
                type = Type.Administrateur;
                menuAjouterTitle.setText("Ajouter un administrateur");
                setFieldsForAdmins();

            }
        });

        /*
         * listener du bouton valider
         */
        validerButton.addActionListener(e -> {
            try {
                identifiantField.setText(identifiantField.getText().toLowerCase());
                String identifiant = identifiantField.getText();
                String nom = nomUserField.getText();
                String prenom = prenomUserField.getText();
                String date = dateNaissanceField.getText();
                String rue = streetTextField.getText();
                String postalCode = codePostalField.getText();
                String ville = villeField.getText();
                char[] password = passwordField.getPassword();
                char[] passwordConfirm = passwordConfirmField.getPassword();
                if (!confirmPassword(password, passwordConfirm)) {
                    throw new UserDataInputException("mot de passe non confirmé.");
                }
                else{
                    if (DataHandler.isIdentifiantavailable(identifiant)) {
                        System.out.println("identifiant " + identifiant+ " is available");

                        if (type == Type.Administrateur) {
                            Administrateur.checkIdentifiantFormat(identifiant);
                            Administrateur.checkPasswordFormat(password);

                            if (new Administrateur(identifiant, password).ajouter()) {
                                clearFields();
                                JOptionPane.showMessageDialog(menuAjouterPanel, "Administrateur ajouté", "succès", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                throw new DataException("echec lors de l'ajout de l'administrateur");
                            }
                        }
                        else{
                            Particulier.TypeParticulier typeParticulier;
                            try {
                                typeParticulier  = Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString());
                            }catch (IllegalArgumentException ex){
                                throw new UserDataInputException("veuillez sélectionner un type de Particulier");
                            }
                            if (typeParticulierBox.getSelectedItem().toString().isEmpty()){
                                throw new UserDataInputException("veuillez sélectionner un type de Particulier");
                            }
                            else {
                                Particulier.checkIdentifiantFormat(identifiant);
                                Particulier.checkNameFormat(nom);
                                Particulier.checkNameFormat(prenom);
                                Particulier.checkDateFormat(date);
                                Particulier.checkPasswordFormat(password);
                                Particulier.checkAdresseFormat(rue,postalCode,ville);

                                if (new Particulier(nom, prenom, date,Particulier.formatAdresse(rue,postalCode,ville), Particulier.generateDateModification(),typeParticulier, identifiant, password).ajouter()) {
                                    clearFields();
                                    setPlaceHolders();
                                    JOptionPane.showMessageDialog(menuAjouterPanel, "Particulier ajouté", "succès", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    } else {
                        throw new DataException("identifiant non disponible");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(menuAjouterPanel, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
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
        setPlaceHolder(passwordField,PASSWORD_PLACEHOLDER);
        setPlaceHolder(streetTextField, STREET_PLACEHOLDER);
        setPlaceHolder(codePostalField,POSTAL_CODE_PLACEHOLDER);
        setPlaceHolder(villeField,CITY_PLACEHOLDER);
    }

    /*
     * affiche les éléments graphiques nécéssaires à l'ajout d'un administrateur
     */
    public void setFieldsForAdmins(){

        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);
        passwordField.setVisible(true);
        passwordLabel.setVisible(true);
        passwordConfirmField.setVisible(true);
        passwordConfirmLabel.setVisible(true);
        typeParticulierBox.setVisible(false);
        typeParticulierLabel.setVisible(false);
        nomUserField.setVisible(false);
        nomUserLabel.setVisible(false);
        prenomUserField.setVisible(false);
        prenomUserLabel.setVisible(false);
        dateNaissanceField.setVisible(false);
        dateLabel.setVisible(false);
        streetTextField.setVisible(false);
        streetLabel.setVisible(false);
        codePostalField.setVisible(false);
        codePostalLabel.setVisible(false);
        villeField.setVisible(false);
        villeLabel.setVisible(false);
    }

    /*
     * affiche les éléments graphiques nécéssaires à l'ajout d'un particulier
     */
    public void setFieldsForParticulier(){
        passwordField.setVisible(true);
        passwordLabel.setVisible(true);
        passwordConfirmField.setVisible(true);
        passwordConfirmLabel.setVisible(true);
        nomUserField.setVisible(true);
        nomUserLabel.setVisible(true);
        prenomUserField.setVisible(true);
        prenomUserLabel.setVisible(true);
        dateNaissanceField.setVisible(true);
        dateLabel.setVisible(true);
        streetTextField.setVisible(true);
        streetLabel.setVisible(true);
        codePostalField.setVisible(true);
        codePostalLabel.setVisible(true);
        villeField.setVisible(true);
        villeLabel.setVisible(true);
        typeParticulierBox.setVisible(true);
        typeParticulierLabel.setVisible(true);
        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);
    }

    /*
     * vide les zone de saisie de texte
     */
    public void clearFields(){
        identifiantField.setText("");
        nomUserField.setText("");
        prenomUserField.setText("");
        dateNaissanceField.setText("");
        passwordConfirmField.setText("");
        passwordField.setText("");
        streetTextField.setText("");
        codePostalField.setText("");
        villeField.setText("");
    }
    /*
     * vérifie la confirmation du mot de passe
     */
    public boolean confirmPassword(char[] password, char[] passwordConfirm){

        if (!Arrays.equals(password, passwordConfirm)) {
            passwordField.setText("");
            passwordConfirmField.setText("");
            return false;
        }
        else {
            return true;
        }
    }

    public JTextField getNomUserField() {
        return nomUserField;
    }

    public JButton getValiderButton() {
        return validerButton;
    }

    public JTextField getPrenomUserField() {
        return prenomUserField;
    }

    public JPanel getMenuAjouterPanel() {
        return menuAjouterPanel;
    }
}
