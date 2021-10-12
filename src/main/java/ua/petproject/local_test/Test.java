package ua.petproject.local_test;

import ua.petproject.queries.SelectBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws SQLException {

        Person p = new Person(222, "Sophie");
        String s1 = new SelectBuilder().addTable(new Person()).buildQuery();
        ArrayList<String> list = new ArrayList<String>();
        list.add("Sophie");
        list.add("Ivan");

//        ResultSet resultSet =
//                new SelectBuilder()
//                        .addTable(new Person())
//                        .addIsInListCondition("person_name", list)
//                        .execute();

        ResultSet resultSet =
                new SelectBuilder()
                        .addTable(new Person())
                        .addComparisonCondition("ggg", "=", "Sophie")
                        .addOrCondition()
                        .addEndsWithStrCondition("person_name","a",true)
                        .addAndCondition()
                        .addComparisonCondition("id", ">", "1222")
                        .execute();

        List<Person> personList = new ArrayList<Person>();

        while (true) {
            try {
                if (!resultSet.next()) break;
                personList.add(new Person(resultSet.getInt(1), resultSet.getString(2))
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println(personList);

    }
}
