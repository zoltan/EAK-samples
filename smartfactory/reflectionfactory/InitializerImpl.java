package reflectionfactory;

public class InitializerImpl implements Initializer {
    @Override
    public void init(Initializable target) {
        target.setX(-1);
        target.setY(1);
    }
}

