package ua.petproject.queries;

import lombok.Data;
import ua.petproject.annotations.Column;
import ua.petproject.annotations.Table;
import ua.petproject.exceptions.WrongQueryException;
import ua.petproject.util.DataBaseConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Data
public class SelectBuilder<H> {

    private String selectQuery = "SELECT *";
    private H entity;
    private String tableName;
    private boolean isSeparator = true;
    private boolean isFirst = true;
    private String comparisonConditions;
    private String isInListConditions;
    private final String[] comparisonSigns = {">", "<", ">=", "<=", "=", "<>"};


    public SelectBuilder addTable(H entity) {
        String tableName = entity.getClass().getAnnotation(Table.class).name();
        if (tableName == null) try {
            throw new WrongQueryException("Invalid table name");
        } catch (WrongQueryException e) {
            e.printStackTrace();
        }
        setTableName(tableName);
        setEntity(entity);
        setSelectQuery(getSelectQuery() + " FROM " + tableName);
        return this;
    }

    public SelectBuilder addOrCondition() {
        if (!isSeparator) {
            setSelectQuery(getSelectQuery() + " OR");
            setSeparator(true);
        }
        return this;
    }

    public SelectBuilder addAndCondition() {
        addAndSeparatorIfItIsNotSet();
        return this;
    }

    public SelectBuilder addComparisonCondition(String field,
                                                String condition,
                                                String parameter) {

        checkIfIsFirstCondition();
        final String[] comparisonSigns = {">", "<", ">=", "<=", "=", "<>"};
        try {
            addAndSeparatorIfItIsNotSet();
            Field f = entity.getClass().getDeclaredField(field);
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                if (stringIsIn(condition, comparisonSigns)) {
                    setSelectQuery(getSelectQuery() + " " + column.name() + " " +
                            condition + " '" +
                            parameter + "'");
                    setSeparator(false);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SelectBuilder addIsInListCondition(String field, ArrayList<String> listValues) {
        return addIsInListCondition(field, listValues, false);
    }

    public SelectBuilder addIsInListCondition(String field, ArrayList<String> listValues, boolean isNotIn) {

        checkIfIsFirstCondition();
        try {
            addAndSeparatorIfItIsNotSet();
            Field f = entity.getClass().getDeclaredField(field);
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                if (isNotIn)
                    setSelectQuery(getSelectQuery() + " " + column.name() + " NOT IN('");
                else
                    setSelectQuery(getSelectQuery() + " " + column.name() + " IN('");

                for (int i = 0; i < listValues.size(); i++) {
                    setSelectQuery(getSelectQuery() + listValues.get(i) + "'");
                    if (i != listValues.size() - 1) setSelectQuery(getSelectQuery() + ",'");
                }
                setSelectQuery(getSelectQuery() + ")");
                setSeparator(false);

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return this;
    }

    public SelectBuilder addBeginsWithStrCondition(String field, String bgnStr) {
        return addBeginsWithStrCondition(field, bgnStr, false);
    }

    public SelectBuilder addBeginsWithStrCondition(String field, String bgnStr, boolean isNot) {
        return likeCondition(field, bgnStr, isNot, true);
    }

    public SelectBuilder addEndsWithStrCondition(String field, String bgnStr) {
        return addEndsWithStrCondition(field, bgnStr, false);
    }

    public SelectBuilder addEndsWithStrCondition(String field, String bgnStr, boolean isNot) {
        return likeCondition(field, bgnStr, isNot, false);
    }

    public SelectBuilder likeCondition(String field, String bgnStr, boolean isNot, boolean beginning) {
        checkIfIsFirstCondition();
        try {
            addAndSeparatorIfItIsNotSet();
            Field f = entity.getClass().getDeclaredField(field);
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                if (isNot)
                    setSelectQuery(getSelectQuery() + " " + column.name() + " NOT LIKE");
                else
                    setSelectQuery(getSelectQuery() + " " + column.name() + " LIKE");

                if (beginning)
                    setSelectQuery(getSelectQuery() + " '" + bgnStr + "%'");
                else
                    setSelectQuery(getSelectQuery() + " '%" + bgnStr + "'");
                setSeparator(false);

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ResultSet execute() {
        DataBaseConnection dataBaseConnection = DataBaseConnection.getInstance();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = dataBaseConnection.getConnection().prepareStatement(buildQuery());
            resultSet = preparedStatement.executeQuery();
            dataBaseConnection.close();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String buildQuery() {
        setSelectQuery(getSelectQuery() + " ;");
        System.out.println(getSelectQuery());
        return getSelectQuery();
    }

    private boolean stringIsIn(String str, String[] strArr) {
        for (String strElem : strArr) {
            if (str.equals(strElem))
                return true;
        }
        return false;
    }

    private void checkIfIsFirstCondition() {
        if (isFirst) {
            setSelectQuery(getSelectQuery() + " WHERE");
            setFirst(false);
        }
    }

    private void addAndSeparatorIfItIsNotSet() {
        if (!isSeparator()) {
            setSelectQuery(getSelectQuery() + " AND");
            setSeparator(true);
        }
    }
}
