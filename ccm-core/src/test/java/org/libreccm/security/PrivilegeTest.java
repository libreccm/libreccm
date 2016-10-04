/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.oro.text.GlobCompiler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.libreccm.tests.categories.UnitTest;

import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * A test for verifying that the default implementations of the methods of the
 * {@link Privilege} interface work as expected.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(UnitTest.class)
public class PrivilegeTest {

    public PrivilegeTest() {
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
    public void verifyKeys() {
        assertThat(ExamplePrivileges.ADMIN_EXAMPLES.getKey(),
                   is(equalTo("ADMIN_EXAMPLES")));
        assertThat(ExamplePrivileges.CREATE_EXAMPLES.getKey(),
                   is(equalTo("CREATE_EXAMPLES")));
        assertThat(ExamplePrivileges.EDIT_EXAMPLES.getKey(),
                   is(equalTo("EDIT_EXAMPLES")));
        assertThat(ExamplePrivileges.PREVIEW_EXAMPLES.getKey(),
                   is(equalTo("PREVIEW_EXAMPLES")));
        assertThat(ExamplePrivileges.PUBLISH_EXAMPLES.getKey(),
                   is(equalTo("PUBLISH_EXAMPLES")));
        assertThat(ExamplePrivileges.VIEW_EXAMPLES.getKey(),
                   is(equalTo("VIEW_EXAMPLES")));
    }

    @Test
    public void verifyLabels() {
        final GlobalizedMessage adminLabel = ExamplePrivileges.ADMIN_EXAMPLES
            .getLabel();
        final GlobalizedMessage createLabel = ExamplePrivileges.CREATE_EXAMPLES
            .getLabel();
        final GlobalizedMessage editLabel = ExamplePrivileges.EDIT_EXAMPLES
            .getLabel();
        final GlobalizedMessage previewLabel
                                    = ExamplePrivileges.PREVIEW_EXAMPLES
            .getLabel();
        final GlobalizedMessage publishLabel
                                    = ExamplePrivileges.PUBLISH_EXAMPLES
            .getLabel();
        final GlobalizedMessage viewLabel = ExamplePrivileges.VIEW_EXAMPLES
            .getLabel();

        assertThat(adminLabel.localize(Locale.ENGLISH),
                   is(equalTo("Administer examples")));
        assertThat(adminLabel.localize(Locale.GERMAN),
                   is(equalTo("Beispiele verwalten")));

        assertThat(createLabel.localize(Locale.ENGLISH),
                   is(equalTo("Create new examples")));
        assertThat(createLabel.localize(Locale.GERMAN),
                   is(equalTo("Neue Beispiele anlegen")));

        assertThat(editLabel.localize(Locale.ENGLISH),
                   is(equalTo("Edit examples")));
        assertThat(editLabel.localize(Locale.GERMAN),
                   is(equalTo("Beispiele bearbeiten")));

        assertThat(previewLabel.localize(Locale.ENGLISH),
                   is(equalTo("Preview examples")));
        assertThat(previewLabel.localize(Locale.GERMAN),
                   is(equalTo("Vorschau ansehen")));

        assertThat(publishLabel.localize(Locale.ENGLISH),
                   is(equalTo("Publish examples")));
        assertThat(publishLabel.localize(Locale.GERMAN),
                   is(equalTo("Beispiele veröffentlichen")));

        assertThat(viewLabel.localize(Locale.ENGLISH),
                   is(equalTo("View examples")));
        assertThat(viewLabel.localize(Locale.GERMAN),
                   is(equalTo("Beispiele ansehen")));
    }

}
