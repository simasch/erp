package io.seventytwo.erp.data;

import io.seventytwo.db.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Component
public class DataProducer {

    private final DSLContext context;

    public DataProducer(DSLContext context) {
        this.context = context;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void createCustomers() {
        for (int i = 0; i < 1000; i++) {
            String firstName = generateName();
            String lastName = generateName();
            CustomerRecord customer = new CustomerRecord(null, firstName + "." + lastName + "@foo.com", lastName, firstName);
            context.attach(customer);
            customer.store();
        }
    }

    public String generateName() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
