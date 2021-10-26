package ua.petproject.manytoone.aop;

import java.util.Set;

public class Main {

    public static void main(String[] args) {
        printName("Толя");
        printName("Вова");
        printName("Саша");
        Set<Test> tests = new User().getTests();
    }

    public static void printName(String name) {
        System.out.println(name);
    }
}
