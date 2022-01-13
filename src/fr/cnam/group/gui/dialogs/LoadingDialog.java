/*
 * Nom de classe : LoadingDialog
 *
 * Description   : boite de dialogue affichée pendant le chargement des données contenues dans les fichiers
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


public class LoadingDialog extends JDialog {
    private JPanel contentPane;
    private JLabel chargementLabel;
    private String text = "Chargement des données en cours";
    private boolean stop;

    public LoadingDialog() {
        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        stop = false;
        new Thread(()->{
            try {
                animate(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // méthode récursive animant le texte lors du chargement
    private void animate(String a) throws InterruptedException {
        Thread.sleep(500);
        if (stop){
//            System.out.println("stopping animation");

        }
        else if (a.length() < 4){
//            System.out.println("a = "+ a);
            a += ".";
            chargementLabel.setText(text + a);
            animate(a);
        }
        else{
            a = ".";
            chargementLabel.setText(text + a);
            animate(a);
        }


    }
    public void stopAnimation(){
        stop = true;
    }

    @Override
    public void dispose() {
        stop = true;
        super.dispose();
    }
}
