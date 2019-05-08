package io.seventytwo.erp.data;

import io.seventytwo.db.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataProducer {

    private final DSLContext context;

    public DataProducer(DSLContext context) {
        this.context = context;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void createCustomers() {
        CustomerRecord justus = new CustomerRecord(null, "justus@jonas.com", "Jonas", "Justus");
        context.attach(justus);
        justus.store();

        CustomerRecord peter = new CustomerRecord(null, "peter@shaw.com", "Shaw", "Peter");
        context.attach(peter);
        peter.store();

        CustomerRecord bob = new CustomerRecord(null, "bob@andrews.com", "Andrews", "Bob");
        context.attach(bob);
        bob.store();
    }
}
