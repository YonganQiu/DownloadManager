
package com.elvishew.download.library.naming;

/**
 * Names file as hash code of url.
 */
public class HashCodeFileNameGenerator implements FileNameGenerator {
    @Override
    public String generateName(String uri) {
        return String.valueOf(uri.hashCode());
    }
}
