package io.seventytwo.erp.data;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import io.seventytwo.db.tables.records.CustomerRecord;
import io.seventytwo.db.tables.records.PhoneRecord;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static io.seventytwo.db.tables.Customer.CUSTOMER;
import static io.seventytwo.db.tables.Phone.PHONE;

@Component
public class DataProducer {

    private final Fairy fairy = Fairy.create();

    private final DSLContext dsl;

    public DataProducer(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void createCustomers() {
        for (int i = 0; i < 200; i++) {
            Person person = fairy.person();

            CustomerRecord customer = dsl.newRecord(CUSTOMER);
            try {
                customer.setFirstName(person.getFirstName());
                customer.setLastName(person.getLastName());
                customer.setEmail(person.getEmail());
                customer.store();
            } catch (DuplicateKeyException e) {
                customer.setEmail("1" + customer.getEmail());
                customer.store();
            }

            PhoneRecord mobile = dsl.newRecord(PHONE);
            mobile.setNumber(person.getTelephoneNumber());
            mobile.setType("MOBILE");
            mobile.setCustomerId(customer.getId());
            mobile.store();

            PhoneRecord office = dsl.newRecord(PHONE);
            office.setNumber("0800-123-456");
            office.setType("OFFICE");
            office.setCustomerId(customer.getId());
            office.store();
        }
    }

}
