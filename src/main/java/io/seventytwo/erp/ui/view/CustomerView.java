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
    private final TransactionTemplate transactionTemplate;

    private final BeanValidationBinder<CustomerRecord> customerBinder;

    private Grid<PhoneRecord> phoneGrid;
    private DataProvider<PhoneRecord, Void> phoneDataProvider;

    private CustomerRecord customer;

    public CustomerView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;
        this.transactionTemplate = transactionTemplate;

        FormLayout formLayout = new FormLayout();

        customerBinder = new BeanValidationBinder<>(CustomerRecord.class);

        TextField id = new TextField("ID");
        id.setWidthFull();
        id.setId(CUSTOMER.ID.getName());
        id.setReadOnly(true);
        customerBinder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .bind(getPropertyName(CUSTOMER.ID));

        formLayout.add(id);

        formLayout.add(new Span());

        TextField firstName = new TextField("First Name");
        firstName.setWidthFull();
        firstName.setId(CUSTOMER.FIRST_NAME.getName());
        customerBinder.forField(firstName)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "First name must contain at least three characters")
                .bind(getPropertyName(CUSTOMER.FIRST_NAME));

        formLayout.add(firstName);

        TextField lastName = new TextField("Last Name");
        lastName.setWidthFull();
        lastName.setId(CUSTOMER.LAST_NAME.getName());
        customerBinder.forField(lastName)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "Last name must contain at least three characters")
                .bind(getPropertyName(CUSTOMER.LAST_NAME));

        formLayout.add(lastName);

        TextField email = new TextField("E-Mail");
        email.setWidthFull();
        email.setId(CUSTOMER.EMAIL.getName());
        customerBinder.forField(email)
                .asRequired()
                .withValidator(new EmailValidator("This is not a valid e-mail address"))
                .bind(getPropertyName(CUSTOMER.EMAIL));

        formLayout.add(email);

        add(formLayout);

        add(new H2("Phone Numbers"));

        createPhoneGrid();

        Button button = new Button("Save");
        button.addClickListener(event ->
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    BinderValidationStatus<CustomerRecord> validate = customerBinder.validate();
                    if (validate.isOk()) {
                        customer.store();

                        Notification.show("Customer saved", 2000, Notification.Position.TOP_END);
                    }
                }));

        add(new HorizontalLayout(button, new Div(new RouterLink("Back", CustomersView.class))));
    }

    private void createPhoneGrid() {
        phoneGrid = new Grid<>();
        phoneDataProvider = createDataProvider();
        phoneGrid.setDataProvider(phoneDataProvider);

        Grid.Column<PhoneRecord> numberColumn = phoneGrid.addColumn(PhoneRecord::getNumber);
        Grid.Column<PhoneRecord> typeColumn = phoneGrid.addColumn(PhoneRecord::getType);

        Binder<PhoneRecord> phoneBinder = new Binder<>(PhoneRecord.class);
        Editor<PhoneRecord> phoneEditor = phoneGrid.getEditor();
        phoneEditor.setBinder(phoneBinder);
        phoneEditor.setBuffered(true);

        TextField number = new TextField();
        phoneBinder.bind(number, getPropertyName(PHONE.NUMBER));
        numberColumn.setEditorComponent(number);

        TextField type = new TextField();
        phoneBinder.bind(type, getPropertyName(PHONE.TYPE));
        typeColumn.setEditorComponent(type);

        phoneGrid.addItemClickListener(event -> {
            phoneEditor.save();
            phoneEditor.editItem(event.getItem());
        });

        phoneEditor.addSaveListener(event ->
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    PhoneRecord phone = event.getItem();
                    dsl.attach(phone);
                    phone.store();

                    phoneEditor.cancel();
                    phoneDataProvider.refreshAll();
                }));

        phoneGrid.getElement()
                .addEventListener("keyup", event -> phoneEditor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        add(phoneGrid);

        Button addPhone = new Button("Add Phone Number");
        addPhone.addClickListener(buttonClickEvent -> {
            PhoneRecord newPhone = dsl.newRecord(PHONE);
            newPhone.setCustomerId(customer.getId());
            phoneEditor.editItem(newPhone);
        });
        Button deletePhone = new Button("Delete Phone Number");
        deletePhone.addClickListener(buttonClickEvent -> {
            if (!phoneGrid.getSelectedItems().isEmpty()) {
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    PhoneRecord phone = phoneGrid.getSelectedItems().iterator().next();
                    dsl.attach(phone);
                    phone.delete();

                    phoneEditor.cancel();
                    phoneDataProvider.refreshAll();
                });
            }
        });

        add(new HorizontalLayout(addPhone, deletePhone));
    }

    public DataProvider<PhoneRecord, Void> createDataProvider() {
        return DataProvider.fromCallbacks(
                query -> dsl
                        .selectFrom(PHONE)
                        .where(PHONE.CUSTOMER_ID.eq(customer.getId()))
                        .offset(query.getOffset())
                        .limit(query.getLimit())
                        .stream(),
                query -> dsl.fetchCount(dsl
                        .selectFrom(PHONE)
                        .where(PHONE.CUSTOMER_ID.eq(customer.getId())))

        );
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer customerId) {
        if (customerId == null) {
            customer = dsl.newRecord(CUSTOMER);
        } else {
            customer = dsl.selectFrom(CUSTOMER).where(CUSTOMER.ID.eq(customerId)).fetchOne();

            UI.getCurrent().getPage().setTitle("Customer " + customerId);
        }
        customerBinder.setBean(customer);
    }
}
