package fr.cnam.group;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private enum Type {Particulier, Administrateur}
    private Type type;







    public AjouterUser() {

        type = Type.Particulier;
        setPlaceHolders();
//        identifiantField.setVisible(false);
//        identifiantLabel.setVisible(false);
//        passwordField.setVisible(false);
//        passwordLabel.setVisible(false);
//        passwordConfirmField.setVisible(false);
//        passwordConfirmLabel.setVisible(false);

        addedTypeBox.addActionListener(e -> {
            if (addedTypeBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;

//                passwordField.setVisible(true);
//                passwordLabel.setVisible(true);
//                passwordConfirmField.setVisible(true);
//                passwordConfirmLabel.setVisible(true);
//                nomUserField.setVisible(true);
//                nomUserLabel.setVisible(true);
//                prenomUserField.setVisible(true);
//                prenomUserLabel.setVisible(true);
//                dateNaissanceField.setVisible(true);
//                dateLabel.setVisible(true);
//                identifiantField.setVisible(false);
//                identifiantLabel.setVisible(false);
            }
            else {
                type = Type.Administrateur;
                setFieldsForAdmins();
//                passwordField.setVisible(true);
//                passwordLabel.setVisible(true);
//                passwordConfirmField.setVisible(true);
//                passwordConfirmLabel.setVisible(true);
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
                                    if (new Particulier(nom,prenom,date,Particulier.generateDateModification(),identifiant,password).ajouter()){
                                        clearFields();
                                        JOptionPane.showMessageDialog(PanelAjouterUser, "Particulier ajouté", "succès", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                            } else {
                                throw new Exception("identifiant non disponible");
                            }
                        }




                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PanelAjouterUser, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setAllFields(boolean b){
        nomUserField.setVisible(b);
        nomUserLabel.setVisible(b);
        prenomUserField.setVisible(b);
        prenomUserLabel.setVisible(b);
        dateNaissanceField.setVisible(b);
        dateLabel.setVisible(b);
        identifiantField.setVisible(b);
        identifiantLabel.setVisible(b);

        passwordField.setVisible(b);
        passwordLabel.setVisible(b);
        passwordConfirmField.setVisible(b);
        passwordConfirmLabel.setVisible(b);
    }

    public void setPlaceHolders(){
        setPlaceHolder(identifiantField, IDENTIFIANT_PLACEHOLDER);
        setPlaceHolder(nomUserField,NOM_PLACEHOLDER);
        setPlaceHolder(prenomUserField,PRENOM_PLACEHOLDER);
        setPlaceHolder(dateNaissanceField,DATE_PLACEHOLDER);
//        setPlaceHolder(newPasswordField,PASSWORD_PLACEHOLDER);
//        setPlaceHolder(newPasswordConfirmField,PASSWORD_PLACEHOLDER);

        setPlaceHolder(passwordField,PASSWORD_PLACEHOLDER);
    }


    public void setFieldsForAdmins(){


        identifiantField.setVisible(true);
        identifiantLabel.setVisible(true);

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
