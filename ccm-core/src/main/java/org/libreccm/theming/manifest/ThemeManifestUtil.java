/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.theming.manifest;

import static org.libreccm.theming.ThemeConstants.THEME_MANIFEST_JSON;
import static org.libreccm.theming.ThemeConstants.THEME_MANIFEST_XML;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.libreccm.core.UnexpectedErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;

/**
 * A Utility class for loading them manifest file of a theme.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeManifestUtil implements Serializable {

    private static final long serialVersionUID = -7650437144515619682L;

    /**
     * Reads the manifest file at {@code path}.
     *
     * @param path The path of the manifest file.
     *
     * @return The parsed manifest file.
     */
    public ThemeManifest loadManifest(final Path path) {
        final BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return parseManifest(reader, path.toString());
    }

    public ThemeManifest loadManifest(
        final InputStream inputStream, final String fileName
    ) {
        final InputStreamReader reader;
        try {
            reader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return parseManifest(reader, fileName);
    }

    public String serializeManifest(
        final ThemeManifest manifest, final String format
    ) {
        final ObjectMapper mapper;

        switch (format) {
            case THEME_MANIFEST_JSON:
                mapper = new ObjectMapper();
                break;
            case THEME_MANIFEST_XML:
                final JacksonXmlModule xmlModule = new JacksonXmlModule();
                mapper = new XmlMapper(xmlModule);
                break;
            default:
                throw new IllegalArgumentException(
                    "Unsupported format for ThemeManifest");
        }

        mapper.registerModule(new JaxbAnnotationModule());
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        final StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, manifest);
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return writer.toString();
    }

    private ThemeManifest parseManifest(final Reader reader,
                                        final String path) {

        final String pathStr = path.toLowerCase(Locale.ROOT);

        final ObjectMapper mapper;
        if (pathStr.endsWith(THEME_MANIFEST_JSON)) {
            mapper = new ObjectMapper();
        } else if (pathStr.endsWith(THEME_MANIFEST_XML)) {
            final JacksonXmlModule xmlModule = new JacksonXmlModule();
            mapper = new XmlMapper(xmlModule);
        } else {
            throw new IllegalArgumentException(String
                .format("The provided path \"%s\" does not point to a theme "
                            + "manifest file.",
                        path));
        }

        mapper.registerModule(new JaxbAnnotationModule());

        final ThemeManifest manifest;
        try {
            manifest = mapper.readValue(reader, ThemeManifest.class);
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
        return manifest;
    }

}
