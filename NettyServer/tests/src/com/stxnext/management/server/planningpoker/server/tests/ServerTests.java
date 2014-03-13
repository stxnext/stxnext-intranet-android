package com.stxnext.management.server.planningpoker.server.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stxnext.management.server.planningpoker.server.database.managers.DAO;

public class ServerTests {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEntityCRUD() {
        DAO.getInstance();
        
    }

}
