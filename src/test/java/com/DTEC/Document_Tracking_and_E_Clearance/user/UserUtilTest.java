package com.DTEC.Document_Tracking_and_E_Clearance.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserUtilTest {

    @Test
    void shouldReturnFalse() {
        Assertions.assertFalse(UserUtil.validateContactNumber("09097412"));
        Assertions.assertFalse(UserUtil.validateContactNumber("0909090909"));
        Assertions.assertFalse(UserUtil.validateContactNumber("+6309090909"));
        Assertions.assertFalse(UserUtil.validateContactNumber("9090909"));
    }

    @Test
    void shouldReturnTrue() {
        Assertions.assertTrue(UserUtil.validateContactNumber("09090909090"));
        Assertions.assertTrue(UserUtil.validateContactNumber("+639090909099"));
    }

    @Test
    void shouldErrorDueToWrongGmail() {
        Assertions.assertFalse(UserUtil.validateGmail("abc@yahoo.com"));
    }

    @Test
    void shouldErrorDueToUpperCaseGOfGmail() {
        Assertions.assertFalse(UserUtil.validateGmail("abc@Gmail.com"));
    }

    @Test
    void shouldBeValid() {
        Assertions.assertTrue(UserUtil.validateGmail("abc@gmail.com"));
    }

    @Test
    void shouldRemoveTheWhiteSpaceOfString() {
        String n1 = " 09090909090 ";
        String n2 = "09090909090";
        Assertions.assertEquals(UserUtil.extractContactNumber(n1), n2);
    }
}
