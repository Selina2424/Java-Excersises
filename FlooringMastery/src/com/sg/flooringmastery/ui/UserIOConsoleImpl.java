package com.sg.flooringmastery.ui;
import java.util.Scanner;
public class UserIOConsoleImpl implements UserIo {
    String userInputStr;
    int userInputInt;

    Scanner sc = new Scanner(System.in);
    //prints to user
    @Override
    public void print(String prompt) {
        System.out.println(prompt);
    }
    //takes in string input from user after prompting them
    @Override
    public String readString(String prompt) {
        System.out.print(prompt);
        userInputStr = sc.nextLine();
        return String.format(userInputStr);
    }
    //prompts user and takes in int
    @Override
    public int readInt(String prompt) {
        System.out.print(prompt);
        //converts input to int
        userInputInt = Integer.parseInt(sc.nextLine());
        return (userInputInt);

    }

    @Override
    public int readInt(String prompt, int min, int max){
        System.out.print(prompt);
        userInputInt = Integer.parseInt(sc.nextLine());
        while(userInputInt < min || userInputInt > max){
            System.out.println("please try again " + userInputInt + " is not between " + min + " - " + max);
            userInputInt = Integer.parseInt(sc.nextLine());

        }
        return (userInputInt);
    }


}
