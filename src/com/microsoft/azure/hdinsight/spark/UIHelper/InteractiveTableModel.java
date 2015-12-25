package com.microsoft.azure.hdinsight.spark.UIHelper;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by guizha on 8/25/2015.
 */
public class InteractiveTableModel extends AbstractTableModel{

    public static final int KEY_INDEX = 0 ;
    public static final int VALUE_INDEX = 1;
    public static final int HIDDEN_INDEX = 2;

    private String[] columnNames;
    private ArrayList<ConfigurationKeyValueRecord> dataRecords;

    public InteractiveTableModel(String[] columnNames) {
        this.columnNames = columnNames;
        this.dataRecords = new ArrayList<ConfigurationKeyValueRecord>();
    }

    @Override
    public int getRowCount() {
        return this.dataRecords.size();
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex > getRowCount()-1 || columnIndex > getColumnCount() -1) {
            return null;
        }

       ConfigurationKeyValueRecord record = dataRecords.get(rowIndex);
        switch(columnIndex){
            case KEY_INDEX:
                return record.getKey();
            case VALUE_INDEX:
                return record.getValue();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case KEY_INDEX:
            case VALUE_INDEX:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
       if(columnIndex == HIDDEN_INDEX){
           return false;
       }

        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(rowIndex > getRowCount()-1 || columnIndex > getColumnCount() -1) {
            return;
        }

        ConfigurationKeyValueRecord record = dataRecords.get(rowIndex);
        switch(columnIndex){
            case KEY_INDEX:
                record.setKey((String)aValue);
                break;
            case VALUE_INDEX:
                record.setValue(aValue);
                break;
            default:
                return;
        }
    }

    public boolean hasEmptyRow(){
        int rowCount = getRowCount();
        if(rowCount == 0){
            return false;
        }

        ConfigurationKeyValueRecord record = dataRecords.get(getRowCount() -1);
        if(record.getKey().trim().equals("") && record.getValue().toString().trim().equals("")){
            return true;
        }

        return false;
    }

    public void addEmptyRow(){
        dataRecords.add(new ConfigurationKeyValueRecord());
        int rowCount = getRowCount();
        fireTableRowsInserted(rowCount-1, rowCount-1);
    }
}
