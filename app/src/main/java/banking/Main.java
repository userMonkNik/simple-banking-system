package banking;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        if (args.length > 1) {
            SQLiteConnect.dbName = args[1];
        }

        final Scanner scannerInput = new Scanner(System.in);
        final Service service = new Service();
        boolean exitFromAuthMenuFlag = false;


        String input;

        while (!exitFromAuthMenuFlag) {
            service.printAuthMenu();
            input = scannerInput.nextLine();

            switch (input) {
                case "0" -> {
                    exitFromAuthMenuFlag = service.exitFlagChanger();
                    System.out.print("\nBye!");
                }
                case "1" -> {
                    Card currentCard = service.createAccount();
                    System.out.print(currentCard.toString());
                }
                case "2" -> service.logIntoAccount(scannerInput);
                default -> service.printWrongActionMenu();
            }
        }
    }

}