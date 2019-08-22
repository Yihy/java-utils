package cc.yihy.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 分析编译时泛型参数，获取class
 *
 * @author jianyun.zhao
 * Create at 2018/11/12
 */
public class GenericToClassUtil {

    private static Map<String, Class<?>> classCached = new ConcurrentHashMap<>();

    /**
     * 获取subClass实现superClass，指定的泛型参数class
     * 要求subClass必须是直接实现或继承superClass
     *
     * @param subClass     实现superClass的subClass
     * @param superClass   接口类或抽象类
     * @param genericIndex 泛型参数的索引
     * @param <T>          泛型参数的类型
     * @param <A>          父类的类型
     * @return 泛型参数的class
     */
    @SuppressWarnings("unchecked")
    public static <T, A> Class<T> getGenericType(Class<? extends A> subClass, Class<A> superClass, int genericIndex) {
        String key = subClass.getName() + "_" + superClass.getName() + "_" + genericIndex;
        return (Class<T>) classCached.computeIfAbsent(key, k -> getGenericTypeByIndex(subClass, superClass, genericIndex));
    }

    private static <T, A> Class<T> getGenericTypeByIndex(Class<? extends A> subClass, Class<A> superClass, int genericIndex) {
        ParameterizedType type = findTypeBySuperClass(subClass, superClass);
        return getClassByIndex(subClass, genericIndex, type);
    }

    @SuppressWarnings("unchecked")
    private static <T, A> Class<T> getClassByIndex(Class<? extends A> subClass, int genericIndex, ParameterizedType type) {
        Type actualTypeArgument = type.getActualTypeArguments()[genericIndex];
        validTypeIsClass(subClass, actualTypeArgument);
        return (Class<T>) actualTypeArgument;
    }

    /**
     * 验证泛型参数是否是Class
     * @param subClass
     * @param actualTypeArgument
     * @param <A>
     */
    private static <A> void validTypeIsClass(Class<? extends A> subClass, Type actualTypeArgument) {
        if (actualTypeArgument instanceof Class) {
            return;
        }
        throw new RuntimeException(MessageFormat.format("不支持自动推断运行时的泛型，请重写 {0} 类的获取泛型类的方法", subClass.getName()));
    }

    private static <A> Type[] getGenericType(Class<? extends A> subClass, Class<A> superClass) {
        Type[] generic;
        if (superClass.isInterface()) {
            generic = subClass.getGenericInterfaces();
        } else {
            generic = new Type[1];
            generic[0] = subClass.getGenericSuperclass();
        }
        return generic;
    }

    private static <A> ParameterizedType findTypeBySuperClass(Class<? extends A> subClass, Class<A> superClass) {
        Type[] generic = getGenericType(subClass, superClass);

        Optional<ParameterizedType> typeOptional = Stream.of(generic).filter(t -> t instanceof ParameterizedType)
                .map(t -> (ParameterizedType) t).filter(type -> Objects.nonNull(type.getRawType()))
                .filter(type -> type.getRawType().equals(superClass)).findAny();
        if (!typeOptional.isPresent()) {
            throw buildException(generic, subClass, superClass);
        }
        return typeOptional.get();
    }

    private static <A> RuntimeException buildException(Type[] generic, Class<? extends A> subClass, Class<A> superClass) {
        Optional<? extends Class<?>> associationClass = findAssociationClass(generic, subClass, superClass);
        if (!associationClass.isPresent()) {
            return new IllegalStateException(
                    MessageFormat.format("{0} 没有直接继承/实现 {1}\n解决办法: 1, {0} 重写 {1} 中的获取泛型类的方法  2, {0} 直接继承或实现 {1}",
                            subClass.getName(), superClass.getName()));
        }
        String name = associationClass.get().getName();
        return new RuntimeException(MessageFormat.format(
                "{1} 需要重写 {2} 的分析泛型类方法\n解决办法: 1, 重写 {1} 类的泛型推断方法  2, 重写 {0} 的泛型推断方法  3, 让 {0} 直接继承或实现 {2}",
                subClass.getName(), name, superClass.getName()));
    }

    /**
     * 查找一个类，既实现了superClass，又是subClass的父类
     *
     * @param <A>        基类
     * @param generic    this类的父类信息
     * @param subClass   当前实现类
     * @param superClass this类的基类
     * @return 实现了superClass，又是subClass父类的类
     */
    private static <A> Optional<? extends Class<?>> findAssociationClass(Type[] generic, Class<? extends A> subClass,
                                                                         Class<A> superClass) {
        List<Type> list = new ArrayList<>(Arrays.asList(generic));
        if (superClass.isInterface()) {
            list.add(subClass.getGenericSuperclass());
        }
        return list.stream()
                .filter(t -> t instanceof ParameterizedType)
                .map(t -> (ParameterizedType) t)
                .filter(type -> Objects.nonNull(type.getRawType()))
                .map(type -> (Class<?>) type.getRawType())
                .filter(superClass::isAssignableFrom)
                .findAny();
    }
}
