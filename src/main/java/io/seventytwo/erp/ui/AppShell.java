package io.seventytwo.erp.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.server.PWA;

@PWA(name = "ERP", shortName = "ERP")
@BodySize(height = "100vh", width = "100%")
public class AppShell implements AppShellConfigurator {
}
