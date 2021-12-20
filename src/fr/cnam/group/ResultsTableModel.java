package fr.cnam.group;

import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.Arrays;


public class ResultsTableModel <T> extends AbstractTableModel {
    private final ArrayList<String> columnsNames = new ArrayList<>();
    private final ArrayList<String> columnsTypes = new ArrayList<>();


    private final ArrayList< ArrayList<String> > values = new ArrayList<>();

    public  ResultsTableModel ( T[] resultArray ) throws IllegalArgumentException {

        if (resultArray == null){

        }
        else {




            if (resultArray instanceof Particulier[]) {
                columnsNames.add("Identifiant");
                columnsNames.add("Nom");
                columnsNames.add("Prénom");
                columnsNames.add("Date de naissance");
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());
                columnsTypes.add(String.class.getName());

                System.out.println("resultTableModel : array type is particulier");
                Particulier[] partArray = (Particulier[]) Arrays.copyOf(resultArray,resultArray.length);

                for (Particulier p : partArray) {
                    if (p != null) {
                        ArrayList<String> line = new ArrayList<>();
                        line.add(p.getIdentifiant());
                        line.add(p.getNom());
                        line.add(p.getPrenom());
                        line.add(p.getDate_naissance());
                        values.add(line);
                    }
                }
            } else if (resultArray instanceof Account[]){
                columnsNames.add("Identifiant");
                columnsTypes.add(String.class.getName());
                System.out.println("resultTableModel : array type is Administrateur");
                Account[] adminArray = (Account[]) Arrays.copyOf(resultArray,resultArray.length);
                for (Account p : adminArray) {
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
    public String getParticulierIdAt(int line){
        return values.get(line).get(0);
    }
}
