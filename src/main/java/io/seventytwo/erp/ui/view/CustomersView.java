package io.seventytwo.erp.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import io.seventytwo.db.tables.records.CustomerRecord;
import io.seventytwo.erp.ui.ApplicationLayout;
import io.seventytwo.erp.ui.editor.CustomerEditor;
import io.seventytwo.erp.ui.editor.CustomerEditorDialog;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static io.seventytwo.erp.util.JooqUtil.createOrderBy;
import static io.seventytwo.erp.util.JooqUtil.getPropertyName;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.jooq.impl.DSL.lower;

@VaadinSessionScope
@PageTitle("ERP - Customers")
@Route(value = "customers", layout = ApplicationLayout.class)
public class CustomersView extends VerticalLayout {

    private final DSLContext dsl;
    private final CustomerEditorDialog customerEditorDialog;

    private ConfigurableFilterDataProvider<CustomerRecord, Void, Condition> filterDataProvider;

    public CustomersView(DSLContext dsl, CustomerEditorDialog customerEditorDialog) {
        this.dsl = dsl;
        this.customerEditorDialog = customerEditorDialog;

        add(new H1("Customers"));

        add(createFilter());

        add(new RouterLink("New", CustomerView.class));

        add(createGrid());

        add(new RouterLink("Back", MainView.class));
    }

    private TextField createFilter() {
        TextField filter = new TextField("Filter");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.focus();

        filter.addValueChangeListener(event -> {
            if (isBlank(filter.getValue())) {
                filterDataProvider.setFilter(null);
            } else {
                if (isNumeric(filter.getValue())) {
                    filterDataProvider.setFilter(CUSTOMER.ID.eq(Integer.parseInt(filter.getValue())));
                } else {
                    filterDataProvider.setFilter(lower(CUSTOMER.FIRST_NAME).like("%" + filter.getValue().toLowerCase() + "%")
                            .or(lower(CUSTOMER.LAST_NAME).like("%" + filter.getValue().toLowerCase() + "%"))
                            .or(lower(CUSTOMER.EMAIL).like("%" + filter.getValue().toLowerCase() + "%")));
                }
            }
        });

        return filter;
    }

    private Grid<CustomerRecord> createGrid() {
        Grid<CustomerRecord> grid = new Grid<>(CustomerRecord.class);
        grid.setPageSize(20);

        grid.setColumns(getPropertyName(CUSTOMER.ID), getPropertyName(CUSTOMER.FIRST_NAME), getPropertyName(CUSTOMER.LAST_NAME), getPropertyName(CUSTOMER.EMAIL));

        grid.addColumn(new ComponentRenderer<>(customer -> new RouterLink("Edit", CustomerView.class, customer.getId())))
                .setWidth("100px")
                .setFlexGrow(0)
                .setFrozen(true);

        grid.addColumn(
                new ComponentRenderer<>(customer -> {
                    Button openInDialog = new Button("Edit in Dialog");
                    openInDialog.addClickListener(event -> customerEditorDialog.open(customer));
                    return openInDialog;
                })).setWidth("100px")
                .setFlexGrow(0)
                .setFrozen(true);

        grid.setColumnReorderingAllowed(true);

        filterDataProvider = createDataProvider();
        grid.setDataProvider(filterDataProvider);
        return grid;
    }

    private ConfigurableFilterDataProvider<CustomerRecord, Void, Condition> createDataProvider() {
        CallbackDataProvider<CustomerRecord, Condition> callbackDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    var where = dsl.selectFrom(CUSTOMER).where(DSL.noCondition());
                    if (query.getFilter().isPresent()) {
                        where = where.and(query.getFilter().get());
                    }
                    if (!query.getSortOrders().isEmpty()) {
                        return where.orderBy(createOrderBy(CUSTOMER, query.getSortOrders())).offset(offset).limit(limit).stream();
                    } else {
                        return where.offset(offset).limit(limit).stream();
                    }
                },
                query -> {
                    if (query.getFilter().isPresent()) {
                        return dsl.fetchCount(dsl.selectFrom(CUSTOMER).where(query.getFilter().get()));
                    } else {
                        return dsl.fetchCount(CUSTOMER);
                    }
                }
        );
        return callbackDataProvider.withConfigurableFilter();
    }

}
