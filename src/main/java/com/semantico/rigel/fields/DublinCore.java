package com.semantico.rigel.fields;

import com.semantico.rigel.fields.types.*;

/**
 * Properties of the Dublin Core elements namespace represented as solr Field abstractions
 */
public final class DublinCore {

    public static final StringField CONTRIBUTOR = new StringField("dc_contributor");
    public static final StringField COVERAGE = new StringField("dc_coverage");
    public static final StringField CREATOR = new StringField("dc_creator");
    public static final DateField DATE = new DateField("dc_date");
    public static final StringField DESCRIPTION = new StringField("dc_description");
    public static final StringField FORMAT = new StringField("dc_format");
    public static final StringField IDENTIFIER = new StringField("dc_identifier");
    public static final StringField LANGUAGE = new StringField("dc_language");
    public static final StringField PUBLISHER = new StringField("dc_publisher");
    public static final StringField RELATION = new StringField("dc_relation");
    public static final StringField RIGHTS = new StringField("dc_rights");
    public static final StringField SOURCE = new StringField("dc_source");
    public static final StringField SUBJECT = new StringField("dc_subject");
    public static final StringField TITLE = new StringField("dc_title");
    public static final StringField TYPE = new StringField("dc_type");

}
