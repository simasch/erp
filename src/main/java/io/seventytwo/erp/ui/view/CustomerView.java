package io.seventytwo.erp.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import io.seventytwo.db.tables.records.CustomerRecord;
import io.seventytwo.db.tables.records.PhoneRecord;
import io.seventytwo.erp.ui.ApplicationLayout;
import io.seventytwo.erp.ui.editor.CustomerEditor;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static io.seventytwo.db.tables.Phone.PHONE;
import static io.seventytwo.erp.util.JooqUtil.getPropertyName;

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
