# Vaadin and jOOQ Showcase

This project implements a small ERP system (Enterprise Resource Planning) to show how to use 
[Vaadin](https://vaadin.com), [jOOQ](https://www.jooq.org) and [Spring Boot](https://spring.io/projects/spring-boot) 
can be used to build a data centric application.

## Running the Application

Import the project to the IDE of your choosing as a Maven project.

Run the application using `mvn spring-boot:run` or by running the `ErpApplication` class directly from your IDE.

Open http://localhost:7272/ in your browser.

If you want to run the application locally in the production mode, run `mvn spring-boot:run -Pproduction`.

To run Integration Tests, execute `mvn verify -Pit`.

### Live Reload (optional)

With live reload, you can see the results of your code changes immediately.
 
When you edit your Java code and recompile it, the application changes will be automatically reloaded and the browser is refreshed.
This is done by leveraging [Spring Boot Developer Tools](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/html/using-boot-devtools.html). 
To be able to see the changes in the browser tab, the page still needs to be reloaded. 

That can also  be automated via a LiveReload browser extension. 
One such extension for Google Chrome is [LiveReload](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei). 
In Firefox, [LiveReload - Web extension](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/) can be used.

You can find such similar extensions for other major browsers too.
These extensions add an icon to your browser next to the address bar.
To enable the extension, you should click that icon after you opened your application. 

You can find more information at [Live Reload in Spring Boot Applications](https://vaadin.com/docs/flow/workflow/tutorial-spring-boot-live-reload.html) document.

## More Information

- [Vaadin Documentation](https://vaadin.com/docs) 
- [jOOQ](https://www.jooq.org)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Using Vaadin and Spring](https://vaadin.com/docs/v14/flow/spring/tutorial-spring-basic.html)
