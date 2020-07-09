package io.seventytwo.erp;

import io.seventytwo.erp.db.tables.pojos.Customer;
import io.seventytwo.erp.db.tables.pojos.Phone;
import io.seventytwo.erp.db.tables.records.CustomerRecord;
import io.seventytwo.erp.db.tables.records.PhoneRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static io.seventytwo.erp.db.tables.Customer.CUSTOMER;
import static io.seventytwo.erp.db.tables.Phone.PHONE;

@SpringBootTest
public class JooqTest {

    @Autowired
    private DSLContext dsl;

    @Test
    public void groups() {
        Map<CustomerRecord, Result<PhoneRecord>> result = dsl.select().from(CUSTOMER).join(PHONE).on(PHONE.CUSTOMER_ID.eq(CUSTOMER.ID)).fetchGroups(CUSTOMER, PHONE);

        result.forEach((key, value) -> {
            System.out.println("Customer " + key.getFirstName() + " " + key.getLastName());
            value.forEach(phoneRecord -> {
                System.out.println("  Phone " + phoneRecord.getNumber());
            });
        });
    }

    @Test
    public void groupsRaw() {
        Map<CustomerRecord, Result<PhoneRecord>> result = dsl.fetch("select * from CUSTOMER join PHONE on PHONE.CUSTOMER_ID = CUSTOMER.ID").intoGroups(CUSTOMER, PHONE);

        result.forEach((key, value) -> {
            System.out.println("Customer " + key.getFirstName() + " " + key.getLastName());
            value.forEach(phoneRecord -> {
                System.out.println("  Phone " + phoneRecord.getNumber());
            });
        });
    }

    @Test
    public void groupsDtoRaw() {
        Map<Customer, List<Phone>> result = dsl.fetch("select * from CUSTOMER join PHONE on PHONE.CUSTOMER_ID = CUSTOMER.ID")
                .intoGroups(record -> record.into(CUSTOMER).into(Customer.class),
                        record -> record.into(PHONE).into(Phone.class));

        result.forEach((key, value) -> {
            System.out.println("Customer " + key.getFirstName() + " " + key.getLastName());
            value.forEach(phoneRecord -> {
                System.out.println("  Phone " + phoneRecord.getNumber());
            });
        });
    }

}
