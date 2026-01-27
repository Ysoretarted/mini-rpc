package org.example.rpcdemo.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心还不太会写
 */
public class ProviderRegistry {

    private final Map<String, InvocationWrapper<?>> registeryMap = new ConcurrentHashMap<>();

    public <I> void registry(Class<I> interfaceClass, I serviceInstance) {
        if(!interfaceClass.isInterface()){
            throw new IllegalArgumentException("注册的类型必须为接口");
        }
        InvocationWrapper<?> existInstance = registeryMap.putIfAbsent(interfaceClass.getName(), new InvocationWrapper<>(interfaceClass, serviceInstance));
        if(existInstance != null){
            throw new IllegalArgumentException(interfaceClass.getName() + "重复注册了");
        }
    }

    public InvocationWrapper<?> getService(String serviceName) {
        return registeryMap.get(serviceName);
    }


    public static class InvocationWrapper<I> {

        public InvocationWrapper(Class<I> interfaceClass, I instance) {
            this.interfaceClass = interfaceClass;
            this.instance = instance;
        }

        private final Class<I> interfaceClass;

        private final I instance;


        public Object invoke(String methodName, Class<?>[] paramClass, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            //这里不能拿实例的class去获取方法, 可能并不是我们想注册的
//            Method method = instance.getClass().getDeclaredMethod(methodName, paramClass);
            Method method = interfaceClass.getDeclaredMethod(methodName, paramClass);
            return method.invoke(instance, params);
        }

    }
}
