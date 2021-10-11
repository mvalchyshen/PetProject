package ua.petproject.repositories;

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

    DataBaseConnection dbConnection = new DataBaseConnection();
    Connection connection = dbConnection.getConnection();

    static void setObject(PreparedStatement st, int parameterIndex, Object parameterObj) throws SQLException {

        if (parameterObj == null) {
            st.setNull(parameterIndex, Types.OTHER);
        } else {
            if (parameterObj instanceof Character) {
                System.out.println("CHARACTER");
                st.setString(parameterIndex, String.valueOf(parameterObj));
            } else if (parameterObj instanceof Byte) {
                System.out.println("BYTE");
                st.setInt(parameterIndex, ((Byte) parameterObj).intValue());
            } else if (parameterObj instanceof String) {
                System.out.println("STRING");
                st.setString(parameterIndex, (String) parameterObj);
            } else if (parameterObj instanceof BigDecimal) {
                System.out.println("BYTE");
                st.setBigDecimal(parameterIndex, (BigDecimal) parameterObj);
            } else if (parameterObj instanceof Short) {
                st.setShort(parameterIndex, ((Short) parameterObj).shortValue());
            } else if (parameterObj instanceof Integer) {
                System.out.println("INTEGER");
                st.setInt(parameterIndex, ((Integer) parameterObj).intValue());
            } else if (parameterObj instanceof Long) {
                st.setLong(parameterIndex, ((Long) parameterObj).longValue());
            } else if (parameterObj instanceof Float) {
                st.setFloat(parameterIndex, ((Float) parameterObj).floatValue());
            } else if (parameterObj instanceof Double) {
                st.setDouble(parameterIndex, ((Double) parameterObj).doubleValue());
            } else if (parameterObj instanceof byte[]) {
                st.setBytes(parameterIndex, (byte[]) parameterObj);
            } else if (parameterObj instanceof Date) {
                st.setDate(parameterIndex, (Date) parameterObj);
            } else if (parameterObj instanceof Time) {
                st.setTime(parameterIndex, (Time) parameterObj);
            } else if (parameterObj instanceof Timestamp) {
                st.setTimestamp(parameterIndex, (Timestamp) parameterObj);
            } else if (parameterObj instanceof Boolean) {
                st.setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
            } else if (parameterObj instanceof InputStream) {
                st.setBinaryStream(parameterIndex, (InputStream) parameterObj, -1);
            } else if (parameterObj instanceof Blob) {
                st.setBlob(parameterIndex, (Blob) parameterObj);
            } else if (parameterObj instanceof Clob) {
                st.setClob(parameterIndex, (Clob) parameterObj);
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


