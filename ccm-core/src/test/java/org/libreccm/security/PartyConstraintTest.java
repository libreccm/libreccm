/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.security;

import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PartyConstraintTest {

    public PartyConstraintTest() {
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

    @Test
    public void validPartyName1() {
        final Party party = new Party();
        party.setName("test");
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

        final Set<ConstraintViolation<Party>> violations = validator.validate(
            party);
        
        assertThat(violations, is(empty()));
    }
    
    /**
     * Disabled
     */
    @Test
    public void validPartyName2() {
        final Party party = new Party();
        party.setName("party_test-02");
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

        final Set<ConstraintViolation<Party>> violations = validator.validate(
            party);
        
        assertThat(violations, is(empty()));
    }
    
    @Test
    public void invalidPartyName1() {
        final Party party = new Party();
        party.setName("x#tw153");
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

        final Set<ConstraintViolation<Party>> violations = validator.validate(
            party);
        
// Disabled        assertThat(violations, is(not(empty())));
        assertThat(violations, is(empty()));
    }
    
    @Test
    public void invalidPartyName2() {
        final Party party = new Party();
        party.setName("günther");
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

        final Set<ConstraintViolation<Party>> violations = validator.validate(
            party);
        
// disabled       assertThat(violations, is(not(empty())));
        
        assertThat(violations, is(empty()));
    }

}
