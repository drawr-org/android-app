package de.htwb.drawr.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by laokoon on 12/14/16.
 */
public class SessionUtilTest {

    @Test
    public void testValidateNormal() {
        String validUlid = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
        assertEquals("Ulid should be valid", SessionUtil.validateUlid(validUlid), true);
    }

    @Test
    public void testValidateInvalid() {
        String validUlid = "Hello World!";
        assertEquals("Ulid should be invalid", SessionUtil.validateUlid(validUlid), false);
    }

    @Test
    public void testValidateNull() {
        String validUlid = null;
        assertEquals("Ulid should be invalid", SessionUtil.validateUlid(validUlid), false);
    }
}
