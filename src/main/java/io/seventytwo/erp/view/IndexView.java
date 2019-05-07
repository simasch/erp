package io.seventytwo.erp.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

@Route("")
public class IndexView extends Div {

    public IndexView() {
        add(new H1("Hello World"));
    }
}
