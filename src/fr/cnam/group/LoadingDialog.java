package fr.cnam.group;

import javax.swing.*;
import java.awt.event.*;

public class LoadingDialog extends JDialog {
    private JPanel contentPane;
    private JLabel chargementDesDonnéesEnLabel;
    private Timer timer;
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
    private void animate(String a) throws InterruptedException {
        Thread.sleep(500);
        if (stop){
            System.out.println("stopping animation");
            return;
        }
        else if (a.length() < 4){
            System.out.println("a = "+ a);
            a += ".";
            chargementDesDonnéesEnLabel.setText(text + a);
            animate(a);
        }
        else{
            a = ".";
            chargementDesDonnéesEnLabel.setText(text + a);
            animate(a);
        }


    }
    private void stopAnimation(){
        stop = true;
    }

    @Override
    public void dispose() {
        stop = true;
        super.dispose();
    }
}
