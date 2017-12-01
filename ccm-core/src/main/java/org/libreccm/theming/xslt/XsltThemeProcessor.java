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
package org.libreccm.theming.xslt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProcessor;
import org.libreccm.theming.ThemeProvider;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.libreccm.theming.ThemeConstants.*;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.theming.ProcessesThemes;
import org.libreccm.theming.manifest.ThemeTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * A {@link ThemeProcessor} implementation for XSLT based themes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ProcessesThemes("xsl")
@RequestScoped
public class XsltThemeProcessor implements ThemeProcessor {

    private static final long serialVersionUID = -3883625727845105417L;

    private static final Logger LOGGER = LogManager
        .getLogger(XsltThemeProcessor.class);

    @Override
    public String process(final Map<String, Object> page,
                          final ThemeInfo theme,
                          final ThemeProvider themeProvider) {

        //Convert page to XML
        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        final ObjectMapper mapper = new XmlMapper(xmlModule);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        final String pageAsXml;
        try {
            pageAsXml = mapper
                .writer()
                .withRootName("page")
                .writeValueAsString(page);
        } catch (JsonProcessingException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final DocumentBuilderFactory documentBuilderFactory
                                         = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Document document;
        try {
            final InputStream xmlBytesStream = new ByteArrayInputStream(
                pageAsXml.getBytes(StandardCharsets.UTF_8));
            document = documentBuilder.parse(xmlBytesStream);
        } catch (SAXException | IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final String pathToTemplate;
        if (page.containsKey(PAGE_PARAMETER_TEMPLATE)) {

            final String templateName = (String) page
                .get(PAGE_PARAMETER_TEMPLATE);

            final Optional<ThemeTemplate> template = theme
                .getManifest()
                .getTemplates()
                .stream()
                .filter(current -> current.getName().equals(templateName))
                .findAny();

            if (template.isPresent()) {
                pathToTemplate = template.get().getPath();
            } else {
                throw new UnexpectedErrorException(String
                    .format("Theme \"%s\" does provide template \"%s\".",
                            theme.getName(),
                            templateName));
            }
        } else {
            pathToTemplate = theme.getManifest().getDefaultTemplate();
        }

        final InputStream xslFileInputStream = themeProvider
            .getThemeFileAsStream(theme.getName(),
                                  theme.getVersion(),
                                  pathToTemplate)
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("Failed to open XSL file \"%s\" from theme \"%s\" for "
                        + "reading.",
                    pathToTemplate,
                    theme.getName())));

        final Reader reader;
        try {
            reader = new InputStreamReader(xslFileInputStream, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final StreamSource xslFileStreamSource = new StreamSource(reader);
        final TransformerFactory transformerFactory = TransformerFactory
            .newInstance();
        final Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer(xslFileStreamSource);
            transformer.setURIResolver(new CcmUriResolver(theme.getName(),
                                                          theme.getVersion(),
                                                          themeProvider));
            transformer.setErrorListener(new ErrorListener() {

                @Override
                public void warning(final TransformerException te)
                    throws TransformerException {

                    LOGGER.warn("A WARNING was reported by the "
                                    + "XSLT Transformer:",
                                te);
                }

                @Override
                public void error(final TransformerException te)
                    throws TransformerException {

                    LOGGER.warn("An ERROR was reported by the "
                                    + "XSLT Transformer:",
                                te);
                }

                @Override
                public void fatalError(final TransformerException te)
                    throws TransformerException {

                    LOGGER.warn("An FATAL ERROR was reported by the "
                                    + "XSLT Transformer:",
                                te);
                }

            });
        } catch (TransformerConfigurationException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final StringWriter resultWriter = new StringWriter();
        final Result result = new StreamResult(resultWriter);
        try {
            transformer.transform(new DOMSource(document), result);
        } catch (TransformerException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return resultWriter.toString();
    }

}
