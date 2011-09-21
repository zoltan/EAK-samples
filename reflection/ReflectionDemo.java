import java.lang.reflect.Method;

@interface A {
    String foo();
    String bar();
}

@A(foo="abc", bar="def")
class X {
    private int a;
    
    public X() {
        a = 5;
    }
    
    private void doIt() {
        a = 2*a;
    }
    
    public void test() {
        doIt();
        System.out.println(a);
    }
}

public class ReflectionDemo {
    private static String getTypeList(Class<?>[] params) {
        StringBuilder sb = new StringBuilder();
        
        for(Class c : params) {
            sb.append(c.getCanonicalName() + ", ");
        }
        
        return sb.toString();
    }
    
    public static void main(String[] args) {
        new X().test();
        
        System.out.println("canonical name: " + X.class.getCanonicalName());
        System.out.println("number of ctors: " + X.class.getConstructors().length);
        System.out.println("number of methods: " + X.class.getMethods().length);
        for(Method m : X.class.getMethods()) {
            System.out.println("\tmethod name: " + m.getName() + " (" + getTypeList(m.getParameterTypes()) + ")");
        }
    }
}

