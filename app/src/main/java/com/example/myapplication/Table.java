package com.example.myapplication;

public enum Table {
    Null(){
        public String[] getColumnsNames()
        {
            return null;
        }
        @Override
        public String[] getColumnsTypes() {
            return null;
        }

        @Override
        public String getTableSchema() {
            return null;
        }

    },
    Persons("Persons", 2)
            {
                public String[] getColumnsNames()
                {
                    return new String[]{ "Name", "Age" };
                }
                @Override
                public String[] getColumnsTypes() {
                    return new String[]{ "TEXT", "INTEGER" };
                }

                @Override
                public String getTableSchema() {
                    return "Name TEXT, Age INTEGER";
                }
            };

    private String tableName = null;
    private int colsCount = 0;

    Table() {
    }

    Table(String name, int columns) {
        tableName = name;
        colsCount = columns;
    }


    public abstract String[] getColumnsNames();
    public abstract String[] getColumnsTypes();
    public abstract String getTableSchema();
    public String getTableName() {
        return tableName;
    }
    public int getColumnsCount() {
        return colsCount;
    }
}
