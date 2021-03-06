package ua.petproject.annotations;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;
import ua.petproject.util.DataBaseConnection;
import ua.petproject.util.PropertiesLoader;
import java.lang.reflect.Field;
import java.sql.*;
import static ua.petproject.annotations.DataTypeForFields.STRING;

/**
 * Class for tables creation if they don't exist.
 */
@Slf4j
public class CreateEntityTables {

    private static final String PACKAGES_TO_SCAN = PropertiesLoader.getProperty("component.scan");

    public static void init() {
        try(Connection connection = DataBaseConnection.getInstance().getConnection();
            ScanResult scanResult = new ClassGraph().whitelistPackages(PACKAGES_TO_SCAN).scan()){

            for(ClassInfo classInfo : scanResult.getAllClasses()){

                Class classElement = classInfo.loadClass();

                boolean isEntityPresent = classElement.isAnnotationPresent(Entity.class);

                if(isEntityPresent) {

                    String tableName = getTableName(classElement);

                    boolean isTableExistsInDatabase = checkIsTableExists(tableName);

                    if (!isTableExistsInDatabase) {

                        String sqlFieldsDeclaration = getSqlFieldsDeclaration(classElement.getDeclaredFields());

                        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " ( " + sqlFieldsDeclaration + " );";

                        Statement statement = connection.createStatement();
                        statement.executeUpdate(sqlQuery);
                        log.info(String.format("New table [%s] created in the database!", tableName));
                    }
                }

            }
        } catch (SQLException e) {
            log.error("SQLException", e);
        }
    }

    private static String getSqlFieldsDeclaration(Field[] declaredFields) {
        String sqlFieldsDeclaration = "";

        for (int i = 0; i < declaredFields.length; i++) {
            boolean isColumn = declaredFields[i].isAnnotationPresent(Column.class);

            if (isColumn) {
                String columnName = getColumnName(declaredFields[i]);

                String columnType = getColumnType(declaredFields[i]);

                String primaryKey = getPrimaryKey(declaredFields[i]);

                String comaDelimiterIfFieldNotFirstOne = getComaDelimiterIfFieldNotFirstOne(i);

                sqlFieldsDeclaration = sqlFieldsDeclaration + comaDelimiterIfFieldNotFirstOne + columnName + " "
                        + columnType + primaryKey;
            }
        }

        return sqlFieldsDeclaration;
    }

    private static String getComaDelimiterIfFieldNotFirstOne(int i) {
        if (i != 0) {
            return ", ";
        }
        return "";
    }

    private static String getColumnType(Field field) {
        boolean isAutoGeneratedValue = field.isAnnotationPresent(AutoGeneratedValue.class);
        String columnType;
        if (isAutoGeneratedValue) {
            columnType = "SERIAL";
        } else {
            String javaDataType = field.getType().getSimpleName().toUpperCase();
            columnType = DataTypeForFields.valueOf(javaDataType).getValue();
            if (columnType.equals(STRING.getValue())) {
                columnType = columnType + "(" + field.getAnnotation(Column.class).max() + ")";
            }
        }
        return columnType;
    }

    private static String getColumnName(Field field) {
        String columnName;
        if (!field.getAnnotation(Column.class).name().equals("")) {
            columnName = field.getAnnotation(Column.class).name();
        } else {
            columnName = field.getName().toLowerCase();
        }
        return columnName;
    }

    private static String getPrimaryKey(Field field) {
        boolean isId = field.isAnnotationPresent(Id.class);
        if (isId) {
            return " PRIMARY KEY";
        }
        return "";
    }

    private static boolean checkIsTableExists(String tableName) {
        try(Connection connection = DataBaseConnection.getInstance().getConnection()) {
            connection.createStatement().executeQuery("SELECT * FROM " + tableName + " LIMIT 1;");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static String getTableName(Class classElement) {
        boolean isTableNameSpecified = classElement.isAnnotationPresent(Table.class) && ((Table) classElement.getAnnotation(Table.class)).name() != null;

        if (isTableNameSpecified) {
            return  ((Table) classElement.getAnnotation(Table.class)).name();
        } else {
            return classElement.getSimpleName().toLowerCase();
        }
    }

}
