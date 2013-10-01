package com.semantico.rigel;

import com.semantico.rigel.fields.types.*;

public interface TestFields {

    public static final StringField TITLE = new StringField("title");
    public static final DateField DATE = new DateField("date");
    public static final IntegerField SCENE_COUNT = new IntegerField("scene_count");
    public static final LongField REALLY_BIG_NUMBER = new LongField("big_num");

}
