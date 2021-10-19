package ua.petproject.manytoone;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.criteria.Fetch;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchTypeLoad<E> {

    private Class<E> className;

    public FetchTypeLoad(Class<E> className) {
        this.className = className;
    }

    public void eagerLoading() {
        Class<?> aClass;
        String regex = ".([A-Za-z]+)>";
        Field fieldWithManyToOne = Arrays.stream(className.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getAnnotation(OneToMany.class) !=null)
                .findAny().orElseThrow(() -> new RuntimeException("No fields with @ManyToOne annotation"));
        FetchType fetchType = fieldWithManyToOne.getAnnotation(OneToMany.class).fetch();
        String mappedBy = fieldWithManyToOne.getAnnotation(OneToMany.class).mappedBy();
        Type genericType = fieldWithManyToOne.getGenericType();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(genericType.getTypeName());
        boolean b = matcher.find();
        String classForName = matcher.group(1);
        System.out.println(group);
        try {
            aClass = Class.forName(classForName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (fetchType.equals(FetchType.EAGER) ) {
//            fieldWithManyToOne = new Set<>()
        }
        System.out.println(fieldWithManyToOne +"\n"+ genericType.getTypeName());
    }

    public static void main(String[] args) {
        FetchTypeLoad<Test> fetchTypeLoad = new FetchTypeLoad<>(Test.class);

        fetchTypeLoad.eagerLoading();
    }

}
