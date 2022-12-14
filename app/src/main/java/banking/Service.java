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

        while (isNumberExists(cardNumber)) {
            cardNumber = generateNumber();
        }

        return repository.save(new Card(id, cardNumber, cardPin));
    }

    private boolean isNumberExists(String number) {

        HashSet<String> uniqueNumbersSet = repository.getAllNumber();

        return uniqueNumbersSet.contains(number);
    }

    private boolean isCorrectPin(String cardId, String cardPin) {
        Card tempCard = repository.get(cardId);

        if (tempCard != null) {

            if (tempCard.getPin().equals(cardPin)) {

                System.out.println("\nYou have successfully logged in!");
                sessionCard = tempCard;
                return true;
            }
        }
        return false;
    }

    public boolean logIntoAccount(Scanner scannerInput) {

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
                    case "0" -> {
                        return exitFlagChanger();
                    }
                    case "1" -> System.out.println("\nBalance: " + getBalance());
                    case "2" -> updateBalance(scannerInput);
                    case "3" -> {
                        if (transferMoney(scannerInput)) {
                            System.out.print("Success!\n");
                        }
                    }
                    case "4" -> {
                        deleteAccount();
                        exitFromMainMenuFlag = exitFlagChanger();
                    }
                    case "5" -> {
                        System.out.println("\nYou have successfully logged out!");
                        exitFromMainMenuFlag = exitFlagChanger();
                    }
                    default -> printWrongActionMenu();
                }
            }
        } else {
            System.out.print("\nWrong card number or PIN!\n");
        }
        return false;
    }

    private boolean transferMoney(Scanner scannerInput) {
        System.out.print("\nTransfer\n" +
                "Enter card number:\n");

        String recipientNumber = scannerInput.nextLine();

        if (!isLuhnAlgorithm(recipientNumber)) {
            System.out.print("Probably you made a mistake in the card number. Please try again!\n");

            return false;
        }

        if (!isNumberExists(recipientNumber)) {
            System.out.print("Such a card does not exist.\n");

            return false;
        }

        if (sessionCard.getNumber().equals(recipientNumber)) {
            System.out.print("You can't transfer money to the same account!\n");

            return false;
        }

        System.out.print("Enter how much money you want to transfer:\n");

        long moneyToTransfer = scannerInput.nextLong();
        scannerInput.nextLine();

        if (!isEnoughMoneyToTransfer(moneyToTransfer)) {
            System.out.print("Not enough money!\n");

            return false;
        }

        return repository.transactionalTransferMoney(
                sessionCard.getNumber(),
                recipientNumber,
                moneyToTransfer
                );
    }

    private boolean isEnoughMoneyToTransfer(long money) {
        return (repository.get(sessionCard.getNumber()).getBalance()) - money >= 0;
    }

    private void updateBalance(Scanner scannerInput) {
        System.out.print("\nEnter income:\n");

        long income = scannerInput.nextLong();
        scannerInput.nextLine();

        if (income >= 0) {

            repository.updateBalance(income, sessionCard.getNumber());
            System.out.print("Income was added!\n");
        } else {

            System.out.print("Wrong format. Please, try again...\n");
        }
    }

    private void deleteAccount() {

        repository.deleteAccount(sessionCard.getNumber());
        sessionCard = null;

        System.out.print("\nThe account has been closed!\n");
    }

    public long getBalance() {
        return repository.get(sessionCard.getNumber()).getBalance();
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

    private boolean isLuhnAlgorithm(String recipientNumber) {

        if (recipientNumber.length() != 16) {
            return false;
        }

        String subStringRecipientNumber = recipientNumber.substring(0, 15);
        int luhnNumber = luhnAlgorithm(subStringRecipientNumber);

        return luhnNumber == Integer.parseInt(recipientNumber.substring(15, 16));
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
