package ua.petproject.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.petproject.annotations.Column;
import ua.petproject.annotations.Table;
import ua.petproject.util.DataBaseConnection;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public interface CrudInterface<T, ID> {

    String CHARACTER = "CHARACTER";
    String BYTE = "BYTE";
    String STRING = "STRING";
    String BIG_DECIMAL = "BIG DECIMAL";
    String SHORT = "SHORT";
    String INTEGER = "INTEGER";
    String LONG = "LONG";
    String FLOAT = "FLOAT";
    String DOUBLE = "DOUBLE";
    String BYTE_ARRAY = "BYTE[]";
    String SQL_DATE = "SQL.DATE";
    String TIME = "TIME";
    String TIMESTAMP = "TIMESTAMP";
    String BOOLEAN = "BOOLEAN";
    String INPUT_STREAM = "INPUT_STREAM";
    String SQL_BLOB = "SQL_BLOB";
    String SQL_CLOB = "SQL_CLOB";
    Logger logger = LoggerFactory.getLogger(CrudInterface.class);

    DataBaseConnection dbConnection = new DataBaseConnection();
    Connection connection = dbConnection.getConnection();

    static void setObject(PreparedStatement st, int parameterIndex, Object parameterObj) throws SQLException {

        if (parameterObj == null) {
            st.setNull(parameterIndex, java.sql.Types.OTHER);
        } else {
            if (parameterObj instanceof Character) {
                logger.info(CHARACTER);
                st.setString(parameterIndex, String.valueOf(parameterObj));
            } else if (parameterObj instanceof Byte) {
                logger.info(BYTE);
                st.setInt(parameterIndex, ((Byte) parameterObj).intValue());
            } else if (parameterObj instanceof String) {
                logger.info(STRING);
                st.setString(parameterIndex, (String) parameterObj);
            } else if (parameterObj instanceof BigDecimal) {
                logger.info(BIG_DECIMAL);
                st.setBigDecimal(parameterIndex, (BigDecimal) parameterObj);
            } else if (parameterObj instanceof Short) {
                logger.info(SHORT);
                st.setShort(parameterIndex, ((Short) parameterObj).shortValue());
            } else if (parameterObj instanceof Integer) {
                logger.info(INTEGER);
                st.setInt(parameterIndex, ((Integer) parameterObj).intValue());
            } else if (parameterObj instanceof Long) {
                logger.info(LONG);
                st.setLong(parameterIndex, ((Long) parameterObj).longValue());
            } else if (parameterObj instanceof Float) {
                logger.info(FLOAT);
                st.setFloat(parameterIndex, ((Float) parameterObj).floatValue());
            } else if (parameterObj instanceof Double) {
                logger.info(DOUBLE);
                st.setDouble(parameterIndex, ((Double) parameterObj).doubleValue());
            } else if (parameterObj instanceof byte[]) {
                logger.info(BYTE_ARRAY);
                st.setBytes(parameterIndex, (byte[]) parameterObj);
            } else if (parameterObj instanceof java.sql.Date) {
                logger.info(SQL_DATE);
                st.setDate(parameterIndex, (java.sql.Date) parameterObj);
            } else if (parameterObj instanceof Time) {
                logger.info(TIME);
                st.setTime(parameterIndex, (Time) parameterObj);
            } else if (parameterObj instanceof Timestamp) {
                logger.info(TIMESTAMP);
                st.setTimestamp(parameterIndex, (Timestamp) parameterObj);
            } else if (parameterObj instanceof Boolean) {
                logger.info(BOOLEAN);
                st.setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
            } else if (parameterObj instanceof InputStream) {
                logger.info(INPUT_STREAM);
                st.setBinaryStream(parameterIndex, (InputStream) parameterObj, -1);
            } else if (parameterObj instanceof java.sql.Blob) {
                logger.info(SQL_BLOB);
                st.setBlob(parameterIndex, (java.sql.Blob) parameterObj);
            } else if (parameterObj instanceof java.sql.Clob) {
                logger.info(SQL_CLOB);
                st.setClob(parameterIndex, (java.sql.Clob) parameterObj);
            }
        }
    }


    default <S extends T> S save(S entity) throws IllegalAccessException {

        // get table
        System.out.println("DB table : " + entity.getClass().getAnnotation(Table.class).name());

        // get columns from database of the Entity
        List<String> columnNames = new ArrayList<>();

        int counter = 0;
        for (Field field1 : entity.getClass().getDeclaredFields()) {
            Column column = field1.getAnnotation(Column.class);
            if (column != null) {
                if (counter != 0) {
                    columnNames.add(column.name());
                }
            }
            counter++;
        }
        System.out.println("Columns : " + columnNames);

        // values to insert
        List<String> valuesToInsert = new ArrayList<>();
        Field[] field = entity.getClass().getDeclaredFields();
        for (Field field1 : field) {
            field1.setAccessible(true);
            try {
                Object value = field1.get(entity);
                valuesToInsert.add(value.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        valuesToInsert.remove(0);
        System.out.println("Values to insert : " + valuesToInsert);

        // create INSERT query
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ");
        queryBuilder.append(entity.getClass().getAnnotation(Table.class).name());
        queryBuilder.append(" (");

        // add column names
        int columnsCounter = 1;
        for (String column : columnNames) {
            queryBuilder.append(column);
            if (columnsCounter == columnNames.size()) {
                queryBuilder.append(")");
            } else {
                queryBuilder.append(" ,");
            }
            columnsCounter++;
        }
        queryBuilder.append(" VALUES (");

        for (int i = 0; i < valuesToInsert.size(); i++) {
            if (i != valuesToInsert.size() - 1) {
                queryBuilder.append(" ?,");
            } else {
                queryBuilder.append(" ? );");
            }
        }

        System.out.println("QUERY to DB : " + queryBuilder.toString());
        try (PreparedStatement st = connection
                .prepareStatement(queryBuilder.toString())) {

            for (int i = 1; i < field.length; i++) {
                setObject(st, i, field[i].get(entity));
            }
            st.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("You can not add new entity.", ex);
        }
        return entity;

    }
}


//    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
//
//    Optional<T> findById(ID id);
//
//    boolean existsById(ID id);
//
//    Iterable<T> findAll();
//
//    Iterable<T> findAllById(Iterable<ID> ids);
//
//    long count();
//
//    void deleteById(ID id);
//
//    void delete(T entity);
//
//    void deleteAll(Iterable<? extends T> entities);
//
//    void deleteAll();


