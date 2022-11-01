package banking;

import java.util.HashSet;

public class Repository {

    Repository() {
        sqLiteConnect = new SQLiteConnect();
        init();
    }

    private final SQLiteConnect sqLiteConnect;
    private static HashSet<String> uniqueNumbers;
    private static HashSet<Long> uniqueIds;
    public static long currentIdSequence;

    private void init() {
        uniqueNumbers = sqLiteConnect.getAllNumber();
        uniqueIds = sqLiteConnect.getAllId();
        currentIdSequence = sqLiteConnect.getLastIdValue();
    }

    public Card save(Card card) {
        sqLiteConnect.saveObject(card.getId(), card.getNumber(), card.getPin(), card.getBalance());
        uniqueIds.add(card.getId());
        uniqueNumbers.add(card.getNumber());

        return card;
    }

    public void updateBalance(long income, String number) {
        sqLiteConnect.updateBalance(income, number);
    }

    public Card get(String id) {
        return sqLiteConnect.getObject(id);
    }

    public HashSet<String> getAllNumber() {
        return uniqueNumbers;
    }

    public HashSet<Long> getAllId() {
        return uniqueIds;
    }
}
