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
package com.arsdigita.web;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class WebConfigTest {

    public WebConfigTest() {
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
    public void setValidResolver() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setResolverClass(DefaultApplicationFileResolver.class
            .getName());

        assertThat(webConfig.getResolverClass(),
                   is(equalTo(DefaultApplicationFileResolver.class.getName())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidResolver() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setResolverClass(ArrayList.class.getName());

    }

    @Test(expected = IllegalArgumentException.class)
    public void setNotExistingResolver() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setResolverClass("org.example.resolvers.NotExisting");
    }

    @Test
    public void setValidHost() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setHost("zeus.example.org:8080");

        assertThat(webConfig.getHost(),
                   is(equalTo("zeus.example.org:8080")));
        assertThat(webConfig.getHostName(),
                   is(equalTo("zeus.example.org")));
        assertThat(webConfig.getHostPort(),
                   is(equalTo(8080)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidHostWithSchema() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setHost("http://zeus.example.org:8080");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidHostWithoutPort() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setHost("http://zeus.example.org");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidHostLongPort() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setHost("http://zeus.example.org:999999");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidHostWithPath() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setHost("http://zeus.example.org:8080/foo");
    }

    @Test
    public void setValidServer() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setServer("zeus.example.org:8080");

        assertThat(webConfig.getServer(),
                   is(equalTo("zeus.example.org:8080")));
        assertThat(webConfig.getServerName(),
                   is(equalTo("zeus.example.org")));
        assertThat(webConfig.getServerPort(),
                   is(equalTo(8080)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidServerWithSchema() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setServer("http://zeus.example.org:8080");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidServerWithoutPort() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setServer("http://zeus.example.org");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidServerLongPort() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setServer("http://zeus.example.org:999999");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidServerWithPath() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setServer("http://zeus.example.org:8080/foo");
    }

    @Test
    public void setValidSecureServer() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setSecureServer("zeus.example.org:8080");

        assertThat(webConfig.getSecureServer(),
                   is(equalTo("zeus.example.org:8080")));
        assertThat(webConfig.getSecureServerName(),
                   is(equalTo("zeus.example.org")));
        assertThat(webConfig.getSecureServerPort(),
                   is(equalTo(8080)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidSecureServerWithSchema() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setSecureServer("http://zeus.example.org:8080");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidSecureServerWithoutPort() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setSecureServer("http://zeus.example.org");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidSecureLongPort() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setSecureServer("http://zeus.example.org:999999");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidSecureServerWithPath() {
        final WebConfig webConfig = new WebConfig();

        webConfig.setSecureServer("http://zeus.example.org:8080/foo");
    }

}
