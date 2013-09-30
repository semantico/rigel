package com.semantico.rigel.fields;

import com.semantico.rigel.RigelContext;
import com.semantico.rigel.RigelContext.RigelConfig.FieldConfig;

public class ConfigurableFieldName implements SimpleField.FieldNameSource {

    private final String configurationName;

    private String resolvedFieldName;

    private RigelContext rigelContext;

    public ConfigurableFieldName(String configurationName) {
        this.configurationName = configurationName;
    }

    @Override
    public String getFieldName() {
        if (resolvedFieldName == null) {
            resolve();
        }
        return resolvedFieldName;
    }

    @Override
    public void bindToContext(RigelContext rigelContext) {
        if (this.rigelContext == null) {
            this.rigelContext = rigelContext;
        } else if (this.rigelContext != rigelContext) {
            throw new RuntimeException("cant register the same instance of configurable field " + configurationName + "in two different contexts");
        }
    }

    private void resolve() {
        if (rigelContext == null) {
            throw new RuntimeException("The schema this field (" + "configurationName" + ") is used in must be registered with a RigelContext before you can use it");
        } else {
            FieldConfig fieldConfig = rigelContext.config.fieldConfig.get(configurationName);
            if (fieldConfig == null) {
                throw new RuntimeException("field " + configurationName + " not configured!");
            }
            this.resolvedFieldName = fieldConfig.solrFieldName;
        }
    }
}
