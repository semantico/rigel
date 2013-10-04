package com.semantico.rigel;

import com.semantico.rigel.fields.types.*;

public interface TestFields {

    public static final StringField ID = new StringField("id");
    public static final StringField TYPE = new StringField("type");
    public static final StringField TITLE = new StringField("dc_title");
    public static final StringField AUTHOR = new StringField("dc_creator");
    public static final DateField DATE = new DateField("dc_issued");
    public static final IntegerField SCENE_COUNT = new IntegerField("scene_count");
    public static final IntegerField CHAPTER_COUNT = new IntegerField("chapter_count");
    public static final LongField REALLY_BIG_NUMBER = new LongField("big_num");
    public static final StringField CHILD_IDS = new StringField("child_ids");

}
