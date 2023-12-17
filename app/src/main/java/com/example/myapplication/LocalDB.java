package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

public final class LocalDB {
    private SQLiteDatabase localdb;
    private Table currentTable = Table.Null;
    public LocalDB()
    {
        localdb = SQLiteDatabase.openDatabase("//data/data/com.example.myapplication/database/localDB.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);;
    }
    public Table getCurrentTable() { return currentTable; }
    public void setCurrentTable(Table table)
    {
        currentTable = table;
        StringBuilder query = new StringBuilder();
        query.append("Create table if not exists ");
        query.append(currentTable.getTableName());
        query.append(" (");
        query.append(currentTable.getTableSchema());
        query.append(")");
        localdb.execSQL(query.toString());
    }
    public void setCurrentTable(String tableName) { currentTable = getTableByName(tableName); }
    public LinkedList<String[]> getValuesFromCurrentDb()
    {
        LinkedList<String[]> results = new LinkedList<String[]>();
        android.database.Cursor queryResult = localdb.rawQuery("Select * from " + currentTable.getTableName(), null);
        if (queryResult.getCount() > 0)
            while (queryResult.moveToNext())
            {
                results.add(new String[currentTable.getColumnsCount()]);
                for (int i = 0; i < currentTable.getColumnsCount(); i++)
                    results.getLast()[i] = queryResult.getString(i);
            }
        return results;
    }
    public boolean insertIntoCurrentTable(Object[] values) {
        if (currentTable.getTableName() == null)
            return false;
        String[] columns = currentTable.getColumnsNames();
        ContentValues dbValues = new ContentValues();
        for (int i = 0; i < columns.length; i++)
            dbValues.put(columns[i], values[i].toString());
        if (localdb.insert(Table.Persons.getTableName(), null, dbValues) == -1)
            return false;
        return true;
    }
    private Table getTableByName(String name)
    {
        for (Table table : Table.values())
            if (table.getTableName() == name)
                return table;
        return Table.Null;
    }
}
