package com.mrxuyc.shop.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-28
 * Time: 10:31
 */
public class ParameterizedTypeHolder implements ParameterizedType {
    private final Class raw;
    private final Type[] args;
    public ParameterizedTypeHolder(Class raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }
    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }
    @Override
    public Type getRawType() {
        return raw;
    }
    @Override
    public Type getOwnerType() {return null;}

}
