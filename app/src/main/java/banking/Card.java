package banking;

public class Card {
    private final String id;
    private final String pin;

    private long balance;

    public Card(String id, String pin) {
        this.id = id;
        this.pin = pin;
        balance = 0;
    }

    public long getBalance() {
        return balance;
    }
    public String getId() {
        return id;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public String toString() {
        return "\nYour card has been created\n" +
                "Your card number:\n" +
                id + "\n" +
                "Your card PIN:\n" +
                pin + "\n\n";
    }
}
