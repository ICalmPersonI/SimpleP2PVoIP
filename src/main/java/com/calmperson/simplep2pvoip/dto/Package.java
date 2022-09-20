package com.calmperson.simplep2pvoip.dto;

import java.io.Serializable;

public class Package<T> implements Serializable {

    String objectName;
    T object;

    public Package(String objectName, T object) {
        this.objectName = objectName;
        this.object = object;
    }

    public String getObjectName() {
        return objectName;
    }

    public T getObject() {
        return object;
    }
}
