package reflectionfactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryPreInit {
    Class<? extends Initializer> 
	initializer() default NullInitializerImpl.class;
}

