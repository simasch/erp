package io.seventytwo.erp.view;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Objects;

@Theme(Lumo.class)
public class ApplicationLayout extends Div implements RouterLayout {

    private final Div container = new Div();

    public ApplicationLayout() {
        add(container);
    }

    @Override
    public void showRouterLayoutContent(HasElement child) {
        if (child != null) {
            container.getElement().appendChild(Objects.requireNonNull(child.getElement()));
        }
    }

}
