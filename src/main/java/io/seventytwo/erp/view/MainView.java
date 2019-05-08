package io.seventytwo.erp.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@PageTitle("ERP")
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        initUi();
    }

    private void initUi() {
        add(new H1("ERP"));
        add(new RouterLink("Customers", CustomersView.class));
    }
}
