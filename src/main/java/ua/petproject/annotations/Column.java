package ua.petproject.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class for adding field in the query while table creation in the database.
 *
 * @name - specify name of the field otherwise field name will be used.
 * @max - max length of the string-type fields
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default "";

    int max() default 255;
}
