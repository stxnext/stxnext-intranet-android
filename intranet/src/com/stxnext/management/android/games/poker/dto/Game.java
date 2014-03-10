
package com.stxnext.management.android.games.poker.dto;

import java.util.Date;
import java.util.List;

import com.parse.ParseObject;

public class Game extends AbstractDTO {
    private static final String ENTITY_NAME = "person";

    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_END_DATE = "endDate";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_OWNER = "owner";
    private static final String FIELD_DECK = "deck";
    private static final String FIELD_PLAYERS = "players";
    private static final String FIELD_TICKETS = "tickets";

    Object externalId;
    Date startDate;
    Date endDate;
    String title;
    Person owner;
    Number deck;
    List<Person> players;
    List<Ticket> tickets;

    @Override
    public ParseObject toParse() {
        ParseObject po = ParseObject.create(ENTITY_NAME);
        po.put(FIELD_START_DATE, this.startDate);
        if (this.externalId != null) {
            po.setObjectId(String.valueOf(this.externalId));
        }
        po.put(FIELD_END_DATE, this.endDate);
        po.put(FIELD_TITLE, this.title);
        po.put(FIELD_OWNER, this.owner.toParse());
        po.put(FIELD_DECK, this.deck);
        po.put(FIELD_PLAYERS, convertListToParseList(this.players));
        po.put(FIELD_TICKETS, convertListToParseList(this.tickets));
        return po;
    }

    @Override
    public void inflateFromParse(ParseObject po) {
        this.startDate = po.getDate(FIELD_START_DATE);
        this.endDate = po.getDate(FIELD_END_DATE);
        this.title = po.getString(FIELD_TITLE);
        this.owner = new Person(po.getParseObject(FIELD_OWNER));
        this.deck = po.getNumber(FIELD_DECK);
        this.externalId = po.getObjectId();

        List<ParseObject> parsePlayers = po.getList(FIELD_PLAYERS);
        this.players = inflateListFromParseObjects(parsePlayers, Person.class);

        List<ParseObject> parseTickets = po.getList(FIELD_TICKETS);
        this.tickets = inflateListFromParseObjects(parseTickets, Ticket.class);
    }
    
    public Object getExternalId() {
        return externalId;
    }

    public void setExternalId(Object externalId) {
        this.externalId = externalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Number getDeck() {
        return deck;
    }

    public void setDeck(Number deck) {
        this.deck = deck;
    }

    public List<Person> getPlayers() {
        return players;
    }

    public void setPlayers(List<Person> players) {
        this.players = players;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

}
