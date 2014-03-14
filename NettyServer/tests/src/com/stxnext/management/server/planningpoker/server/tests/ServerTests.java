package com.stxnext.management.server.planningpoker.server.tests;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.j256.ormlite.dao.ForeignCollection;
import com.stxnext.management.server.planningpoker.server.ServerConfigurator;
import com.stxnext.management.server.planningpoker.server.database.managers.DAO;
import com.stxnext.management.server.planningpoker.server.dto.combined.Card;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Ticket;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;
import com.stxnext.management.server.planningpoker.server.handlers.DeckFactory;

public class ServerTests {

    static Logger logger;
    
    @Before
    public void setUp() throws Exception {
        ServerConfigurator.getInstance().configure();
        logger = ServerConfigurator.getInstance().getLogger();
    }

    @After
    public void tearDown() throws Exception {
    }

    //@Test
    public void testEntityCRUD() throws Exception{
        //C
        
        DAO dao = DAO.getInstance();
        
        Player player1 = new Player();
        player1.setEmail("player1@gmail.com");
        player1.setName("player1");
        player1.setExternalId(1);
        
        Player player2 = new Player();
        player2.setEmail("player2@gmail.com");
        player2.setName("player2");
        player2.setExternalId(2);
        
        dao.getPlayerDao().create(player1);
        dao.getPlayerDao().create(player2);
        
        Card card = new Card();
        card.setName("trefl ;)");
        dao.getCardDao().create(card);
        
        Ticket ticket = new Ticket();
        ticket.setDisplayValue("some ticket");
        dao.getTicketDao().create(ticket);
        
        Vote vote1 = new Vote();
        vote1.setCard(card);
        vote1.setPlayer(player1);
        vote1.setTicket(ticket);
        
        Vote vote2 = new Vote();
        vote2.setCard(card);
        vote2.setPlayer(player2);
        vote2.setTicket(ticket);
        
        dao.getVoteDao().create(vote1);
        dao.getVoteDao().create(vote2);
        
        
        // R
        
        List<Ticket> tickets = dao.getTicketDao().queryForAll();
        for(Ticket t : tickets){
            ForeignCollection<Vote> votes = t.getVotes();
            for(Vote v : votes){
                logger.log(Level.DEBUG, t.getDisplayValue()+" has vote "+v.getCard().getName());
            }
        }
        
        DeckFactory.preparePredefined(dao);

        List<Deck> decks = dao.getDeckDao().queryForAll();
        for(Deck deck : decks){
            ForeignCollection<Card> cards = deck.getCards();
            for(Card c : cards){
                logger.log(Level.DEBUG, deck.getName()+" has card "+c.getName());
            }
        }
        
        // U
        
        
        // D
        
        //ticket.setVotes(votes)
        
    }
    
    
    @Test
    public void testGsonSerialization() throws Exception{
        List<Deck> decks = DAO.getInstance().getDeckDao().queryForAll();
        for(Deck deck : decks){
            String jsonString = deck.serialize();
            logger.log(Level.DEBUG, jsonString);
        }
    }

}
