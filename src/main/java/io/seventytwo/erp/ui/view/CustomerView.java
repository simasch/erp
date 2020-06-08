package io.seventytwo.erp.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import io.seventytwo.erp.db.tables.records.CustomerRecord;
import io.seventytwo.erp.ui.ApplicationLayout;
import io.seventytwo.erp.ui.editor.CustomerEditor;
import org.jooq.DSLContext;

import static io.seventytwo.erp.db.tables.Customer.CUSTOMER;

@VaadinSessionScope
@PageTitle("ERP - Customer")
@Route(value = "customers/customer", layout = ApplicationLayout.class)
public class CustomerView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final DSLContext dsl;
    private final CustomerEditor customerEditor;

    private CustomerRecord customer;

    public CustomerView(DSLContext dsl, CustomerEditor customerEditor) {
        this.dsl = dsl;
        this.customerEditor = customerEditor;
        this.customerEditor.setCancelAction(() -> UI.getCurrent().navigate(CustomersView.class));

        add(customerEditor);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer customerId) {
        if (customerId == null) {
            customer = dsl.newRecord(CUSTOMER);
        } else {
            customer = dsl.selectFrom(CUSTOMER).where(CUSTOMER.ID.eq(customerId)).fetchOne();

            UI.getCurrent().getPage().setTitle("Customer " + customerId);
        }
        customerEditor.setCustomer(customer);
    }
}
