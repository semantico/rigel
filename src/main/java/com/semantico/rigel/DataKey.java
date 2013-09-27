package com.semantico.rigel;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

public interface DataKey<F> {

    void storeValue(Map<DataKey<?>, ? super Object> map, ClassToInstanceMap<FieldDataSource<?>> context);

    F retrieveValue(Map<DataKey<?>, ? super Object> map);

}
