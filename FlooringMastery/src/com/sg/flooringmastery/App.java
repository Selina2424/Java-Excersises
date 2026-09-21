package com.sg.flooringmastery;

import com.sg.flooringmastery.controller.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        //loads the objects and their connections from applicationContext.xml
        ApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        //gets the completed controller object from Spring
        Controller controller = context.getBean("controller", Controller.class);
        controller.run();
    }
}
