package com.statter.statter.util;

import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeanUtils {

    public static <T> T copy(Object src, Class<T> target) {
        try {
            T t = target.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(src, t, target);
            return t;
        } catch (Exception e) {
            throw new AppBizException(HttpStatusExtend.ERROR_COMMON_DEV_PARAM_CONVERT);
        }
    }

    public static <T> List<T> copyList(List srcs, Class<T> target) {
        try {
            List<T> list = new ArrayList<>();
            for (Object src : srcs) {
                list.add(copy(src, target));
            }
            return list;
        } catch (Exception e) {
            throw new AppBizException(HttpStatusExtend.ERROR_COMMON_DEV_PARAM_CONVERT);
        }
    }

    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


}
