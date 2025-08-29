package console;

import java.util.InputMismatchException;
import java.util.Scanner;

public abstract class AbstractExpandingOption extends MenuPage{

    public AbstractExpandingOption(String name, String message) {
        super(name, message);
    }

    public int getExpansionDegree(int maxExpansionDegree) {
        String range = "[0..." +  maxExpansionDegree + "]";
        System.out.println("Please enter the expansion degree " + range + ":");
        Scanner sc = new Scanner(System.in);

        int userInput = -1;

        while(userInput < 0 || userInput > maxExpansionDegree) {
            try {
                userInput = sc.nextInt();
                if(userInput < 0 || userInput > maxExpansionDegree)
                    System.out.println("Invalid input. Please input a number " + range);
            } catch (InputMismatchException e){
                System.out.println("Invalid input. Please input a number " + range);
                sc.nextLine();
            }
        }

        return userInput;
    }
}
