package com.semantico.sipp2.solr.fields;

import java.util.Collection;

/**
 * Subset of the field interface, prevents type loss with multivalued fields
 */
public interface MultivaluedField<R> extends Field<Collection<R>> {

}
