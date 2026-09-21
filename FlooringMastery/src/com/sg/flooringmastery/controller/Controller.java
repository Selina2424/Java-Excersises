package com.sg.flooringmastery.controller;

import com.sg.flooringmastery.dto.Order;
import com.sg.flooringmastery.service.NoSuchOrderException;
import com.sg.flooringmastery.service.PersistenceException;
import com.sg.flooringmastery.service.ServiceLayer;
import com.sg.flooringmastery.ui.FlooringMasteryView;
import java.time.LocalDate;
import java.util.NoSuchElementException;


public class Controller {
    private final FlooringMasteryView view;
    private final ServiceLayer service;

    //app supplies the service and view that this controller will coordinate
    public Controller(ServiceLayer service, FlooringMasteryView view) {
        this.service = service;
        this.view = view;
    }

    //repeat the menu until the user selects Quit or closes console input
    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            //catch inside the loop so one failed action doesnt close the program.
            try {
                int menuSelection = getMenuSelection();
                switch (menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportData();
                        break;
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }
            } catch (PersistenceException | NoSuchOrderException | IllegalArgumentException e) {
                view.displayErrorMessage(e.getMessage());
            } catch (NoSuchElementException e) {
                // scanner throws this built in exception when console input has been closed
                keepGoing = false;
            }
        }
        exitMessage();
    }

    //ask the view to display the menu and read a selection
    private int getMenuSelection() {
        return view.displayMainMenuAndGetSelection();
    }

    //gets a date, ask the service for that dates orders, and display them
    private void displayOrders() throws PersistenceException {
        LocalDate date = view.getDateInput();
        view.displayOrders(service.getOrderByDate(date));
    }

    //collect input, display a calculated summary, and save only after confirmation
    private void addOrder() throws PersistenceException {
        view.displayAddOrderBanner();
        boolean hasErrors;
        do {
            hasErrors = false;
            Order input = view.getAddOrderInput(service.getTaxes(), service.getProducts());
            try {
                Order prepared = service.prepareNewOrder(input);
                view.displayOrderInfo(prepared);
                if (view.getConfirmation()) {
                    service.createOrder(prepared);
                    view.displayAddOrderSuccess();
                } //else return to menu
            } catch (IllegalArgumentException e) {
                // Same retry pattern as ClassRoster's createStudent method.
                hasErrors = true;
                view.displayErrorMessage(e.getMessage());
            }
        } while (hasErrors);
    }

    //loads an order, prepares changes, displays the new summary, and confirms before saving
    private void editOrder() throws PersistenceException, NoSuchOrderException {
        view.displayEditOrderBanner();
        LocalDate date = view.getDateInput();
        int orderNumber = view.getOrderNumberInput();
        Order original = service.getOrder(date, orderNumber);
        view.displayOrderInfo(original);
        Order changes = view.getEditOrderInput(original, service.getTaxes(), service.getProducts());
        Order prepared = service.prepareEditedOrder(date, orderNumber, changes);
        view.displayOrderInfo(prepared);
        if (view.getConfirmation()) {
            service.editOrder(date, orderNumber, prepared);
            view.displayEditOrderSuccess();
        } //else return to menu
    }

    //loads and displays an order, then removes it only when the user confirms
    private void removeOrder() throws PersistenceException, NoSuchOrderException {
        view.displayRemoveOrderBanner();
        LocalDate date = view.getDateInput();
        int orderNumber = view.getOrderNumberInput();
        Order order = service.getOrder(date, orderNumber);
        view.displayOrderInfo(order);
        if (view.getConfirmation()) {
            service.removeOrder(date, orderNumber);
            view.displayRemoveOrderSuccess();
        } //else return to menu
    }

    //ask the service to export all active orders and display the result
    private void exportData() throws PersistenceException {
        service.exportAllData();
        view.displayExportDataSuccess();
    }

    //use the view to explain an unrecognised menu choice
    private void unknownCommand() {
        view.displayUnknownCommandMessage();
    }

    //use the view to output the goodbye message
    private void exitMessage() {
        view.displayExitMessage();
    }


}
