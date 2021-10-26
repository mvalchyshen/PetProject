package ua.petproject.manytoone.aop;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

public class User {

    @OneToMany(fetch = FetchType.LAZY)
    Set<Test> tests = new HashSet<>();

    public Set<Test> getTests() {
        return tests;
    }
}
