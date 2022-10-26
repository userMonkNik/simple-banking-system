package banking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Repository {
    private static final Map<String, Card> database = new ConcurrentHashMap<>();

    public Card save(Card card) {
        database.put(card.getId(), card);

        return card;
    }

    public Card get(String id) {
        return database.get(id);
    }

    public boolean isUnique(String id) {
        return database.get(id) == null;
    }
}
