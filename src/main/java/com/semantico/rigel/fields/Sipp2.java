package com.semantico.sipp2.solr.fields;

import com.semantico.sipp2.solr.fields.types.StringField;

public final class Sipp2 {

    public static final StringField TYPE = new StringField("s2_type");
    public static final StringField ID = new StringField("s2_id");
    public static final StringField PARENT = new StringField("s2_parent");
    public static final StringField XML_CONTENT = new StringField("s2_content_xml");
    public static final StringField XML_CONTENT_STRIPPED = new StringField("s2_content_xml_stripped");
}
