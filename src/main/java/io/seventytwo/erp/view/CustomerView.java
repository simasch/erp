package io.seventytwo.erp.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import io.seventytwo.db.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import static io.seventytwo.db.tables.Customer.CUSTOMER;

@Route("customers/customer")
public class CustomerView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final DSLContext context;
    private final TransactionTemplate transactionTemplate;

    private TextField id;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private BeanValidationBinder<CustomerRecord> binder;

    private CustomerRecord customer;

    public CustomerView(DSLContext context, TransactionTemplate transactionTemplate) {
        this.context = context;
        this.transactionTemplate = transactionTemplate;

        initUi();
    }

    private void initUi() {
        binder = new BeanValidationBinder<>(CustomerRecord.class);

        id = new TextField("ID");
        id.setReadOnly(true);
        binder.forField(id)
                .withConverter(new StringToIntegerConverter("Must enter a number"))
                .bind(CustomerRecord::getId, null);

        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("E-Mail");

        add(new FormLayout(id, new Span(), firstName, lastName, email));

        binder.bindInstanceFields(this);

        Button button = new Button("Save");
        button.addClickListener(event ->
                transactionTemplate.execute(transactionStatus -> {
                    context.attach(customer);
                    customer.store();

                    Notification.show("Customer saved", 2000, Notification.Position.TOP_END);

                    return null;
                }));

        add(new HorizontalLayout(button, new RouterLink("Back", CustomersView.class)));
    }

    @Override
    public void setParameter(BeforeEvent event, Integer customerId) {
        customer = context.selectFrom(CUSTOMER).where(CUSTOMER.ID.eq(customerId)).fetchOne();
        binder.setBean(customer);
    }
}
