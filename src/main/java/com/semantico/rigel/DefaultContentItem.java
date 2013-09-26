package com.semantico.sipp2.solr;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.semantico.sipp2.solr.fields.DublinCore;
import com.semantico.sipp2.solr.fields.SimpleField;
import com.semantico.sipp2.solr.fields.Sipp2;
import com.semantico.sipp2.transformation.xslt.XsltResolver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.semantico.sipp2.solr.transformers.JsoupFieldTransformer.*;

/**
 * Mimics the old implementation of content item to make it easier to move over to the new one.
 *
 * Things to look out for when switching to this class:
 * <ul>
 * <li> Jsoup is used instead of the HTMLStripper, this affects the getTitleStripped() method </li>
 * <li> getFieldValue & related helper methods have been removed, subclasses will have to switch to using schemas </li>
 * </ul>
 * @deprecated The new implementation of content item should be used in preference to this one, everything except id and type are project specific
 */
@Deprecated
public class DefaultContentItem extends ContentItem {

    private static final Logger LOG = LoggerFactory.getLogger(ContentItem.class);

    public static abstract class Schema<T extends DefaultContentItem> extends ContentItem.Schema<T> {

        public final FieldKey<?, String> type = field(DublinCore.TYPE).build();
        public final FieldKey<?, String> title = field(DublinCore.TITLE).build();
        public final FieldKey<?, String> description = field(DublinCore.DESCRIPTION).build();
        public final FieldKey<?, Collection<String>> creator = field(DublinCore.CREATOR.multivalued()).build();
        public final FieldKey<?, Collection<String>> contributor = field(DublinCore.CONTRIBUTOR.multivalued()).build();
        public final FieldKey<?, Date> modified = field(new SimpleField<Date>("dc_modified")).build();
        public final FieldKey<?, String> sortable_title = field(new SimpleField<String>("dc_title_sortable")).build();
        public final FieldKey<?, String> content = field(Sipp2.XML_CONTENT).build();
        public final FieldKey<?, String> parent = field(Sipp2.PARENT).build();
        public final FieldKey<?, String> parent_title = field(new SimpleField<String>("s2_parent_title")).build();
        public final FieldKey<?, String> content_stripped = field(Sipp2.XML_CONTENT_STRIPPED).build();
        public final FieldKey<?, String> xml_id = field(new SimpleField<String>("s2_xml_id")).build();
        public final FieldKey<?, String> sipp2_type = field(Sipp2.TYPE).build();
        public final FieldKey<?, String> title_stripped = field(DublinCore.TITLE).transform(
                jsoupClean(Whitelist.simpleText())).build();

    }

    public static final Schema<DefaultContentItem> SCHEMA = new Schema<DefaultContentItem>() {

        public DefaultContentItem create(Map<FieldKey<?, ?>, ?> data) {
            return new DefaultContentItem(data);
        }
    };

    public DefaultContentItem(Map<FieldKey<?, ?>, ?> data) {
        super(data);
    }

    public URI getURI() {
        try {
            return new URI(String.format("sipp2:%s", getId()));
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public String getTitle() {
        return get(SCHEMA.title);
    }

    public String getSortableTitle() {
        return get(SCHEMA.sortable_title);
    }

    public String getTitleStripped() {
        return get(SCHEMA.title_stripped);
    }

    public String getXmlContent() {
        return get(SCHEMA.content);
    }

    public String getXmlContentStripped() {
        return get(SCHEMA.content_stripped);
    }

    public String getXmlId() {
        return get(SCHEMA.xml_id);
    }

    public String getSipp2Type() {
        return get(SCHEMA.sipp2_type);
    }

    public String getType() {
        return get(SCHEMA.type);
    }

    public Date getModified() {
        return get(SCHEMA.modified);
    }

    public String getParentId() {
        return get(SCHEMA.parent);
    }

    public String getParentTitle() {
        return get(SCHEMA.parent_title);
    }

    public boolean hasParent() {
        return getParentId() != null;
    }

    public String getDescription() {
        return get(SCHEMA.description);
    }

    public List<String> getContributor() {
        return Lists.newArrayList(Objects.firstNonNull(get(SCHEMA.contributor), ImmutableList.<String>of()));
    }

    public List<String> getCreator() {
        return Lists.newArrayList(Objects.firstNonNull(get(SCHEMA.creator), ImmutableList.<String>of()));
    }

    public String toString() {
        return Objects.toStringHelper(this).add("s2_id", getId()).toString();
    }

    public URI getTransformationTo(String type) throws IOException {
        return XsltResolver.getTransformation(getType(), type);
    }

}
