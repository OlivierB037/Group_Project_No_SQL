package fr.cnam.group;

import javax.swing.*;

public class TopMenu extends JMenuBar {


        private JMenu rollMenu;
        private JMenuItem returnToMain;
        private JMenuItem quit;

        public TopMenu() {
            super();
            rollMenu = new JMenu("fichier");

            returnToMain = new JMenuItem("menu principal");
            quit = new JMenuItem("quitter");

            rollMenu.setOpaque(true);
            rollMenu.add(returnToMain);
            rollMenu.add(quit);
            //rollMenu.setBackground(Color.CYAN);



            this.add(rollMenu);


        }

        public void mainMenu(){
            rollMenu.remove(returnToMain);
            rollMenu.updateUI();
        }
        public void quitMain(){
            rollMenu.add(returnToMain);
            rollMenu.updateUI();
        }


    public JMenuItem getQuit() {
        return quit;
    }

    public JMenu getRollMenu() {
            return rollMenu;
        }

        public JMenuItem getReturnToMain() {
            return returnToMain;
        }







}
