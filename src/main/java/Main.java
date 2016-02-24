import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.util.List;

public class Main {
    public <S> Class<S> createDynamicSubclass(Class<S> superclass) {
        Enhancer e = new Enhancer() {
            @Override
            protected void filterConstructors(Class sc, List constructors) {
                // Don't filter
            }
        };

        if (superclass.isInterface()) {
            e.setInterfaces(new Class[] { superclass });
        }
        else {
            e.setSuperclass(superclass);
        }

        e.setCallbackType(NoOp.class);
        @SuppressWarnings("unchecked")
        Class<S> proxyClass = e.createClass();
        return proxyClass;
    }

}
