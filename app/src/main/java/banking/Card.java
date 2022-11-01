package banking;

public class Card {
    private final long id;
    private final String number;
    private final String pin;
    private long balance;

    public Card(long id, String number, String pin) {
        this.id = id;
        this.number = number;
        this.pin = pin;
        balance = 0;
    }

    public long getId() {
        return id;
    }
    public long getBalance() {
        return balance;
    }
    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }
    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "\nYour card has been created\n" +
                "Your card number:\n" +
                number + "\n" +
                "Your card PIN:\n" +
                pin + "\n";
    }
}
