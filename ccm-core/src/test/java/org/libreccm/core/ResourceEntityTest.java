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
package org.libreccm.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.tests.categories.UnitTest;

import java.util.Locale;



/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(UnitTest.class)
public class ResourceEntityTest {
    
    public ResourceEntityTest() {
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
    public void verifyEqualsAndHashCode() {
        final Resource resource1 = new Resource();
        final LocalizedString title1 = new LocalizedString();
        title1.addValue(Locale.ENGLISH, "Resource 1");
        resource1.setTitle(title1);
        
        final Resource resource2 = new Resource();
        final LocalizedString title2 = new LocalizedString();
        title2.addValue(Locale.ENGLISH, "Resource 2");
        resource2.setTitle(title2);
        
        EqualsVerifier
            .forClass(Resource.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .withPrefabValues(Resource.class, resource1, resource2)
            .withRedefinedSuperclass()
            .verify();
    }
}
