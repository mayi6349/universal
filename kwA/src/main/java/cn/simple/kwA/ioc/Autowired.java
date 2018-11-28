package cn.simple.kwA.ioc;

@java.lang.annotation.Target(value={java.lang.annotation.ElementType.CONSTRUCTOR,java.lang.annotation.ElementType.FIELD,java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface Autowired {
	public String value() default "";

}
