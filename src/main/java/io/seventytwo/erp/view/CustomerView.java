package io.seventytwo.erp.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import io.seventytwo.db.tables.records.CustomerRecord;
import io.seventytwo.db.tables.records.PhoneRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.transaction.support.TransactionTemplate;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static io.seventytwo.db.tables.Phone.PHONE;
import static io.seventytwo.erp.util.JooqUtil.getPropertyName;

@VaadinSessionScope
@PageTitle("ERP - Customer")
@Route(value = "customers/customer", layout = ModuleLayout.class)
public class CustomerView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final DSLContext dsl;

    private BeanValidationBinder<CustomerRecord> binder;
    private final Grid<PhoneRecord> phoneGrid;

    private CustomerRecord customer;
    private Result<PhoneRecord> phones;

    public CustomerView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;

        FormLayout formLayout = new FormLayout();

        binder = new BeanValidationBinder<>(CustomerRecord.class);

        TextField id = new TextField("ID");
        id.setWidthFull();
        id.setId(CUSTOMER.ID.getName());
        id.setReadOnly(true);
        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .bind(getPropertyName(CUSTOMER.ID));

        formLayout.add(id);

        formLayout.add(new Span());

        TextField firstName = new TextField("First Name");
        firstName.setWidthFull();
        firstName.setId(CUSTOMER.FIRST_NAME.getName());
        binder.forField(firstName)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "First name must contain at least three characters")
                .bind(getPropertyName(CUSTOMER.FIRST_NAME));

        formLayout.add(firstName);

        TextField lastName = new TextField("Last Name");
        lastName.setWidthFull();
        lastName.setId(CUSTOMER.LAST_NAME.getName());
        binder.forField(lastName)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "Last name must contain at least three characters")
                .bind(getPropertyName(CUSTOMER.LAST_NAME));

        formLayout.add(lastName);

        TextField email = new TextField("E-Mail");
        email.setWidthFull();
        email.setId(CUSTOMER.EMAIL.getName());
        binder.forField(email)
                .asRequired()
                .withValidator(new EmailValidator("This is not a valid e-mail address"))
                .bind(getPropertyName(CUSTOMER.EMAIL));

        formLayout.add(email);

        add(formLayout);

        add(new H2("Phone numbers"));

        phoneGrid = new Grid<>(PhoneRecord.class);
        phoneGrid.setColumns(getPropertyName(PHONE.NUMBER), getPropertyName(PHONE.TYPE));

        add(phoneGrid);

        Button button = new Button("Save");
        button.addClickListener(event ->
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    BinderValidationStatus<CustomerRecord> validate = binder.validate();
                    if (validate.isOk()) {
                        customer.store();

                        Notification.show("Customer saved", 2000, Notification.Position.TOP_END);
                    }
                }));

        add(new HorizontalLayout(button, new Div(new RouterLink("Back", CustomersView.class))));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer customerId) {
        if (customerId == null) {
            customer = dsl.newRecord(CUSTOMER);
        } else {
            customer = dsl.selectFrom(CUSTOMER).where(CUSTOMER.ID.eq(customerId)).fetchOne();
            phones = dsl.selectFrom(PHONE).where(PHONE.CUSTOMER_ID.eq(customerId)).fetch();
            phoneGrid.setItems(phones);

            UI.getCurrent().getPage().setTitle("Customer " + customerId);
        }
        binder.setBean(customer);
    }
}
