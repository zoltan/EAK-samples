package reflectionfactory;

abstract class InitializableImpl implements Initializable {
    protected int X;
    protected int Y;

    @Override
    public void setX(int X) {
        this.X = X;
    }

    @Override
    public void setY(int Y) {
        this.Y = Y;
    }

    public int getProduct() {
        return X * Y;
    }
}

//@FactoryPreInit(initializer=InitializerImpl.class)
class Foo extends InitializableImpl {
}

@FactoryPreInit(initializer=InitializerImpl.class)
class Bar extends InitializableImpl {
    public Bar() {
        X = 2;
        Y = 3;
    }
}








class SmartFactory {
    public static <T extends Initializable> 
		    T
		    getInitializable(Class<T> clazz) {
        T instance = null;

        try {
            instance = clazz.newInstance();
        } catch(IllegalAccessException e) {
            return null;
        } catch(InstantiationException e) {
            return null;
        }

        if(clazz.isAnnotationPresent(FactoryPreInit.class)) {
            FactoryPreInit ann = 
        	clazz.getAnnotation(FactoryPreInit.class);

            try {
                Initializer init 
            	    = ann.initializer().newInstance();
                init.init(instance);
            } catch(IllegalAccessException e) {
                return null;
            } catch(InstantiationException e) {
                return null;
            }

        }

        return instance;
    }
}

public class ReflectionFactory {
    public static void main(String[] args) {
        Foo fooInstance =
    	    SmartFactory.getInitializable(Foo.class);
        System.out.println("foo's product: " + fooInstance.getProduct());

        Bar barInstance =
    	    SmartFactory.getInitializable(Bar.class);
        System.out.println("bar's product: " + barInstance.getProduct());
    }
}





