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

import static org.libreccm.theming.ThemeConstants.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.libreccm.core.UnexpectedErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
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
     * @return The parsed manifest file.
     */
    public ThemeManifest loadManifest(final Path path) {

        final String pathStr = path.toString().toLowerCase(Locale.ROOT);

        final BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

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
                        path.toString()));
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
