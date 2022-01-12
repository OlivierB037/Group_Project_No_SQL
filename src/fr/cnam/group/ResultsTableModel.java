/*
 * Nom de classe : ResultsTableModel
 *
 * Description   : contient le modèle à appliquer à la JTable permettant d'afficher les utilisateurs
 *                 (inspirée du code permettant d'afficher des résultats de requêtes MySQL proposée par Dominique Liard sur koor.fr)
 *
 * Auteurs       : Steven Besnard, Agnes Laurencon, Olivier Baylac, Benjamin Launay
 *
 * Version       : 1.0
 *
 * Date          : 09/01/2022
 *
 * Copyright     : CC-BY-SA
 */

package fr.cnam.group;

import fr.cnam.group.users.Administrateur;
import fr.cnam.group.users.Particulier;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;


public class ResultsTableModel extends AbstractTableModel {
    private final ArrayList<String> columnsNames = new ArrayList<>();
    private final ArrayList<String> columnsTypes = new ArrayList<>();
    private final ArrayList< ArrayList<String> > values = new ArrayList<>();

    public <T> ResultsTableModel ( T[] resultArray ) throws IllegalArgumentException {

        if (resultArray == null){ // vide le JTable

        }
        else {
            if (resultArray instanceof Particulier[]) {
                columnsNames.add("Identifiant");
                columnsNames.add("Nom");
                columnsNames.add("Prénom");
                columnsNames.add("Date de naissance");
                columnsNames.add("Adresse");
                columnsNames.add("Dernière modification");
                columnsNames.add("Type de compte");
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());

                Particulier[] partArray = (Particulier[]) Arrays.copyOf(resultArray,resultArray.length);

                for (Particulier p : partArray) {
                    if (p != null) {
                        ArrayList<String> line = new ArrayList<>();
                        line.add(p.getIdentifiant());
                        line.add(p.getNom());
                        line.add(p.getPrenom());
                        line.add(p.getDate_naissance());
                        line.add(p.getAdresse());
                        line.add(p.getDate_modification());
                        line.add(p.getTypeParticulier().toString());
                        values.add(line);
                    }
                }
            } else if (resultArray instanceof Administrateur[]){
                columnsNames.add("Identifiant");
                columnsTypes.add(String.class.getName());
//                System.out.println("resultTableModel : array type is Administrateur");
                Administrateur[] adminArray = (Administrateur[]) Arrays.copyOf(resultArray,resultArray.length);
                for (Administrateur p : adminArray) {
                    if (p != null) {
                        ArrayList<String> line = new ArrayList<>();
                        line.add(p.getIdentifiant());
                        values.add(line);
                    }
                }
            }
            else{
                throw new IllegalArgumentException("ResultsTableModel only takes Administarteur and Particuliers Classes");
            }
        }

    }

    @Override public Class<?> getColumnClass( int column ) {
        String type = this.columnsTypes.get( column );
        try {
            return Class.forName( type );
        } catch( Exception e ) {
            return String.class;
        }
    }

    @Override public String getColumnName(int i) {
        return columnsNames.get( i );
    }

    @Override public int getColumnCount() {
        return columnsNames.size();
    }

    @Override public int getRowCount() {
        return values.size();
    }

    @Override public Object getValueAt( int line, int column ) {
        return values.get( line ).get( column );
    }

}
