/*
 * Nom de classe : MenuBar
 *
 * Description   : classe de la barre d'outil
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group.gui;

import javax.swing.*;

public class MenuBar extends JMenuBar {

        private JMenu rollMenu;
        private JMenuItem returnToMain;
        private JMenuItem quit;

        public MenuBar() {
            super();
            rollMenu = new JMenu("fichier");
            returnToMain = new JMenuItem("menu principal");
            quit = new JMenuItem("quitter");
            rollMenu.setOpaque(true);
            rollMenu.add(returnToMain);
            rollMenu.add(quit);
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
