import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Proxy<T> implements InvocationHandler {
    private final T realInstance;

    public static <T> T getInstance(T target) {
        return (T)java.lang.reflect.Proxy.newProxyInstance(target.getClass().getClassLoader(),
                                                           target.getClass().getInterfaces(),
                                                           new Proxy<T>(target));
    }

    public Proxy(T realInstance) {
        this.realInstance = realInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(realInstance, args);
        } catch(IllegalAccessException e) {
            return null;
        } catch(IllegalArgumentException e) {
            return null;
        } catch(InvocationTargetException e) {
            return null;
        }
    }
}

interface FooInterface {
    public void x();
}

class Foo implements FooInterface {
    @Override
    public void x() {
        System.out.println("hi");
    }
}

public class SimpleProxy {
    public static void main(String[] args) {
        FooInterface test = Proxy.getInstance(new Foo());

        test.x();
    }
}

