#Rigel

Rigel is a client library for Apache Solr and framework for mapping results to domain objects.

Major Features:
- A type-safe literate API for performing queries
- Maps result documents onto user defined objects
- A mechanism for defining document post processing

##In Development!

Rigel isnt finished yet, use at your own risk!
Still to do:

- Content Repository
    - [x] Id query
    - [x] Multiple Id query
    - [x] All query
    - [x] Group query
    - [x] Join query
    - [ ] FL methods
    - [ ] distinct values?
- [ ] more filters e.g. startsWith
- Search
    - [x] Field Facets
    - [x] Range Facets
    - [ ] Highlighting
    - [ ] Spellcheck
    - [ ] Results object
- [ ] Asynchronous Content repository

##How to Install

Rigel is still in development and hasn't been published to a public maven repository yet.
You'll have to build from source.

```xml
<dependency>
    <groupId>com.semantico</groupId>
    <artifactId>rigel</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

##How to use

####1. Define your fields

```java
StringField ID = new StringField("id");
StringField TYPE = new StringField("type");
StringField TITLE = new StringField("dc_title");
StringField AUTHOR = new StringField("dc_creator");
DateField DATE = new DateField("dc_issued");
IntegerField SCENE_COUNT = new IntegerField("scene_count");
```

####2. Define your models

```java
public class Play extends ContentItem {

    public static class Schema extends ContentItem.Schema<Play> {

        public FieldKey<?, String> title;
        public FieldKey<?, String> type;
        public FieldKey<?, Author> author;
        public FieldKey<?, Date> date;
        public FieldKey<?, Integer> sceneCount;

        public Schema() {
            super(ID);
            type = field(TYPE).build();
            title = field(TITLE).build();
            author = field(AUTHOR).transform(Author.parse()).build();
            date = field(DATE).build();
            sceneCount = field(SCENE_COUNT).build();

            filter(TYPE.isEqualTo("play"));
        }

        @Override
        public Play create(Map<DataKey<?>, ? super Object> data) {
            return new Play(this, data);
        }
    }

    private final Schema schema;

    public Play(Schema schema, Map<DataKey<?>, ? super Object> data) {
        super(schema, data);
        this.schema = schema;
    }

    public String getTitle() {
        return get(schema.title);
    }

    public Author getAuthor() {
        return get(schema.author);
    }

    public Date getDate() {
        return get(schema.date);
    }

    public Integer getSceneCount() {
        return get(schema.sceneCount);
    }
```

####3. Initialize a RigelContext

```java
Play.Schema playSchema = new Play.Schema();

RigelContext rigel = RigelContext.builder()
    .registerSchemas(playSchema)
    .build();
```


####4. Start Querying

```java
ContentRepository<Play> plays = rigel.getContentRepository(playSchema);

List<Play> newest = plays.all()
    .filterBy(AUTHOR.isEqualTo("Edd"))
    .orderBy(DATE, desc)
    .limit(20)
    .get();

ListMultimap<String, Play> groups = plays.groupBy(SCENE_COUNT)
    .limitResultsPerGroup(1)
    .get();

List<Play> results = plays.joinFrom(CHILD_IDS)
    .filterBy(ID.isEqualTo("collection1"))
    .to(ID)
    .get();

Optional<Play> op = plays.id("play1").get();
```
