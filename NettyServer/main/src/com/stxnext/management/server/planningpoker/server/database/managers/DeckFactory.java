
package com.stxnext.management.server.planningpoker.server.database.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;
import com.stxnext.management.server.planningpoker.server.dto.combined.Card;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;

public class DeckFactory {

    public enum DeckType {
        BINARY("Binary", new String[] {
                "0", "1", "2", "3", "5", "8", "13", "20", "40", "100", "?", "cafe"
        }),
        FIBONACCI("Fibonacci", new String[] {
                "0", "1", "2", "4", "8", "16", "32", "64", "128", "?", "cafe"
        }),
        LARGE("Large", new String[] {
                "0", "10", "20", "30", "50", "80", "130", "200", "400", "999", "?", "cafe"
        }),
        ONE_TO_TEN("1 to 10", new String[] {
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?", "cafe"
        });

        String name;
        String[] displayNames;

        DeckType(String name, String[] displayNames) {
            this.name = name;
            this.displayNames = displayNames;
        }

        public String getName() {
            return name;
        }
        
    }

    public static HashMap<Deck, List<Card>> preparePredefined(DAO dao) throws Exception {
        HashMap<Deck, List<Card>> result = new LinkedHashMap<Deck, List<Card>>();
        for(DeckType type : DeckType.values()){
            prepareDeck(type, result, dao);
        }
        return result;
    }

    private static void prepareDeck(final DeckType type, final HashMap<Deck, List<Card>> bucket, DAO dao)
            throws Exception {
        final Dao<Deck, Long> deckDao = dao.getDeckDao();
        final Dao<Card, Long> cardDao = dao.getCardDao();

        long total = deckDao.countOf(deckDao.queryBuilder().setCountOf(true).where()
                .eq(Deck.FIELD_PREDEFINED_TYPE, type.ordinal()).prepare());
        if (total > 0)
            return;

        final Deck deck = new Deck();
        deck.setName(type.getName());
        deck.setPredefinedType(type.ordinal());
        deckDao.create(deck);

        cardDao.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                List<Card> cards = new ArrayList<Card>();

                for (String cardValue : type.displayNames) {
                    Card card = new Card();
                    card.setDeck(deck);
                    card.setName(cardValue);
                    cardDao.create(card);
                    cards.add(card);
                }

                bucket.put(deck, cards);
                return null;
            }
        });
    }
}
