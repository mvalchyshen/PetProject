package ua.petproject.impl;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import ua.petproject.annotations.OneToAny;
import ua.petproject.repository.Repository;
import ua.petproject.repository.RepositoryFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;

@Aspect
public class OneToOneOOP {
    @Before(value = "@annotation(ua.petproject.annotations.SaveMethod) && execution(@ua.petproject.annotations.SaveMethod * *.*(..)) && args(e)", argNames = "e")
    public void beforeCallAt(Object e) throws IllegalAccessException {
        changeClassToId(e);
    }

    @AfterReturning(value = "@annotation(ua.petproject.annotations.SaveMethod) && execution(@ua.petproject.annotations.SaveMethod * *.*(..)) && args(e)", argNames = "e")
    public void afterReturn(Object e) throws IllegalAccessException, SQLException {
        changeIdToClass(e);
    }

    private void changeClassToId(Object e) throws IllegalAccessException {
        Field[] allFields = e.getClass().getDeclaredFields();

        for(Field field:allFields) {
            if(field.isAnnotationPresent(OneToAny.class)) {
                field.setAccessible(true);
                field.setLong(e, (Long) field.get(e));
            }
        }
    }

    private void changeIdToClass(Object e) throws SQLException, IllegalAccessException {
        Field[] allFields = e.getClass().getDeclaredFields();

        for(Field field:allFields) {
            if(field.isAnnotationPresent(OneToAny.class)) {
                field.setAccessible(true);
                Repository repository = RepositoryFactory.of(e.getClass());

                field.set(e, repository.findById(field.get(e)));
            }
        }
    }



}
