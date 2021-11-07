//package ua.petproject.queries;
//
//import lombok.Data;
//import ua.petproject.annotations.Column;
//import ua.petproject.annotations.Table;
//import ua.petproject.util.DataBaseConnection;
//
//import java.lang.reflect.Field;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.logging.Logger;
//
//@Data
//public class SelectBuilder<H> {
//
//    private static final Logger logger = Logger.getGlobal();
//    private String selectBeginning = " ";
//    private String aggregation;
//    private String tableName;
//    private String whereConditions;
//    private String groupBy;
//    private String having;
//    private String orderBy;
//    private H entity;
//    private boolean isSeparator = true;
//    private boolean isSeparatorGroup = true;
//    private boolean isFirst = true;
//    private boolean isSelectedAll = true;
//
//
//    public SelectBuilder addColumns(String[] columnNames) {
//        return addColumns(columnNames, false);
//    }
//
//    public SelectBuilder addColumns(String[] columnNames, boolean unique) {
//
//        setSelectBeginning("");
//        if (unique) setSelectBeginning("DISTINCT ");
//        for (int i = 0; i < columnNames.length; i++) {
//            Field f = null;
//            try {
//                f = entity.getClass().getDeclaredField(columnNames[i]);
//            } catch (NoSuchFieldException e) {
//                logger.info(e.toString());
//            }
//            if (f != null) {
//                Column column = f.getAnnotation(Column.class);
//                if (i == (columnNames.length - 1))
//                    setSelectBeginning(getSelectBeginning() + column.name() + " ");
//                else
//                    setSelectBeginning(getSelectBeginning() + column.name() + ",");
//            }
//        }
//        setSelectedAll(false);
//        return this;
//
//    }
//
//    public SelectBuilder addTable(H entity) {
//        String tableName = entity.getClass().getAnnotation(Table.class).name();
//        setTableName(tableName);
//        setEntity(entity);
//        return this;
//    }
//
//    public SelectBuilder addOrCondition() {
//        if (!isSeparator) {
//            setWhereConditions(getWhereConditions() + " OR");
//            setSeparator(true);
//        }
//        return this;
//    }
//
//    public SelectBuilder addOrGroupCondition() {
//        if (!isSeparatorGroup) {
//            setHaving(getHaving() + " OR");
//            setSeparatorGroup(true);
//        }
//        return this;
//    }
//
//    public SelectBuilder addAndCondition() {
//        addAndSeparatorIfItIsNotSet();
//        return this;
//    }
//
//    public SelectBuilder addAndGroupCondition() {
//        addAndGroupSeparatorIfItIsNotSet();
//        return this;
//    }
//
//    public SelectBuilder addComparisonCondition(String field,
//                                                String condition,
//                                                Object parameter) {
//
//        if (getWhereConditions() == null)
//            setWhereConditions("");
//        try {
//            addAndSeparatorIfItIsNotSet();
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                if (stringIsInCompressionSights(condition)) {
//                    setWhereConditions(getWhereConditions() + " " + column.name() + " " +
//                            condition + " '" +
//                            parameter + "'");
//                    setSeparator(false);
//                }
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder addIsInListCondition(String field, ArrayList<Object> listOfValues) {
//        return addIsInListCondition(field, listOfValues, false);
//    }
//
//    public SelectBuilder addIsInListCondition(String field,
//                                              ArrayList<Object> listOfValues,
//                                              boolean isNotIn) {
//
//        try {
//            addAndSeparatorIfItIsNotSet();
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                if (isNotIn)
//                    setWhereConditions(getWhereConditions() + " " + column.name() + " NOT IN('");
//                else
//                    setWhereConditions(getWhereConditions() + " " + column.name() + " IN('");
//
//                for (int i = 0; i < listOfValues.size(); i++) {
//                    setWhereConditions(getWhereConditions() + listOfValues.get(i) + "'");
//                    if (i != listOfValues.size() - 1) setWhereConditions(getWhereConditions() + ",'");
//                }
//                setWhereConditions(getWhereConditions() + ")");
//                setSeparator(false);
//
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder addBeginsWithStrCondition(String field, String bgnStr) {
//        return addBeginsWithStrCondition(field, bgnStr, false);
//    }
//
//    public SelectBuilder addBeginsWithStrCondition(String field, String bgnStr, boolean isNot) {
//        return likeCondition(field, bgnStr, isNot, true);
//    }
//
//    public SelectBuilder addEndsWithStrCondition(String field, String bgnStr) {
//        return addEndsWithStrCondition(field, bgnStr, false);
//    }
//
//    public SelectBuilder addEndsWithStrCondition(String field, String bgnStr, boolean isNot) {
//        return likeCondition(field, bgnStr, isNot, false);
//    }
//
//    public SelectBuilder likeCondition(String field,
//                                       String bgnStr,
//                                       boolean isNot,
//                                       boolean beginning) {
//        try {
//            addAndSeparatorIfItIsNotSet();
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                if (isNot)
//                    setWhereConditions(getWhereConditions() + " " + column.name() + " NOT LIKE");
//                else
//                    setWhereConditions(getWhereConditions() + " " + column.name() + " LIKE");
//
//                if (beginning)
//                    setWhereConditions(getWhereConditions() + " '" + bgnStr + "%'");
//                else
//                    setWhereConditions(getWhereConditions() + " '%" + bgnStr + "'");
//                setSeparator(false);
//
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder isBetween(String field, Object first, Object last) {
//        return isBetween(field, first, last, false);
//    }
//
//    public SelectBuilder isBetween(String field, Object first, Object last, boolean isNot) {
//        try {
//            addAndSeparatorIfItIsNotSet();
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                setWhereConditions(getWhereConditions() + " " + column.name());
//
//                if (isNot)
//                    setWhereConditions(getWhereConditions() + " NOT");
//
//                setWhereConditions(getWhereConditions() + " BETWEEN " + first + " AND " + last);
//                setSeparator(false);
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder sortBy(String field) {
//        return sortBy(field, true);
//    }
//
//    public SelectBuilder sortBy(String field, boolean asc) {
//        try {
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                if (getOrderBy() == null)
//                    setOrderBy("");
//                else
//                    setOrderBy(getOrderBy() + ", ");
//
//                setOrderBy(getOrderBy() + column.name());
//
//                if (asc)
//                    setOrderBy(getOrderBy() + " ASC");
//                else
//                    setOrderBy(getOrderBy() + " DESC");
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder countAll(String pseudonym) {
//
//        if (getAggregation() == null)
//            setAggregation("");
//        if (!isSelectedAll())
//            setAggregation(getAggregation() + ", ");
//
//        setAggregation(getAggregation() + "COUNT(*) AS \"" + pseudonym + "\" ");
//        setSelectedAll(false);
//        return this;
//    }
//
//    public SelectBuilder count(String pseudonym, String field) {
//        return count(pseudonym, field, false);
//    }
//
//    public SelectBuilder count(String pseudonym, String field, boolean unique) {
//        return aggregate("COUNT", pseudonym, field, unique);
//    }
//
//    public SelectBuilder sum(String pseudonym, String field) {
//        return aggregate("SUM", pseudonym, field, false);
//    }
//
//    public SelectBuilder sum(String pseudonym, String field, boolean unique) {
//        return aggregate("SUM", pseudonym, field, unique);
//    }
//
//    public SelectBuilder max(String pseudonym, String field) {
//
//        return aggregate("MAX", pseudonym, field, false);
//    }
//
//    public SelectBuilder min(String pseudonym, String field) {
//
//        return aggregate("MIN", pseudonym, field, false);
//    }
//
//    public SelectBuilder avg(String pseudonym, String field) {
//        return aggregate("AVG", pseudonym, field, false);
//    }
//
//    public SelectBuilder aggregate(String method, String pseudonym, String field, boolean unique) {
//
//        if (getAggregation() == null)
//            setAggregation("");
//
//        if (!isSelectedAll())
//            setAggregation(getAggregation() + ", ");
//
//        try {
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                if (unique)
//                    setAggregation(getAggregation() + method + "( DISTINCT " + column.name() + ") AS \"" + pseudonym + "\" ");
//                else
//                    setAggregation(getAggregation() + method + "(" + column.name() + ") AS \"" + pseudonym + "\" ");
//                setSelectedAll(false);
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder groupBy(String field) {
//
//        if (getGroupBy() == null)
//            setGroupBy("");
//        else
//            setGroupBy(getGroupBy() + ", ");
//
//        try {
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                setGroupBy(getGroupBy() + column.name());
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public SelectBuilder addGroupCondition(String field,
//                                           String condition,
//                                           Object parameter) {
//
//        if (getHaving() == null)
//            setHaving("");
//
//        try {
//            addAndGroupSeparatorIfItIsNotSet();
//            Field f = entity.getClass().getDeclaredField(field);
//            Column column = f.getAnnotation(Column.class);
//            if (column != null) {
//                if (stringIsInCompressionSights(condition)) {
//                    setHaving(getHaving() + " " + column.name() + " " +
//                            condition + " '" +
//                            parameter + "'");
//                    setSeparatorGroup(false);
//                }
//            }
//        } catch (NoSuchFieldException e) {
//            logger.info(e.toString());
//        }
//        return this;
//    }
//
//    public ResultSet execute() throws SQLException {
//        DataBaseConnection dataBaseConnection = DataBaseConnection.getInstance();
//        ResultSet resultSet;
//        try {
//            PreparedStatement preparedStatement = dataBaseConnection.getConnection().prepareStatement(buildQuery());
//            resultSet = preparedStatement.executeQuery();
//            return resultSet;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String buildQuery() {
//
//        if (isSelectedAll) setSelectBeginning(" *");
//
//        String query = "SELECT" + getSelectBeginning();
//
//        if (getAggregation() != null)
//            query = query + " " + getAggregation();
//
//        query = query + "FROM " + getTableName();
//
//        if (getWhereConditions() != null)
//            query = query + " WHERE" + getWhereConditions();
//
//        if (getGroupBy() != null)
//            query = query + " GROUP BY " + getGroupBy();
//
//        if (getHaving() != null)
//            query = query + " HAVING " + getHaving();
//
//        if (getOrderBy() != null)
//            query = query + " ORDER BY " + getOrderBy();
//
//        query = query + " ;";
//
//        logger.info("Query:  " + query);
//        return query;
//    }
//
//    private boolean stringIsInCompressionSights(String str) {
//        String[] COMPARISON_SIGNS = {">", "<", ">=", "<=", "=", "<>"};
//        for (String strElem : COMPARISON_SIGNS) {
//            if (str.equals(strElem))
//                return true;
//        }
//        return false;
//    }
//
//    private void addAndSeparatorIfItIsNotSet() {
//        if (!isSeparator()) {
//            setWhereConditions(getWhereConditions() + " AND");
//            setSeparator(true);
//        }
//    }
//
//    private void addAndGroupSeparatorIfItIsNotSet() {
//        if (!isSeparatorGroup()) {
//            setHaving(getHaving() + " AND");
//            setSeparatorGroup(true);
//        }
//    }
//}
//
//
///*
//*  If you want to create SELECT query, you have to:
//*  1. create SelectBuilder object  ->  new SelectBuilder()     -- necessarily
//*
//*  By default all columns are selected, but if you want special ones, you can use:
//*  addColumns(String[] columnNames, boolean unique(default:false))
//*  to get only some columns, also you can make unique==true to have only unique rows
//*
//*  2. add table -> .addTable(entity)                           -- necessarily
//*  (finishing the second point you will get all the information from the table)
//*
//*  3. add conditions:
//*       3.1 addComparisonCondition(field,condition,parameter)
//*           to get info which satisfies the condition with the parameter
//*       3.2 addIsInListCondition(field, listOfValues, isNot (default:false))
//*           to get information for which the field is (not if isNot==true) in the listOfValues
//*       3.3 addBeginsWithStrCondition(field, beginOfString, isNot(default:false))
//*           to get information for which the field (not if isNot==true) begins with beginOfString
//*       3.4 addEndsWithStrCondition(field, endOfString, isNot(default:false))
//*           to get information for which the field (not if isNot==true) ends with endOfString
//*       3.5 isBetween(field,firstValue,lastValue,isNot(default:false))
//*           to get information for which the field is (not if isNot==true) between
//*           firstValue and lastValue
//*       3.6 sortBy(field,isAsc)
//*           to get information sorted by field in ASC or DESC order
//*
//*  4. add separator (by default it is AND)
//*      4.1 addOrCondition()
//*      4.2 addAndCondition()
//*
//*  5. add one of the aggregate functions
//*       5.1 countAll(pseudonym, unique(default:false))
//*           to get a count of (if unique==true -> unique) rows
//*       5.2 count(pseudonym, field,unique(default:false))
//*           to get a count of (if unique==true -> unique) rows of field
//*       5.3 sum(pseudonym, field,unique(default:false))
//*           to get a sum of (if unique==true -> unique) rows of field
//*       5.4 min(pseudonym, field)
//*           to get a min of rows of field
//*       5.4 max(pseudonym, field)
//*           to get a max of rows of field
//*       5.4 avg(pseudonym, field)
//*           to get a avg of rows of field
//*
//*  6. group by some column
//*     groupBy(String field)- to group by some column
//*  7  add group condition addGroupCondition(String field, String condition, Object parameter)
//*     to add some condition to column we grouped
//*  8. execute query to get ResultSet -> execute()            -- necessarily
//*  9. parse ResultSet in a format convenient for you
//*
//*  Example of usage:
//*
//*
//*      ResultSet resultSet =
//                new SelectBuilder()
//                        .addTable(new Person())
//                        .addComparisonCondition("person_age", ">=", 10)
//                        .addOrCondition()
//                        .addEndsWithStrCondition("person_name","a",true)
//                        .addAndCondition()
//                        .isBetween("id", 1223, 1250,true)
//                        .sortBy("person_name",true)
//                        .execute();
//
//
//      ResultSet resultSet1 =
//                new SelectBuilder()
//                        .addTable(new Person())
//                        .count(*)
//                        .addIsInListCondition("person_name", listOfValues)
//                        .execute();
//*
//*       ResultSet resultSet =
//                new SelectBuilder()
//                        .addTable(new Person())
//                        .sum("o","age")
//                        .groupBy("person_name")
//                        .addGroupCondition("person_name", ">", "a")
//                        .execute();
//* */
