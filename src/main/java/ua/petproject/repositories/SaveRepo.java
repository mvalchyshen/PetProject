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


public interface SaveRepo<T, ID> {

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
    Logger logger = LoggerFactory.getLogger(SaveRepo.class);

    Connection connection = DataBaseConnection.getInstance().getConnection();

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

    static <S> String createQuery(S entity, Field[] fields, List<String> columnNames, List<String> valuesToInsert) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ");
        queryBuilder.append(entity.getClass().getAnnotation(Table.class).name());
        queryBuilder.append(" (");
        addColumnNames(fields, columnNames, queryBuilder);
        queryBuilder.append(" VALUES (");
        addValuesToInsert(valuesToInsert, queryBuilder, entity, fields);
        logger.info(queryBuilder.toString());
        return queryBuilder.toString();
    }

    static <S> String addColumnNames(Field[] fields, List<String> columnNames, StringBuilder queryBuilder) {
        int counter = 0;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (counter != 0) {
                    columnNames.add(column.name());
                }
            }
            counter++;
        }
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
        return queryBuilder.toString();
    }

    static <S> String addValuesToInsert(List<String> valuesToInsert, StringBuilder queryBuilder,
                                        S entity, Field[] fields) {
        for (Field field1 : fields) {
            field1.setAccessible(true);
            try {
                Object value = field1.get(entity);
                valuesToInsert.add(value.toString());
            } catch (IllegalAccessException e) {
                logger.error("Something went wrong...", e);
            }
        }
        valuesToInsert.remove(0);

        for (int i = 0; i < valuesToInsert.size(); i++) {
            if (i != valuesToInsert.size() - 1) {
                queryBuilder.append(" ?,");
            } else {
                queryBuilder.append(" ? );");
            }
        }
        return queryBuilder.toString();
    }

    static <S> void executeQuery(String queryBuilder, S entity, Field[] fields) {
        try (PreparedStatement st = connection
                .prepareStatement(queryBuilder)) {
            for (int i = 1; i < fields.length; i++) {
                setObject(st, i, fields[i].get(entity));
            }
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("You can not add new entity.", ex);
        } catch (IllegalAccessException e) {
            logger.error("Something went wrong...", e);
        }
    }

    default <S extends T> S save(S entity) throws IllegalAccessException {
        StringBuilder queryBuilder = new StringBuilder();
        List<String> columnNames = new ArrayList<>();
        List<String> valuesToInsert = new ArrayList<>();
        Field[] fields = entity.getClass().getDeclaredFields();

        queryBuilder.append(createQuery(entity, fields, columnNames, valuesToInsert));
        executeQuery(queryBuilder.toString(), entity, fields);
        return entity;
    }

    default <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            try {
                save(entity);
            } catch (IllegalAccessException e) {
                logger.error("Something went wrong...", e);
            }
        }
        return entities;
    }
}




