package io.seventytwo.erp.ui.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import io.seventytwo.erp.ui.ApplicationLayout;

@VaadinSessionScope
@PageTitle("ERP")
@Route(value = "", layout = ApplicationLayout.class)
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("ERP"));
        add(new RouterLink("Customers", CustomersView.class));
    }

}
