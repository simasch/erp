package io.seventytwo.erp.util;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Table;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.seventytwo.erp.util.StringConverter.camelToSnakeCase;
import static io.seventytwo.erp.util.StringConverter.snakeToCamelCase;

public class JooqUtil {

    private JooqUtil() {
    }

    public static String getPropertyName(Field<?> field) {
        Objects.requireNonNull(field);

        String fieldName = field.getName();
        return snakeToCamelCase(fieldName);
    }

    public static OrderField<?>[] createOrderBy(Table<?> table, List<QuerySortOrder> sortOrders) {
        return sortOrders.stream().map(sortOrder -> {
            Field<?> field = table.field(camelToSnakeCase(sortOrder.getSorted()));
            return sortOrder.getDirection() == SortDirection.ASCENDING ? field.asc() : field.desc();
        }).toArray(OrderField[]::new);
    }

}
