/*
Name: Gustavo Juscamayta
Course: CNT 4714 Spring 2026
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 15, 2026

Class: ResultSetTableModel
*/

import javax.swing.table.AbstractTableModel;
import java.sql.*;

public class ResultSetTableModel extends AbstractTableModel {

    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;

    public ResultSetTableModel(ResultSet rs) throws SQLException {

        resultSet = rs;
        metaData = resultSet.getMetaData();

        resultSet.last();
        numberOfRows = resultSet.getRow();
        resultSet.beforeFirst();
    }

    public int getColumnCount() {

        try {
            return metaData.getColumnCount();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getRowCount() {
        return numberOfRows;
    }

    public String getColumnName(int column) {

        try {
            return metaData.getColumnName(column + 1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public Object getValueAt(int row, int column) {

        try {

            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}