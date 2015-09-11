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
package org.libreccm.modules;

import org.apache.maven.artifact.versioning.ComparableVersion;

import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.tests.categories.UnitTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(UnitTest.class)
public class ComparableVersionTest {

    public ComparableVersionTest() {
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
    public void compareVersions() {
        final ComparableVersion version001 = new ComparableVersion("0.0.1");
        final ComparableVersion version010 = new ComparableVersion("0.1.0");
        final ComparableVersion version015 = new ComparableVersion("0.1.5");
        final ComparableVersion version100alpha1 = new ComparableVersion(
            "1.0.0-alpha.1");
        final ComparableVersion version100alpha2 = new ComparableVersion(
            "1.0.0-alpha.2");
        final ComparableVersion version100beta1 = new ComparableVersion(
            "1.0.0-beta.1");
        final ComparableVersion version100beta2 = new ComparableVersion(
            "1.0.0-beta.2");
        final ComparableVersion version100 = new ComparableVersion("1.0.0");
        final ComparableVersion version100final = new ComparableVersion("1.0.0");
        final ComparableVersion version100ga = new ComparableVersion("1.0.0-ga");
        final ComparableVersion version157 = new ComparableVersion("1.5.7");
        final ComparableVersion version273beta3 = new ComparableVersion(
            "2.7.3-beta.3");
        final ComparableVersion emptyVersion = new ComparableVersion("");

        final List<ComparableVersion> versions = new ArrayList<>();
        versions.add(version001);
        versions.add(version157);
        versions.add(version100alpha2);
        versions.add(version100beta2);
        versions.add(version100alpha1);
        versions.add(version273beta3);
        versions.add(version100);
        versions.add(version010);
        versions.add(version015);
        versions.add(version100beta1);
        versions.add(version100ga);
        versions.add(version100final);
        versions.add(emptyVersion);

        Collections.sort(versions);

        assertThat(versions.size(), is(13));

        assertThat(versions,
                   contains(emptyVersion,
                            version001,
                            version010,
                            version015,
                            version100alpha1,
                            version100alpha2,
                            version100beta1,
                            version100beta2,
                            version100final,
                            version100ga,
                            version100,
                            version157,
                            version273beta3));
    }

}
