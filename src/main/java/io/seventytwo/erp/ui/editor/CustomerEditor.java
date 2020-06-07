package io.seventytwo.erp.ui.editor;

import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.seventytwo.db.tables.records.CustomerRecord;
import io.seventytwo.db.tables.records.PhoneRecord;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static io.seventytwo.db.tables.Phone.PHONE;
import static io.seventytwo.erp.util.JooqUtil.getPropertyName;

@UIScope
@SpringComponent
public class CustomerEditor extends Div {

    private final DSLContext dsl;
    private final TransactionTemplate transactionTemplate;

    private Div cancel;

    private CustomerRecord customer;

    private final BeanValidationBinder<CustomerRecord> customerBinder;

    private Grid<PhoneRecord> phoneGrid;
    private DataProvider<PhoneRecord, Void> phoneDataProvider;
    private Editor<PhoneRecord> phoneEditor;

    public CustomerEditor(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;
        this.transactionTemplate = transactionTemplate;
        this.cancel = new Div();

        customerBinder = new BeanValidationBinder<>(CustomerRecord.class);

        add(createCustomerForm());
        add(new H2("Phone Numbers"));
        add(createPhoneGrid());
        add(createGridButtons());
        add(createActionBar());
    }

    public void setCustomer(CustomerRecord customer) {
        this.customer = customer;
        this.customerBinder.setBean(customer);
    }

    public void setCancel(Component cancelComponent) {
        this.cancel.removeAll();
        this.cancel.add(cancelComponent);
    }

    private FormLayout createCustomerForm() {
        FormLayout customerForm = new FormLayout();
        add(customerForm);

        TextField id = new TextField("ID");
        id.setWidthFull();
        id.setId(CUSTOMER.ID.getName());
        id.setReadOnly(true);
        customerForm.add(id);

        customerBinder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Must be a number"))
                .bind(getPropertyName(CUSTOMER.ID));

        customerForm.add(new Span());

        TextField firstName = new TextField("First Name");
        firstName.setWidthFull();
        firstName.setId(CUSTOMER.FIRST_NAME.getName());
        customerForm.add(firstName);

        customerBinder.forField(firstName)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "First name must contain at least three characters")
                .bind(getPropertyName(CUSTOMER.FIRST_NAME));

        TextField lastName = new TextField("Last Name");
        lastName.setWidthFull();
        lastName.setId(CUSTOMER.LAST_NAME.getName());
        customerForm.add(lastName);

        customerBinder.forField(lastName)
                .asRequired()
                .withValidator(n -> n.length() >= 3, "Last name must contain at least three characters")
                .bind(getPropertyName(CUSTOMER.LAST_NAME));

        TextField email = new TextField("E-Mail");
        email.setWidthFull();
        email.setId(CUSTOMER.EMAIL.getName());
        customerForm.add(email);

        customerBinder.forField(email)
                .asRequired()
                .withValidator(new EmailValidator("This is not a valid e-mail address"))
                .bind(getPropertyName(CUSTOMER.EMAIL));

        return customerForm;
    }

    private Grid<PhoneRecord> createPhoneGrid() {
        phoneGrid = new Grid<>();
        phoneGrid.setDataProvider(createDataProvider());

        Grid.Column<PhoneRecord> numberColumn = phoneGrid.addColumn(PhoneRecord::getNumber);
        Grid.Column<PhoneRecord> typeColumn = phoneGrid.addColumn(PhoneRecord::getType);

        Binder<PhoneRecord> phoneBinder = new Binder<>(PhoneRecord.class);
        phoneEditor = phoneGrid.getEditor();
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

        phoneEditor.addSaveListener(event -> {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                PhoneRecord phone = event.getItem();
                dsl.attach(phone);
                phone.store();

                phoneEditor.cancel();
                phoneGrid.getDataProvider().refreshAll();
            });
        });

        phoneGrid.getElement()
                .addEventListener("keyup", event -> phoneEditor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        return phoneGrid;
    }

    private DataProvider<PhoneRecord, Void> createDataProvider() {
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

    private HorizontalLayout createGridButtons() {
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

        return new HorizontalLayout(addPhone, deletePhone);
    }

    private HorizontalLayout createActionBar() {
        HorizontalLayout horizontalLayout = new HorizontalLayout(createSaveButton(), cancel);
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        return horizontalLayout;
    }

    private Button createSaveButton() {
        Button button = new Button("Save");
        button.addClickListener(event ->
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    BinderValidationStatus<CustomerRecord> validate = customerBinder.validate();
                    if (validate.isOk()) {
                        customer.store();

                        Notification.show("Customer saved", 2000, Notification.Position.TOP_END);
                    }
                }));
        return button;
    }

}
