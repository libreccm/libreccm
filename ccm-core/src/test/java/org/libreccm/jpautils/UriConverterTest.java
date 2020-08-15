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
package org.libreccm.jpautils;

import org.libreccm.jpa.utils.UriConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * This test suite checks the functionality of the {@link UriConverter} class
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UriConverterTest {

    private static final String WWW_EXAMPLE_ORG = "http://www.example.org";
    private static final String WWW_EXAMPLE_COM = "http://www.example.com";
    private static final String EXAMPLE_ORG_WITH_PATH2
                                    = "http://example.org/some/path";
    private static final String WWW_EXAMPLE_ORG_WITH_PATH
                                    = "http://www.example.org/some/path";
    private static final String WITH_USER_AND_PORT_AND_PATH
                                    = "http://foo:bar@example.org/api/?query=foo";
    private static final String FILE_PATH = "file:///home/foo/some/file";
    private static final String HTTP = "http";
    private static final String FILE = "file";

    private UriConverter uriConverter;

    public UriConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        uriConverter = new UriConverter();
    }

    @After
    public void tearDown() {
        uriConverter = null;
    }

    /**
     * Verifies that URI passed to 
     * {@link UriConverter#convertToDatabaseColumn(java.net.URI)} is converted
     * to the expected string value.
     * 
     * @throws URISyntaxException If one the test URIs could not be created
     * (should never happen).
     */
    @Test
    public void verifyToDatabaseColumn() throws URISyntaxException {
        final URI wwwExampleOrg = new URI(WWW_EXAMPLE_ORG);
        final URI wwwExampleCom = new URI(WWW_EXAMPLE_COM);
        final URI wwwExampleOrgWithPath = new URI(WWW_EXAMPLE_ORG_WITH_PATH);
        final URI exampleOrgWithPath = new URI(EXAMPLE_ORG_WITH_PATH2);
        final URI filePath = new URI(FILE_PATH);
        final URI withUserAndPortAndPath = new URI(
            WITH_USER_AND_PORT_AND_PATH);

        assertThat(uriConverter.convertToDatabaseColumn(wwwExampleOrg),
                   is(equalTo(WWW_EXAMPLE_ORG)));
        assertThat(uriConverter.convertToDatabaseColumn(wwwExampleCom),
                   is(equalTo(WWW_EXAMPLE_COM)));
        assertThat(uriConverter.convertToDatabaseColumn(wwwExampleOrgWithPath),
                   is(equalTo(WWW_EXAMPLE_ORG_WITH_PATH)));
        assertThat(uriConverter.convertToDatabaseColumn(exampleOrgWithPath),
                   is(equalTo(EXAMPLE_ORG_WITH_PATH2)));
        assertThat(uriConverter.convertToDatabaseColumn(filePath),
                   is(equalTo(FILE_PATH)));
        assertThat(uriConverter.convertToDatabaseColumn(withUserAndPortAndPath),
                   is(equalTo(WITH_USER_AND_PORT_AND_PATH)));
    }

    /**
     * Verifies that 
     * {@link UriConverter#convertToEntityAttribute(java.lang.String)}
     * returns the expected URI from the string passed to the method.
     */
    @Test
    public void verifyToEntityAttribute() {
        final URI wwwExampleOrg = uriConverter.convertToEntityAttribute(
            WWW_EXAMPLE_ORG);
        assertThat(wwwExampleOrg, is(instanceOf(URI.class)));
        assertThat(wwwExampleOrg.getScheme(), is(equalTo(HTTP)));
        assertThat(wwwExampleOrg.getHost(), is(equalTo("www.example.org")));

        final URI filePath = uriConverter.convertToEntityAttribute(FILE_PATH);
        assertThat(filePath, is(instanceOf(URI.class)));
        assertThat(filePath.getScheme(), is(equalTo(FILE)));

        final URI withUserAndPortAndPath = uriConverter
            .convertToEntityAttribute(WITH_USER_AND_PORT_AND_PATH);
        assertThat(withUserAndPortAndPath, is(instanceOf(URI.class)));
        assertThat(withUserAndPortAndPath.getScheme(), is(equalTo(HTTP)));
        assertThat(withUserAndPortAndPath.getHost(), is(equalTo("example.org")));
        assertThat(withUserAndPortAndPath.getUserInfo(), is(equalTo("foo:bar")));
        assertThat(withUserAndPortAndPath.getPath(), is(equalTo("/api/")));
        assertThat(withUserAndPortAndPath.getQuery(), is(equalTo("query=foo")));
    }

    /**
     * Checks if 
     * {@link UriConverter#convertToEntityAttribute(java.lang.String)}
     * throws an {@link IllegalArgumentException} for an invalid URI.
     */
    @Test(expected = IllegalArgumentException.class)
    public void invalidUriInDb() {
        uriConverter.convertToEntityAttribute("file:///foo/b([[ar");
    }
}
