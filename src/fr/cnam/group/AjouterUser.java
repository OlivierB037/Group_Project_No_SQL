package fr.cnam.group;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Arrays;

public class AjouterUser {
    private JPanel PanelAjouterUser;
    private JTextPane ajouterUserTextPane;
    private JTextField nomUserField;
    private JButton validerButton;
    private JLabel nomUserLabel;
    private JTextField prenomUserField;
    private JLabel prenomUserLabel;

    private JFormattedTextField dateUserField;
    private JLabel dateLabel;
    private JComboBox statutUserBox;
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
        identifiantField.setVisible(false);
        identifiantLabel.setVisible(false);
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);
        passwordConfirmField.setVisible(false);
        passwordConfirmLabel.setVisible(false);

        statutUserBox.addActionListener(e -> {
            if (statutUserBox.getSelectedItem().toString().equals(Type.Particulier.toString())){
                type = Type.Particulier;
                passwordField.setVisible(false);
                passwordLabel.setVisible(false);
                passwordConfirmField.setVisible(false);
                passwordConfirmLabel.setVisible(false);
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
                passwordField.setVisible(true);
                passwordLabel.setVisible(true);
                passwordConfirmField.setVisible(true);
                passwordConfirmLabel.setVisible(true);
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
                    if (type == Type.Particulier) {
                        String nom = nomUserField.getText();
                        String prenom = prenomUserField.getText();
                        String date = dateUserField.getText();
                        if (Particulier.isDateFormatOk(date)) {
                            if(Particulier.isNameFormatOk(nom) && Particulier.isNameFormatOk(prenom)){
                                if(Particulier.trouverParticulier(nom,prenom,date,true) == null){
                                    if (Particulier.ajouterParticulier(nom, prenom, date) != null){
                                        clearFields();
                                        JOptionPane.showMessageDialog(PanelAjouterUser, "Particulier ajouté","succès",JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                                else{
                                    throw new Exception("ce particulier' existe déja");
                                }
                            }
                            else{ throw new Exception("données incorrectes");}
                        }
                        else {
                        throw new Exception("le format de la date doit être MM/DD/YYYY");
                        }
                    } else {
                        String identifiant = identifiantField.getText();
                        char[] password = passwordField.getPassword();
                        char[] passwordConfirm = passwordConfirmField.getPassword();
                        if (!Arrays.equals(password, passwordConfirm)) {
                            JOptionPane.showMessageDialog(PanelAjouterUser,"mot de passe non confirmé","erreur mot de passe",JOptionPane.ERROR_MESSAGE);
                            System.out.println("mot de passe non confirmé.");

                        }
                        else{
                            if (Main.isIdentifiantavailable(identifiant)) {
                                if (Administrateur.ajouterAdministrateur(new Administrateur(identifiant, password))) {
                                    clearFields();
                                    JOptionPane.showMessageDialog(PanelAjouterUser, "Administrateur ajouté","succès",JOptionPane.INFORMATION_MESSAGE);
                                }else{
                                    throw new Exception("echec lors de l'ajout de l'administrateur");
                                }
                            } else {
                                throw new Exception("identifiant non disponible");
                            }
                        }



                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PanelAjouterUser, ex.getMessage(), "erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }





    public void clearFields(){
        identifiantField.setText("");
        nomUserField.setText("");
        prenomUserField.setText("");
        dateUserField.setText("");
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
