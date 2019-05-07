package io.seventytwo.erp.util;

import com.google.common.base.CaseFormat;
import org.jooq.Field;
import org.jooq.Table;

public class PropertyUtil {

    private PropertyUtil() {
    }

    public static String getPropertyName(Field<?> field) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName());
    }

    private static String getFieldName(String propertyName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, propertyName);
    }

    public static Field<?> getField(Table table, String propertyName) {
        String fieldName = getFieldName(propertyName);
        return (Field<?>) table.field(fieldName);
    }
}
