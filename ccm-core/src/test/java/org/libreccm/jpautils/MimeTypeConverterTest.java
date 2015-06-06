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

import org.libreccm.jpa.utils.MimeTypeConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MimeTypeConverterTest {

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_OCTET_STREAM
                               = "application/octet-stream";
    public static final String IMAGE_PNG = "image/png";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String DOCX
                               = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String VIDEO_MP4
                               = "video/mp4";

    private transient MimeTypeConverter mimeTypeConverter;
    
    public MimeTypeConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        mimeTypeConverter = new MimeTypeConverter();
    }

    @After
    public void tearDown() {
        mimeTypeConverter = null;
    }

    @Test
    public void verifyToDatabaseColumn() throws MimeTypeParseException {
        final MimeType textPlain = new MimeType(TEXT_PLAIN);
        final MimeType textXml = new MimeType(TEXT_XML);
        final MimeType textHtml = new MimeType(TEXT_HTML);
        final MimeType octetStream = new MimeType(APPLICATION_OCTET_STREAM);
        final MimeType appPdf = new MimeType(APPLICATION_PDF);
        final MimeType imgPng = new MimeType(IMAGE_PNG);
        final MimeType docx = new MimeType(DOCX);
        final MimeType videoMp4 = new MimeType(VIDEO_MP4);
        
        assertThat(mimeTypeConverter.convertToDatabaseColumn(textPlain),
                   is(equalTo(TEXT_PLAIN)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(textXml),
                   is(equalTo(TEXT_XML)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(textHtml),
                   is(equalTo(TEXT_HTML)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(octetStream),
                   is(equalTo(APPLICATION_OCTET_STREAM)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(appPdf),
                   is(equalTo(APPLICATION_PDF)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(imgPng),
                   is(equalTo(IMAGE_PNG)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(docx),
                   is(equalTo(DOCX)));
        assertThat(mimeTypeConverter.convertToDatabaseColumn(videoMp4),
                   is(equalTo(VIDEO_MP4)));
    }
    
    @Test
    public void verifyToEntityAttribute() {
        final MimeType textPlain = mimeTypeConverter.convertToEntityAttribute(
            TEXT_PLAIN);
        assertThat(textPlain.toString(), is(equalTo(TEXT_PLAIN)));
        
        final MimeType docx = mimeTypeConverter.convertToEntityAttribute(DOCX);
        assertThat(docx.toString(), is(equalTo(DOCX)));
        
        final MimeType videoMp4 = mimeTypeConverter.convertToEntityAttribute(
            VIDEO_MP4);
        assertThat(videoMp4.toString(), is(equalTo(VIDEO_MP4)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMimeTypeInDb() {
        mimeTypeConverter.convertToEntityAttribute("foo//bar");
    }
}
