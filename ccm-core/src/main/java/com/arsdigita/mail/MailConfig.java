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
package com.arsdigita.mail;

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class MailConfig {

    @Setting
    private Boolean debug = false;

    @Setting
    private String javaMailPropertiesFile = null;

    @Setting
    private String defaultFrom = "";

    @Setting
    private Boolean sendHtml = false;

    public static MailConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(MailConfig.class);
    }

    public Boolean isDebug() {
        return debug;
    }

    public void setDebug(final Boolean debug) {
        this.debug = debug;
    }

    public String getJavaMailPropertiesFile() {
        return javaMailPropertiesFile;
    }

    public Properties getJavaMailProperties() {
        final Properties properties = new Properties();

        if (javaMailPropertiesFile == null
                || javaMailPropertiesFile.isEmpty()) {
            if (System.getProperty("ccm.mail.config") == null) {
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.host", "localhost");
            } else {
                try {
                    properties.load(new URL(System
                        .getProperty("ccm.mail.config")).openStream());
                } catch (IOException ex) {
                    throw new UncheckedWrapperException(String.format(
                        "Unable to retrieve properties for JavaMail from \"%s\".",
                        System.getProperty("ccm.mail.config")), ex);
                }
            }
        } else {
            try {
                properties.load(new URL(javaMailPropertiesFile).openStream());
            } catch (IOException ex) {
                throw new UncheckedWrapperException(String.format(
                    "Unable to retrieve properties for JavaMail from \"%s\".",
                    javaMailPropertiesFile), ex);
            }
        }

        return properties;
    }

    public void setJavaMailPropertiesFile(final String javaMailPropertiesFile) {
        this.javaMailPropertiesFile = javaMailPropertiesFile;
    }

    public String getDefaultFrom() {
        return defaultFrom;
    }

    public void setDefaultFrom(final String defaultFrom) {
        this.defaultFrom = defaultFrom;
    }

    public Boolean getSendHtml() {
        return sendHtml;
    }

    public void setSendHtml(final Boolean sendHtml) {
        this.sendHtml = sendHtml;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(debug);
        hash = 67 * hash + Objects.hashCode(javaMailPropertiesFile);
        hash = 67 * hash + Objects.hashCode(defaultFrom);
        hash = 67 * hash + Objects.hashCode(sendHtml);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MailConfig)) {
            return false;
        }
        final MailConfig other = (MailConfig) obj;
        if (!Objects.equals(javaMailPropertiesFile,
                            other.getJavaMailPropertiesFile())) {
            return false;
        }
        if (!Objects.equals(defaultFrom, other.getDefaultFrom())) {
            return false;
        }
        if (!Objects.equals(debug, other.isDebug())) {
            return false;
        }
        return Objects.equals(sendHtml, other.getSendHtml());
    }

    @Override
    public String toString() {
        return String.format(
            "%s{ "
                + "debug = %b, "
                + "javaMailPropertiesFile = \"%s\", "
                + "defaultFrom = \"%s\", "
                + "sendHtml = %b"
                + " }",
            super.toString(),
            debug,
            javaMailPropertiesFile,
            defaultFrom,
            sendHtml);
    }

}
