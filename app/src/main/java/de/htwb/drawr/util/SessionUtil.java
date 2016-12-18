package de.htwb.drawr.util;

import java.util.regex.Pattern;

/**
 * Created by laokoon on 12/14/16.
 */
public final class SessionUtil {

    public final static Pattern ULID_PATTERN = Pattern.compile("[A-Z0-9]*");
    public static final int ULID_LENGTH = 26;

    private SessionUtil(){}

    /**
     * Validates a String if it matches the Session Id Pattern
     * @param ulid the string to validate
     * @return <code>true</code> when ulid matches Session Id pattern, otherwise returns <code>false</code>
     */
    public static final boolean validateUlid(String ulid) {
        if(ulid != null && ulid.length() == ULID_LENGTH) {
            return ULID_PATTERN.matcher(ulid).matches();
        }
        return false;
    }

}
