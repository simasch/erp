package io.seventytwo.erp.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import io.seventytwo.db.tables.records.CustomerRecord;
import org.jooq.DSLContext;

import java.util.List;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static io.seventytwo.erp.util.PropertyUtil.getPropertyName;

@PageTitle("ERP")
@Route("customers")
public class CustomersView extends VerticalLayout implements AfterNavigationObserver {

    private final DSLContext context;
    private Grid<CustomerRecord> grid;

    public CustomersView(DSLContext context) {
        this.context = context;

        add(new H1("Customers"));

        grid = new Grid<>(CustomerRecord.class);
        grid.setColumns(getPropertyName(CUSTOMER.ID), getPropertyName(CUSTOMER.FIRST_NAME),
                getPropertyName(CUSTOMER.LAST_NAME), getPropertyName(CUSTOMER.EMAIL));

        Grid.Column<CustomerRecord> edit = grid.addColumn(
                new ComponentRenderer<>(customer -> new RouterLink("Edit", CustomerView.class, customer.getId())));
        edit.setFrozen(true);

        grid.setColumnReorderingAllowed(true);

        add(grid);

        add(new RouterLink("Back", MainView.class));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        List<CustomerRecord> customers = context.selectFrom(CUSTOMER).fetch();
        grid.setItems(customers);
    }
}
