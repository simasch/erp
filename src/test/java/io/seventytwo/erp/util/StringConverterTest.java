package io.seventytwo.erp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringConverterTest {

    @Test
    void snakeToCamelCase() {
        String camelCase = StringConverter.snakeToCamelCase("CUSTOMER_FIRST_NAME");

        assertEquals("customerFirstName", camelCase);
    }

    @Test
    void camelToSnakeCase() {
        String camelCase = StringConverter.camelToSnakeCase("customerFirstName");

        assertEquals("CUSTOMER_FIRST_NAME", camelCase);
    }
}
