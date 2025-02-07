package com.tr.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Asen
 */
public class BeanUtil {
    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static final String SORT_DESC = "desc"; //排序方式

    /**
     * 用来遍历对象属性和属性值来判断对象属性是否为空
     *
     * @param obj            传入对象
     * @param checkAttribute 判断的属性(不区分大小写)
     * @return 返回校验结果 如果符合标签，返回true
     */
    public static boolean checkObject(Object obj, String... checkAttribute) {
        Assert.notNull(obj, "对象不能为空");
        Field[] fields = obj.getClass().getDeclaredFields();
        if (checkAttribute == null) {
            return true;
        }
        for (Field field : fields) {
            for (String attribute : checkAttribute) {
                if (field.getName().equalsIgnoreCase(attribute)) {
                    if (checkValueIsNull(field, obj)) { //如果属性为空
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 检查字段是否为空
     *
     * @param field 属性
     * @param obj   对象
     * @return 如果属性为空，返回True
     */
    private static boolean checkValueIsNull(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj) == null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 统计不为空的属性
     *
     * @param obj 统计对象
     * @return 结果
     */
    public static <T> int countNotNullValues(T obj) {
        int count = 0;
        if (obj != null) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (countNotNullValues(field, obj)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean countNotNullValues(Field field, Object obj) {
        assert field != null && obj != null;
        try {
            //获取属性的类型(有需要，自己增加)
            Object o = field.get(obj);
            if (o != null) {
                return true;
            }
        } catch (Exception e) {
            logger.error(field.getName() + "获取值失败");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将List中的对象拷贝到目标对象的List中(标准Bean)
     *
     * @param sourceList 源List<T>
     * @param targetCls  目标对象类型
     * @param <T>        源类型
     * @param <R>        目标类型
     * @return 目标类型List数组
     */
    public static <T, R> List<R> beanCopyPropertiesForList(List<T> sourceList, Class<R> targetCls) {
        Assert.notNull(targetCls, "target class can not null");
        List<R> targetList = new ArrayList<R>();
        if (sourceList != null && !sourceList.isEmpty()) {
            for (T source : sourceList) {
                targetList.add(beanCopyProperties(source, targetCls));
            }
        }
        return targetList;
    }

    /**
     * 属性值拷贝
     *
     * @param source    源对象
     * @param targetCls 目标对象类
     * @Return 拷贝目标类的实体
     */
    public static <R> R copyProperties(Object source, Class<R> targetCls) {
        Assert.notNull(targetCls, "target class can not null");
        try {
            R target = targetCls.newInstance();
            if (source != null) {
                copyProperties(source, target, false);
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 属性值拷贝(标准Bean)
     *
     * @param source    源对象
     * @param targetCls 目标对象类
     * @Return 拷贝目标类的实体
     */
    public static <R> R beanCopyProperties(Object source, Class<R> targetCls) {
        try {
            Assert.notNull(targetCls, "target class can not null");
            R target = targetCls.newInstance();
            if (source != null) {
                BeanUtils.copyProperties(source, target);
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 属性值拷贝(标准Bean)
     *
     * @param source    源对象
     * @param targetCls 目标对象类
     * @return 拷贝目标类的实体
     */
    public static <R> R beanCopyProperties(Object source, Class<R> targetCls, String ignoreProperties) {
        try {
            Assert.notNull(targetCls, "target class can not null");
            R target = targetCls.newInstance();
            if (source != null) {
                if (StringUtils.isEmpty(ignoreProperties)) {
                    BeanUtils.copyProperties(source, target);
                } else {
                    BeanUtils.copyProperties(source, target, ignoreProperties.split(","));
                }
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 属性值拷贝(标准Bean)
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 拷贝目标类的实体
     */
    public static void beanCopyProperties(Object source, Object target, String ignoreProperties) {
        try {
            Assert.notNull(target, "target can not null");
            if (source != null) {
                if (StringUtils.isEmpty(ignoreProperties)) {
                    BeanUtils.copyProperties(source, target);
                } else {
                    BeanUtils.copyProperties(source, target, ignoreProperties.split(","));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将属性一样的两个类，进行属性值拷贝
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, true);
    }

    /**
     * 将属性一样的两个类，进行属性值拷贝,值为空时，不进行拷贝
     *
     * @param source             源对象
     * @param target             目标对象
     * @param attributeCoverFlag 当为true时，属性值为空时也进行覆盖
     */
    public static void copyProperties(Object source, Object target, boolean attributeCoverFlag) {
        if (source != null && target != null) {
            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = target.getClass().getDeclaredFields();
            for (Field sourceField : sourceFields) {
                for (Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName()) && sourceField.getType().equals(targetField.getType())) {
                        sourceField.setAccessible(true);
                        targetField.setAccessible(true);
                        try {
                            Object value = sourceField.get(source);
                            if (attributeCoverFlag) {
                                targetField.set(target, value);
                            } else if (value != null) {
                                targetField.set(target, value);
                            }
                        } catch (IllegalAccessException e) {
                            logger.error(target.getClass() + " 中 " + targetField.getName() + " 赋值错误");
                            throw new RuntimeException(target.getClass() + " 中 " + targetField.getName() + " 赋值错误");
                        }
                    }
                }
            }
        }
    }

    /**
     * Get value or default value if it is null.
     *
     * @param value        原值
     * @param defaultValue 默认值
     * @param <T>          类型
     * @return 结果
     */
    public static <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * 将属性一样的两个类，进行属性值拷贝,值为空时，不进行拷贝
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesWithOutNull(Object source, Object target) {
        if (source != null && target != null) {
            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = target.getClass().getDeclaredFields();
            for (Field sourceField : sourceFields) {
                for (Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName()) && sourceField.getType().equals(targetField.getType())) {
                        sourceField.setAccessible(true);
                        targetField.setAccessible(true);
                        try {
                            Object value = sourceField.get(source);
                            if (value != null) {
                                targetField.set(target, value);
                            }
                        } catch (IllegalAccessException e) {
                            logger.error(target.getClass() + " 中 " + targetField.getName() + " 赋值错误");
                        }
                    }
                }
            }
        }
    }
}
