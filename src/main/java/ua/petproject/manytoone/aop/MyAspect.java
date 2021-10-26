package ua.petproject.manytoone.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import java.util.Arrays;

@Aspect
public class MyAspect {

    @Pointcut("execution(public * ua.petproject.manytoone.aop.User.getTests())")
    public void catchAnnotation(){}

    @Before("catchAnnotation()")
    public void initField(JoinPoint jp) {
        System.out.println("initFieldentry");
        Object[] args = jp.getArgs();
        System.out.println(Arrays.toString(args));
    }
    
}
