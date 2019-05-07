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
        CustomerRecord customer = new CustomerRecord(null, "simon@martinelli.ch", "Martinelli", "Simon");
        context.attach(customer);
        customer.store();
    }
}
