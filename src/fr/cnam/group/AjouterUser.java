package fr.cnam.group;



import javax.swing.*;
import java.util.Arrays;

public class AjouterUser implements PlaceHolder {
    private JPanel PanelAjouterUser;
    private JTextPane ajouterUserTextPane;
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

    private enum Type {Particulier, Administrateur}
    private Type type;



    public AjouterUser() {

        type = Type.Particulier;
        setPlaceHolders();


        addedTypeBox.addActionListener(e -> {
            if (addedTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;
                setFieldsForParticulier();

            }
            else {
                type = Type.Administrateur;
                setFieldsForAdmins();

            }
        });
        validerButton.addActionListener(e -> {
            try {
                identifiantField.setText(identifiantField.getText().toLowerCase());
                String identifiant = identifiantField.getText();
                String nom = nomUserField.getText();
                String prenom = prenomUserField.getText();
                String date = dateNaissanceField.getText();
                char[] password = passwordField.getPassword();
                char[] passwordConfirm = passwordConfirmField.getPassword();
                if (!Arrays.equals(password, passwordConfirm)) {
                    JOptionPane.showMessageDialog(PanelAjouterUser,"mot de passe non confirmé","erreur mot de passe",JOptionPane.ERROR_MESSAGE);
                    System.out.println("mot de passe non confirmé.");

                }
                else{
                    if (DataHandler.isIdentifiantavailable(identifiant)) {

                        if (type == Type.Administrateur) {
                            if (new Administrateur(identifiant, password).ajouter()) {
                                clearFields();
                                JOptionPane.showMessageDialog(PanelAjouterUser, "Administrateur ajouté", "succès", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                throw new Exception("echec lors de l'ajout de l'administrateur");
                            }
                        }
                        else{
                            Particulier.TypeParticulier typeParticulier;
                            try {
                                typeParticulier  = Particulier.TypeParticulier.valueOf(typeParticulierBox.getSelectedItem().toString());
                            }catch (IllegalArgumentException ex){
                                throw new Exception("veuillez sélectionner un type de Particulier");
                            }
                            if (typeParticulierBox.getSelectedItem().toString().isEmpty()){
                                throw new Exception("veuillez sélectionner un type de Particulier");
                            }
                            else {
                                if (new Particulier(nom, prenom, date, Particulier.generateDateModification(),typeParticulier, identifiant, password).ajouter()) {
                                    clearFields();
                                    JOptionPane.showMessageDialog(PanelAjouterUser, "Particulier ajouté", "succès", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    } else {
                        throw new Exception("identifiant non disponible");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PanelAjouterUser, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }



    public void setPlaceHolders(){
        setPlaceHolder(identifiantField, IDENTIFIANT_PLACEHOLDER);
        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
        setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
        setPlaceHolder(passwordField,PASSWORD_PLACEHOLDER);
    }


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



    }

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
        typeParticulierBox.setVisible(true);
        typeParticulierLabel.setVisible(true);
        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);
    }




    public void clearFields(){
        identifiantField.setText("");
        nomUserField.setText("");
        prenomUserField.setText("");
        dateNaissanceField.setText("");
        passwordConfirmField.setText("");
        passwordField.setText("");
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

    public JPanel getPanelAjouterUser() {
        return PanelAjouterUser;
    }



}
