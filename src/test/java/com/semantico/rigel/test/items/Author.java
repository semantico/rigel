package com.semantico.rigel.test.items;

import com.google.common.base.Function;

public class Author {

    public static Function<String, Author> parse() {
        return new Function<String, Author>() {
            @Override
            public Author apply(String input) {
                return new Author(input);
            }
        };
    }

    private final String givenName;
    private final String familyName;

    public Author(String datas) {
        String[] parts = datas.split(",");
        familyName = parts[0].trim();
        givenName = parts[1].trim();
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Author)) {
            return false;
        }
        Author other = (Author) obj;
        return other.givenName.equals(this.givenName)
            && other.familyName.equals(this.familyName);
    }
}
