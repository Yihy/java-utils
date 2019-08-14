package cc.yihy.util;

import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yihy
 * Create at 2018/10/31
 */
public class CollectionUtil {

    /**
     * 截取list
     * 尽最大努力获取到该范围的元素
     *
     * @param list  被截取的list
     * @param beginIndex 开始的索引
     * @param endIndex   结束的索引，不包含
     * @param <E>   集合元素
     * @return 截取到的集合
     */
    public static <E> List<E> subList(List<E> list, int beginIndex, int endIndex) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        if (beginIndex >= endIndex) {
            throw new IllegalArgumentException(MessageFormat.format("截取的索引错误 beginIndex:{0} >= endIndex {1}", beginIndex, endIndex));
        }
        if (endIndex > list.size()) {
            endIndex = list.size();
        }

        List<E> result = new ArrayList<>();
        for (int i = beginIndex; i < endIndex; i++) {
            result.add(list.get(i));
        }
        return result;
    }
}
