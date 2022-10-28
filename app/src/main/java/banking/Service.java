package banking;

import java.util.*;

public class Service {

    private final Repository repository = new Repository();
    private final Random random = new Random(8080);
    private Card sessionCard;
    private final String BIN = "400000";
    private final int pinLength = 4;
    private final int cardIdLength = 15;

    public void printAuthMenu() {
        System.out.print(Menu.AUTH);
    }

    public void printMainMenu() {
        System.out.print(Menu.MAIN);
    }

    public void printWrongActionMenu() {
        System.out.print(Menu.WRONG_ACTION);
    }

    public Card createAccount() {

        long id = setId();
        String cardNumber = generateNumber();
        String cardPin = generatePin();

        HashSet<String> uniqueNumbersSet = repository.getAllNumber();

        while (uniqueNumbersSet.contains(cardNumber)) {
            cardNumber = generateNumber();
        }

        return repository.save(new Card(id, cardNumber, cardPin));
    }

    private boolean isCorrectPin(String cardId, String cardPin) {
        Card tempCard = repository.get(cardId);

        if (tempCard != null) {

            if (tempCard.getPin().equals(cardPin)) {

                System.out.println("\nYou have successfully logged in!\n");
                sessionCard = tempCard;
                return true;
            }
        }
        return false;
    }

    public void logIntoAccount(Scanner scannerInput) {

        System.out.print("\nEnter your card number:\n");
        String cardId = scannerInput.nextLine();
        System.out.print("Enter your PIN:\n");
        String cardPin = scannerInput.nextLine();

        if (isCorrectPin(cardId, cardPin)) {
            boolean exitFromMainMenuFlag = false;
            String tempInput;

            while (!exitFromMainMenuFlag) {
                printMainMenu();
                tempInput = scannerInput.nextLine();

                switch (tempInput) {
                    case "0" -> exitFromMainMenuFlag = exitFlagChanger();
                    case "1" -> System.out.println("\nBalance: " + getBalance() + "\n");
                    case "2" -> {
                        System.out.println("\nYou have successfully logged out!\n");
                        exitFromMainMenuFlag = exitFlagChanger();
                    }
                    default -> printWrongActionMenu();
                }
            }
        } else {
            System.out.print("\nWrong card number or PIN!\n");
        }
    }

    public long getBalance() {
        return sessionCard.getBalance();
    }

    public boolean exitFlagChanger() {
        sessionCard = null;
        return true;
    }

    private long setId() {
        return ++Repository.currentIdSequence;
    }

    private String generateNumber() {
        StringBuilder tempId = new StringBuilder(BIN);

        for (int i = 0; tempId.length() < cardIdLength; i++) {
            tempId.append(generateRandomNumber(random));
        }

        tempId.append(luhnAlgorithm(tempId.toString()));

        return tempId.toString();
    }

    private String generatePin() {
        StringBuilder tempPin = new StringBuilder();

        for (int i = 0; tempPin.length() < pinLength; i++) {
            tempPin.append(generateRandomNumber(random));
        }

        return tempPin.toString();
    }

    private int luhnAlgorithm(String id) {
        int[] idArray = Arrays.stream(id.split(""))
                .mapToInt(Integer::parseInt)
                .toArray();

        for (int i = 0; i < idArray.length; i++) {

            if (i % 2 == 0) {

                idArray[i] *= 2;
            }
            if (idArray[i] > 9) {

                idArray[i] -= 9;
            }
        }

        int checksum = Arrays.stream(idArray).sum();
        int result = 0;


        for (int j = 0; j < 10; j++) {

            if ((checksum + j) % 10 == 0) {
                result = j;
                break;
            }
        }
        return result;
    }

    private int generateRandomNumber(Random randomGenerator) {
        randomGenerator.setSeed((int)(Math.random() * 100));

        return randomGenerator.nextInt(9);
    }
}
