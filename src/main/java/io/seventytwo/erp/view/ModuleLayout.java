package io.seventytwo.erp.view;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;

import java.util.Objects;

@ParentLayout(ApplicationLayout.class)
public class ModuleLayout extends Div implements RouterLayout {

    private final Div container = new Div();

    public ModuleLayout() {
        add(container);
    }

    @Override
    public void showRouterLayoutContent(HasElement child) {
        if (child != null) {
            container.getElement().appendChild(Objects.requireNonNull(child.getElement()));
        }
    }
}
