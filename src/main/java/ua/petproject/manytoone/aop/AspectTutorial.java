package ua.petproject.manytoone.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectTutorial {
    @Pointcut("execution(* Main.printName(String))")
    public void greeting() {
    }

    @Before("greeting()")
    public void beforeAdvice() {
        System.out.print("Привет ");
    }
}
