package cc.yihy.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Stream工具类
 *
 * @author yihy
 * Created at 2019/08/14
 */
public class StreamUtils {

    /**
     * 获取集合的stream
     * 集合为null是返回Stream.empty()
     *
     * @param collection 集合
     * @param <T>        集合元素
     * @return 集合的流对象
     */
    public static <T> Stream<T> stream(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return Stream.empty();
        }
        return collection.stream();
    }

    /**
     * 获取切分后字符串的stream
     *
     * @param str            待切分字符串
     * @param separatorChars 分隔符
     * @return 切分后字符串产生的流
     */
    public static Stream<String> split(String str, String separatorChars) {
        String[] array = StringUtils.split(str, separatorChars);
        if (array == null) {
            return Stream.empty();
        }
        return Arrays.stream(array);
    }
}
