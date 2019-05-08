package io.seventytwo.erp.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import static io.seventytwo.erp.security.SecurityUtils.isUserLoggedIn;

@PageTitle("Login")
@Route("login")
public class LoginView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {

    private LoginOverlay loginOverlay = new LoginOverlay();

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("ERP");
        i18n.getHeader().setDescription("user + user");
        i18n.setAdditionalInformation(null);
        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit("Sign in");
        i18n.getForm().setTitle("Sign in");
        i18n.getForm().setUsername("User");
        i18n.getForm().setPassword("Password");

        loginOverlay.setI18n(i18n);
        loginOverlay.getElement().setAttribute("no-forgot-password", true);
        loginOverlay.setAction("login");
        loginOverlay.setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (isUserLoggedIn()) {
            UI.getCurrent().navigate("/");
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        loginOverlay.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

}
