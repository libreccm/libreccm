/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Methods for reading configuration options from the theme. Most themes have
 * some configuration options. The class provides several methods for reading
 * settings from different file formats. The following formats are currently
 * supported:
 *
 * <ul>
 * <li>Java Properties files</li>
 * <li>JSON files</li>
 * <li>XML files</li>
 * </ul>
 *
 * For property files and JSON files it is expected that the name of the setting
 * is the key. For XML files the following simple format is expected:
 *
 * <pre>
 *  &lt;settings&gt;
 *      &lt;setting key="nameOfSetting">value of setting&lt;/setting&gt;
 *      &lt;setting key="anotherSetting">value of setting&lt;/setting&gt;
 *      ...
 *  &lt;/settings/&gt;
 * </pre>
 *
 * The file type is determined by the file extension of the file to read.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class SettingsUtils implements Serializable {

    private static final long serialVersionUID = 8705552323418210749L;

    private static final Logger LOGGER = LogManager
        .getLogger(SettingsUtils.class);

    /**
     * Retrieve the value of a setting.
     *
     * @param fromTheme     The theme from which the setting is read.
     * @param themeProvider The provider of the the theme.
     * @param filePath      The path of the settings file relative to the root
     *                      directory of the theme.
     * @param settingName   The name of the setting.
     *
     * @return The value of the setting as String.
     */
    public String getSetting(final ThemeInfo fromTheme,
                             final ThemeProvider themeProvider,
                             final String filePath,
                             final String settingName) {

        return getSetting(fromTheme,
                          themeProvider,
                          filePath,
                          settingName,
                          null);
    }

    /**
     * Retrieve the value of a setting. If the provided file does not have a
     * value for the setting the provided default value is used.
     *
     * @param fromTheme     The theme from which the setting is read.
     * @param themeProvider The provider of the theme.
     * @param filePath      The path of the settings file relative to the root
     *                      directory of the theme.
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value for the setting.
     *
     * @return The value of the setting as String. If the provided file has no
     *         value for the setting the provided default value is used.
     */
    public String getSetting(final ThemeInfo fromTheme,
                             final ThemeProvider themeProvider,
                             final String filePath,
                             final String settingName,
                             final String defaultValue) {

        Objects.requireNonNull(fromTheme);
        Objects.requireNonNull(themeProvider);
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(settingName);

        if (filePath.isEmpty() || filePath.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The path of the settings file can't be empty.");
        }

        if (settingName.isEmpty() || settingName.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the settings file can't be empty.");
        }

        final Optional<InputStream> fileInputStream = themeProvider
            .getThemeFileAsStream(fromTheme.getName(),
                                  fromTheme.getVersion(),
                                  filePath);
        if (!fileInputStream.isPresent()) {
            LOGGER.warn(
                "Configuration file \"{}\" was not found in theme \"{}\".",
                filePath,
                fromTheme.getName());
            return defaultValue;
        }

        if (filePath.toLowerCase(Locale.ROOT).endsWith(".properties")) {
            return getSettingFromPropertiesFile(fileInputStream.get(),
                                                settingName)
                .orElse(defaultValue);
        } else if (filePath.toLowerCase(Locale.ROOT).endsWith(".xml")) {
            return getSettingFromXmlFile(fileInputStream.get(), settingName)
                .orElse(defaultValue);
        } else if (filePath.toLowerCase(Locale.ROOT).endsWith(".json")) {
            return getSettingFromJsonFile(fileInputStream.get(), settingName)
                .orElse(defaultValue);
        } else {
            throw new IllegalArgumentException(
                "The file path must point to file in a supported format. "
                    + "Supported formats are \".properties\", \".xml\" and \".json\".");
        }
    }

    /**
     * Retrieve the boolean value of a setting. This method reads the value of a
     * setting using null null null null null null null null null null null null
     * null null null null null null null null null null null     {@link #getSetting(java.lang.String, java.lang.String, java.lang.String, java.lang.String) 
     * and converts it into a {@code boolean} value using
     * {@link Boolean#parseBoolean(java.lang.String)}.
     *
     * @param fromTheme     The name of the theme from which the setting is
     *                      read.
     * @param themeProvider The provider of the theme.
     * @param filePath      The path of the settings file relative to the root
     *                      directory of the theme.
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     *
     * @return The value of the setting as {@code boolean} or the default value
     * if the configuration file has no value for the setting.
     */
    public boolean getSettingAsBoolean(final ThemeInfo fromTheme,
                                       final ThemeProvider themeProvider,
                                       final String filePath,
                                       final String settingName,
                                       final boolean defaultValue) {

        final String result = getSetting(fromTheme,
                                         themeProvider,
                                         filePath,
                                         settingName,
                                         Boolean.toString(defaultValue));
        return Boolean.parseBoolean(result);
    }

    /**
     * Retrieve the value of setting and convert it to a {@code long} value.
     * This method reads the value using
     * {@link #getSetting(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * and converts the value to a value of type {@code long} using
     * {@link Long#parseLong(java.lang.String)}.
     *
     *
     * @param fromTheme     The name of the theme from which the setting is
     *                      read.
     * @param themeProvider The provider of the theme.
     * @param filePath      The path of the settings file relative to the root
     *                      directory of the theme.
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     *
     * @return The value of the setting as {@code boolean} or the default value
     *         if the configuration file has no value for the setting.
     *
     * @throws NumberFormatException If the the value can't be converted into a
     *                               {@code long} value.
     */
    public long getSettingAsLong(final ThemeInfo fromTheme,
                                 final ThemeProvider themeProvider,
                                 final String filePath,
                                 final String settingName,
                                 final long defaultValue) {

        final String result = getSetting(fromTheme,
                                         themeProvider,
                                         filePath,
                                         settingName,
                                         Long.toString(defaultValue));
        return Long.parseLong(result);
    }

    /**
     * Retrieve the value of setting and convert it to a {@code double} value.
     * This method reads the value using
     * {@link #getSetting(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * and converts the value to a value of type {@code double} using
     * {@link Long#parseLong(java.lang.String)}.
     *
     *
     * @param fromTheme     The name of the theme from which the setting is
     *                      read.
     * @param themeProvider The provider of the theme.
     * @param filePath      The path of the settings file relative to the root
     *                      directory of the theme.
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     *
     * @return The value of the setting as {@code boolean} or the default value
     *         if the configuration file has no value for the setting.
     *
     * @throws NumberFormatException If the the value can't be converted into a
     *                               {@code double} value.
     */
    public double getSettingAsDouble(final ThemeInfo fromTheme,
                                     final ThemeProvider themeProvider,
                                     final String filePath,
                                     final String settingName,
                                     final double defaultValue) {

        final String result = getSetting(fromTheme,
                                         themeProvider,
                                         filePath,
                                         settingName,
                                         settingName);
        return Double.parseDouble(result);
    }

    private Optional<String> getSettingFromJsonFile(
        final InputStream fileInputStream,
        final String settingName) {

        final JsonReader jsonReader = Json.createReader(fileInputStream);
        final JsonObject settings = jsonReader.readObject();
        final String result = settings.getString(settingName);

        return Optional.ofNullable(result);
    }

    private Optional<String> getSettingFromPropertiesFile(
        final InputStream fileInputStream,
        final String settingName) {

        final Properties settings = new Properties();
        try {
            settings.load(fileInputStream);
        } catch (IOException ex) {
            LOGGER.warn("Failed to load setting file.");
            return Optional.empty();
        }

        if (settings.containsKey(settingName)) {
            return Optional.of(settings.getProperty(settingName));
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getSettingFromXmlFile(
        final InputStream fileInputStream,
        final String settingName) {

        final Map<String, String> settings;
//        = new HashMap<>();
//        final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
//        final XMLStreamReader xmlStreamReader;
//        try {
//            xmlStreamReader = xmlInputFactory
//                .createXMLStreamReader(fileInputStream);
//        } catch (XMLStreamException ex) {
//            LOGGER.error("Failed to read XML settings file.");
//            return Optional.empty();
//        }
//
//        while

        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        final ObjectMapper mapper = new XmlMapper(xmlModule);
        try {
            settings = mapper
                .reader()
                .withRootName("settings")
                .readValue(fileInputStream);
        } catch (IOException ex) {
            LOGGER.error("Failed to read Xfinal XML settings file.");
            return Optional.empty();
        }

        if (settings.containsKey(settingName)) {
            return Optional.of(settings.get(settingName));
        } else {
            return Optional.empty();
        }
    }

}
