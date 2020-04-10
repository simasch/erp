package io.seventytwo.erp.view;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.server.PWA;

@PWA(name = "ERP", shortName = "ERP")
@BodySize(height = "100vh", width = "100%")
public class AppShellConfig implements AppShellConfigurator {
}
