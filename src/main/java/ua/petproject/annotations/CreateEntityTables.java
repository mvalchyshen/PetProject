package ua.petproject.annotations;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import ua.petproject.util.DataBaseConnection;
import ua.petproject.util.PropertiesLoader;

import java.lang.reflect.Field;
import java.sql.*;

public class CreateEntityTables {

    private static final String PACKGES_TO_SCAN = PropertiesLoader.getProperty("component.scan");

    public static void main(String[] args) {
        // connect to DB to execute query
//        try(Connection connection = DataBaseConnection.getInstance().getConnection()) {



        try(Connection connection = DataBaseConnection.getInstance().getConnection();
            ScanResult scanResult = new ClassGraph().whitelistPackages(PACKGES_TO_SCAN).scan()){
            for(ClassInfo classInfo : scanResult.getAllClasses()){ // begining of class interation

                // get annotated class
                Class classElement = classInfo.loadClass();

                //declare request parts
                String sqlQueryBegining = "CREATE TABLE IF NOT EXISTS";
                String tableName = "";
                String sqlFieldsDeclaration = "";

                // check if there are @Entity and Table annotations
                boolean isEntityPresent = classElement.isAnnotationPresent(Entity.class);
                boolean isTablePresent = classElement.isAnnotationPresent(Table.class);

                if(isEntityPresent){

                    // table name setting: check if table name is specified otherwise use class name
                    if(isTablePresent && ((Table) classElement.getAnnotation(Table.class)).name() != null){
                        tableName = ((Table) classElement.getAnnotation(Table.class)).name();
                    } else {
                        tableName = classElement.getSimpleName().toLowerCase();
                    }

                    // table fields setting
                    Field[] declaredFields = classElement.getDeclaredFields();

                    //iterating through fields to form the column names, types and other parameters
                    for(int i = 0; i < declaredFields.length; i++){

                        // checking on field annotations
                        boolean isColumn = declaredFields[i].isAnnotationPresent(Column.class);
                        boolean isAutoGeneratedValue = declaredFields[i].isAnnotationPresent(AutoGeneratedValue.class);
                        boolean isId = declaredFields[i].isAnnotationPresent(Id.class);

                        String columnName = "";
                        String columnType = "";
                        String autoGenerationFlag = "";
                        String primaryKey = "";
                        if(isId){
                            primaryKey = " PRIMARY KEY";
                        }
                        if(isColumn){
                            if(!declaredFields[i].getAnnotation(Column.class).name().equals("")){
                                columnName = declaredFields[i].getAnnotation(Column.class).name();
                            } else {
                                columnName = declaredFields[i].getName().toLowerCase();
                            }
                            if(isAutoGeneratedValue){
                                columnType = "SERIAL";
                            } else {
                                columnType = toSQLType(declaredFields[i].getType().getSimpleName());
                            }


                            // adding max size for varchar type
                            if(columnType.equals("VARCHAR")){
                                columnType = columnType + "(" + declaredFields[i].getAnnotation(Column.class).max() + ")";
                            }

                            // adding field to fields declaration string
                            if(i == 0){
                                sqlFieldsDeclaration = sqlFieldsDeclaration + columnName + " "
                                        + columnType + primaryKey;
                            } else {
                                sqlFieldsDeclaration = sqlFieldsDeclaration + ", " + columnName + " " + columnType + autoGenerationFlag + primaryKey;
                            }
                        }
                    }

                    String sqlQuery = sqlQueryBegining + " " + tableName + " ( " + sqlFieldsDeclaration + " );";
                    System.out.println(sqlQuery);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(sqlQuery);
                    System.out.println("new table created");
                }


            } // ending of class iteration
        } catch (SQLException e) { // end try-with-resources for package scanning
            e.printStackTrace();
        } // end catch section for try-with-resources of package scanning


    } // ending of main

    private static String toSQLType(String initialDataType) {

        if(initialDataType.equals("String") || initialDataType.equals("char") || initialDataType.equals("Character")){
            return "VARCHAR";
        } else if(initialDataType.equals("boolean") || initialDataType.equals("Boolean")){
            return "BIT";
        } else if(initialDataType.equals("int") || initialDataType.equals("Integer")){
            return "INT";
        } else if(initialDataType.equals("long") || initialDataType.equals("Long") || initialDataType.equals("BigInt")){
            return "INT";
        } else if(initialDataType.equals("short") || initialDataType.equals("Short")){
            return "SMALLINT";
        } else if(initialDataType.equals("Float") || initialDataType.equals("float")  || initialDataType.equals("double")
                || initialDataType.equals("Double")){
            return "REAL";
        } else if(initialDataType.equals("byte") || initialDataType.equals("Byte")){
            return "BINARY";
        } else {
            return initialDataType;
        }
    }
}
