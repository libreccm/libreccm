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
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.theming.ProcessesThemes;
import org.libreccm.theming.Themes;
import org.libreccm.theming.manifest.ThemeTemplate;
import org.libreccm.theming.utils.L10NUtils;
import org.libreccm.theming.utils.SettingsUtils;
import org.libreccm.theming.utils.SystemInfoUtils;
import org.libreccm.theming.utils.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.inject.Inject;
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

    private static final String FUNCTION_XMLNS = "http://xmlns.libreccm.org";
    private static final String FUNCTION_XMLNS_PREFIX = "ccm";

    private static final Logger LOGGER = LogManager
        .getLogger(XsltThemeProcessor.class);

    @Inject
    private L10NUtils l10nUtils;

    @Inject
    private SettingsUtils settingsUtils;

    @Inject
    private SystemInfoUtils systemInfoUtils;

    @Inject
    private TextUtils textUtils;

    @Inject
    private Themes themes;

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

        final InputStream xslFileInputStream = themes
            .getFileFromTheme(theme,
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
            .newInstance(TransformerFactoryImpl.class.getName(),
                         getClass().getClassLoader());
        transformerFactory.setURIResolver(new CcmUriResolver(theme.getName(),
                                                             theme.getVersion(),
                                                             themeProvider));
        final TransformerFactoryImpl transformerFactoryImpl
                                         = (TransformerFactoryImpl) transformerFactory;
        final Configuration configuration = transformerFactoryImpl
            .getConfiguration();
        configuration
            .registerExtensionFunction(new GetContextPathFunctionDefinition());
        configuration
            .registerExtensionFunction(
                new GetSettingFunctionDefinition(theme, themeProvider));
        configuration
            .registerExtensionFunction(
                new LocalizeFunctionDefinition(theme, themeProvider));
        configuration
            .registerExtensionFunction(new TruncateTextFunctionDefinition());
        final Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer(xslFileStreamSource);
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

    private class GetContextPathFunctionDefinition
        extends ExtensionFunctionDefinition {

        @Override
        public StructuredQName getFunctionQName() {
            return new StructuredQName(FUNCTION_XMLNS_PREFIX,
                                       FUNCTION_XMLNS,
                                       "getContextPath");
        }

        @Override
        public SequenceType[] getArgumentTypes() {
            return new SequenceType[]{};
        }

        @Override
        public SequenceType getResultType(final SequenceType[] arguments) {
            return SequenceType.SINGLE_STRING;
        }

        @Override
        public ExtensionFunctionCall makeCallExpression() {
            return new ExtensionFunctionCall() {

                @Override
                public Sequence call(final XPathContext xPathContext,
                                     final Sequence[] arguments)
                    throws XPathException {

                    return StringValue
                        .makeStringValue(systemInfoUtils.getContextPath());
                }

            };
        }

    }

    private class GetSettingFunctionDefinition
        extends ExtensionFunctionDefinition {

        private final ThemeInfo theme;
        private final ThemeProvider themeProvider;

        public GetSettingFunctionDefinition(final ThemeInfo themeInfo,
                                            final ThemeProvider themeProvider) {
            this.theme = themeInfo;
            this.themeProvider = themeProvider;
        }

        @Override
        public StructuredQName getFunctionQName() {
            return new StructuredQName(FUNCTION_XMLNS_PREFIX,
                                       FUNCTION_XMLNS,
                                       "getSetting");
        }

        @Override
        public SequenceType[] getArgumentTypes() {
            return new SequenceType[]{SequenceType.SINGLE_STRING,
                                      SequenceType.SINGLE_STRING,
                                      SequenceType.SINGLE_STRING};
        }

        @Override
        public int getMinimumNumberOfArguments() {
            return 2;
        }

        @Override
        public int getMaximumNumberOfArguments() {
            return 3;
        }

        @Override
        public SequenceType getResultType(final SequenceType[] arguments) {
            return SequenceType.SINGLE_STRING;
        }

        @Override
        public ExtensionFunctionCall makeCallExpression() {

            return new ExtensionFunctionCall() {

                @Override
                public Sequence call(final XPathContext xPathContext,
                                     final Sequence[] arguments)
                    throws XPathException {

                    final String result;
                    final String filePath = ((Item) arguments[0])
                        .getStringValue();
                    final String settingName = ((Item) arguments[1])
                        .getStringValue();
                    switch (arguments.length) {
                        case 2:
                            result = settingsUtils.getSetting(theme,
                                                              themeProvider,
                                                              filePath,
                                                              settingName);
                            break;
                        case 3:
                            final String defaultValue = ((Item) arguments[2])
                                .getStringValue();
                            result = settingsUtils.getSetting(theme,
                                                              themeProvider,
                                                              filePath,
                                                              settingName,
                                                              defaultValue);
                            break;
                        default:
                            throw new UnexpectedErrorException(
                                "Illegal number of arguments.");
                    }

                    return StringValue.makeStringValue(result);
                }

            };
        }

    }

    /**
     * Definition for XSL function for localising texts in the theme. Allows use
     * of {@link ResourceBundle}s instead of a custom XML format which was used
     * in legacy versions.
     *
     * The XSL function {@code ccm:localize} expects one mandatory parameter,
     * the {@code key} of the text to localise. The optional second parameter
     * {@code bundle} identifies the {@link ResourceBundle} to use. The
     * {@code bundle} parameter is passed to
     * {@link ResourceBundle#getBundle(java.lang.String)}. All bundle paths are
     * resolved relative to the root of the theme using {@link ThemeProvider}.
     * If the {@code bundle} parameter is omitted the function will look for
     * {@link PropertyResourceBundle} named {@code theme-bundle.properties} in
     * the root of the theme. Examples:
     *
     * {@code <xsl:value-of select="ccm:localize('footer.privacy')" />}
     *
     * In this case this function will load the file
     * {@code theme-bundle.properties} from the root of the theme and use it to
     * create an instance of {@link PropertyResourceBundle}. If this is
     * successful the key {@code footer.privacy} is lookup in the resource
     * bundle.
     *
     * {@code <xsl:value-of select="ccm:localize('footer.privacy', '/texts/footer')" />}
     *
     * In this case the function tries find a property file called
     * {@code footer.properties} in the texts directory in the theme.
     *
     * Of course the function, or better {@link ResourceBundle} will also take
     * into account the current locale, therefore in both examples the first
     * file name will be {@code footer_$locale.properties} where {@code $locale}
     * is the the locale returned by
     * {@link GlobalizationHelper#getNegotiatedLocale()}.
     */
    private class LocalizeFunctionDefinition
        extends ExtensionFunctionDefinition {

        private final ThemeInfo theme;
        private final ThemeProvider themeProvider;

        public LocalizeFunctionDefinition(final ThemeInfo theme,
                                          final ThemeProvider themeProvider) {
            super();
            this.theme = theme;
            this.themeProvider = themeProvider;
        }

        @Override
        public StructuredQName getFunctionQName() {
            return new StructuredQName(FUNCTION_XMLNS_PREFIX,
                                       FUNCTION_XMLNS,
                                       "localize");
        }

        @Override
        public SequenceType[] getArgumentTypes() {
            return new SequenceType[]{SequenceType.SINGLE_STRING};
        }

        @Override
        public int getMaximumNumberOfArguments() {
            return 2;
        }

        @Override
        public SequenceType getResultType(final SequenceType[] arguments) {
            return SequenceType.SINGLE_STRING;
        }

        @Override
        public ExtensionFunctionCall makeCallExpression() {
            return new ExtensionFunctionCall() {

                @Override
                public Sequence call(final XPathContext xPathContext,
                                     final Sequence[] arguments)
                    throws XPathException {

                    final String bundle;
                    if (arguments.length > 1) {
                        bundle = ((Item) arguments[1]).getStringValue();
                    } else {
                        bundle = "theme-bundle";
                    }
                    final String key = ((Item) arguments[0]).getStringValue();

                    LOGGER.debug("Localizing key \"{}\" from bundle \"{}\"...",
                                 key,
                                 bundle);

                    return StringValue
                        .makeStringValue(l10nUtils.getText(theme,
                                                           themeProvider,
                                                           bundle,
                                                           key));
                }

            };
        }

    }

    private class TruncateTextFunctionDefinition
        extends ExtensionFunctionDefinition {

        @Override
        public StructuredQName getFunctionQName() {
            return new StructuredQName(FUNCTION_XMLNS_PREFIX,
                                       FUNCTION_XMLNS,
                                       "truncateText");
        }

        @Override
        public SequenceType[] getArgumentTypes() {
            return new SequenceType[]{SequenceType.SINGLE_STRING,
                                      SequenceType.SINGLE_INTEGER};
        }

        @Override
        public SequenceType getResultType(final SequenceType[] arguments) {
            return SequenceType.SINGLE_STRING;
        }

        @Override
        public ExtensionFunctionCall makeCallExpression() {
            return new ExtensionFunctionCall() {

                @Override
                public Sequence call(final XPathContext xPathContext,
                                     final Sequence[] arguments)
                    throws XPathException {

                    final String text = ((Item) arguments[0]).getStringValue();
                    final int length = Integer
                        .parseInt(((Item) arguments[1]).getStringValue());
                    return StringValue
                        .makeStringValue(textUtils.truncateText(text, length));
                }

            };
        }

    }

}
