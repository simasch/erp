package io.seventytwo.erp.util;

import io.seventytwo.db.tables.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JooqUtilTest {

    @Test
    void getPropertyName() {
        String propertyName = JooqUtil.getPropertyName(Customer.CUSTOMER.FIRST_NAME);

        assertEquals("firstName", propertyName);
    }
}
