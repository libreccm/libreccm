/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LifecyclesControllerTest {
    
    public LifecyclesControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of updatePhaseDates method, of class DocumentLifecyclesController.
     */
    @Test
    public void testUpdatePhaseDates() {
        System.out.println("updatePhaseDates");
        String sectionIdentifier = "";
        String documentPath = "";
        long phaseId = 0L;
        String startDateParam = "";
        String endDateParam = "";
        DocumentLifecyclesController instance = new DocumentLifecyclesController();
        String expResult = "";
        String result
            = instance.updatePhaseDates(sectionIdentifier, documentPath, phaseId,
                                        startDateParam, endDateParam);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
