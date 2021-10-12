package ua.petproject.queries;

import lombok.Data;
import ua.petproject.annotations.Column;
import ua.petproject.annotations.Table;
import ua.petproject.util.DataBaseConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Data
public class SelectBuilder<H> {

    private static final String[] COMPARISON_SIGNS = {">", "<", ">=", "<=", "=", "<>"};
    private String selectQuery = "SELECT *";
    private H entity;
    private String tableName;
    private boolean isSeparator = true;
    private boolean isFirst = true;
    private String sortInfo = "";

    public SelectBuilder addTable(H entity) {
        String tableName = entity.getClass().getAnnotation(Table.class).name();
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
                                                Object parameter) {

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

    public SelectBuilder addIsInListCondition(String field, ArrayList<Object> listOfValues) {
        return addIsInListCondition(field, listOfValues, false);
    }

    public SelectBuilder addIsInListCondition(String field,
                                              ArrayList<Object> listOfValues,
                                              boolean isNotIn) {

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

                for (int i = 0; i < listOfValues.size(); i++) {
                    setSelectQuery(getSelectQuery() + listOfValues.get(i) + "'");
                    if (i != listOfValues.size() - 1) setSelectQuery(getSelectQuery() + ",'");
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

    public SelectBuilder likeCondition(String field,
                                       String bgnStr,
                                       boolean isNot,
                                       boolean beginning) {
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

    public SelectBuilder isBetween(String field, Object first, Object last) {
        return isBetween(field, first, last, false);
    }

    public SelectBuilder isBetween(String field, Object first, Object last, boolean isNot) {
        checkIfIsFirstCondition();
        try {
            addAndSeparatorIfItIsNotSet();
            Field f = entity.getClass().getDeclaredField(field);
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                setSelectQuery(getSelectQuery() + " " + column.name());

                if (isNot)
                    setSelectQuery(getSelectQuery() + " NOT");

                setSelectQuery(getSelectQuery() + " BETWEEN " + first + " AND " + last);
                setSeparator(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SelectBuilder sortBy(String field, boolean asc) {
        try {
            Field f = entity.getClass().getDeclaredField(field);
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                setSortInfo(" ORDER BY " + column.name());
                if (asc)
                    setSortInfo(getSortInfo() + " ASC");
                else
                    setSortInfo(getSortInfo() + " DESC");
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
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String buildQuery() {
        setSelectQuery(getSelectQuery() + getSortInfo() + " ;");
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


/*
*  If you want to create SELECT query, you have to:
*  1. create SelectBuilder object  ->  new SelectBuilder()
*  2. add table -> .addTable(entity)
*  (finishing the second point you will get all the information from the table)
*  3. add conditions:
*       3.1 addComparisonCondition(field,condition,parameter)
*           to get info which satisfies the condition with the parameter
*       3.2 addIsInListCondition(field, listOfValues, isNot (default:false))
*           to get information for which the field is (not if isNot==true) in the listOfValues
*       3.3 addBeginsWithStrCondition(field, beginOfString, isNot(default:false))
*           to get information for which the field (not if isNot==true) begins with beginOfString
*       3.4 addEndsWithStrCondition(field, endOfString, isNot(default:false))
*           to get information for which the field (not if isNot==true) ends with endOfString
*       3.5 isBetween(field,firstValue,lastValue,isNot(default:false))
*           to get information for which the field is (not if isNot==true) between
*           firstValue and lastValue
*       3.6 sortBy(field,isAsc)
*           to get information sorted by field in ASC or DESC order
*  4. add separator (by default it is AND)
*      4.1 addOrCondition()
*      4.2 addAndCondition()
*  5. execute query to get ResultSet -> execute()
*  6. parse ResultSet in a format convenient for you
*
*  Example of usage:
*
*
*      ResultSet resultSet =
                new SelectBuilder()
                        .addTable(new Person())
                        .addComparisonCondition("person_age", ">=", 10)
                        .addOrCondition()
                        .addEndsWithStrCondition("person_name","a",true)
                        .addAndCondition()
                        .isBetween("id", 1223, 1250,true)
                        .sortBy("person_name",true)
                        .execute();
*
*
* */