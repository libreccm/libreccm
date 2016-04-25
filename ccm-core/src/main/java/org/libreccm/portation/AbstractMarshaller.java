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
package org.libreccm.portation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.log4j.Logger;
import org.libreccm.core.Identifiable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class responsible for ex- and importing entity-objects to several
 * file-formats. Every entity-class (e.g. DocRepo.File) needs to have its own
 * extension of this class to override the abstract methods, making it
 * possible to ex- or import that extending entity-class (e.g. DocRepo
 * .FileMarshal).
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 2/10/16
 */
public abstract class AbstractMarshaller<I extends Identifiable> {

    private static final Logger log = Logger.getLogger(AbstractMarshaller.class);

    private Format format;
    private String filename;

    // XML specifics
    ObjectMapper xmlMapper;

    // JSON specifics

    // CSV specifics



    public void prepare(final Format format, String filename, boolean
            indentation) {
        this.format = format;
        this.filename = filename;

        switch (this.format) {
            case XML:
                // for additional configuration
                JacksonXmlModule module = new JacksonXmlModule();
                module.setDefaultUseWrapper(false);
                xmlMapper = new XmlMapper(module);
                if (indentation) {
                    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                }
                break;

            case JSON:
                break;

            case CSV:
                break;

            default:
                break;
        }
    }


    public void exportList(final List<I> exportList) {
        File file = new File(filename);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            log.error(String.format("Unable to open a fileWriter for the file" +
                    " with the name %s.", file.getName()));
        }
        if (fileWriter != null) {
            for (I object : exportList) {
                String line = null;

                switch (format) {
                    case XML:
                        try {
                            line = xmlMapper.writeValueAsString(object);
                            //log.info(line);
                        } catch (IOException e) {
                            log.error(String.format("Unable to write objetct " +
                                    "of class %s as XML string with name %s.",
                                    object.getClass(), file.getName()), e);
                        }
                        break;

                    case JSON:
                        break;

                    case CSV:
                        break;

                    default:
                        break;
                }

                if (line != null) {
                    try {
                        fileWriter.write(line);
                        fileWriter.write(System.getProperty("line.separator"));
                    } catch (IOException e) {
                        log.error(String.format("Unable to write to file with the" +
                                " name %s.", file.getName()));
                    }
                }
            }

            try {
                fileWriter.close();
            } catch (IOException e) {
                log.error(String.format("Unable to close a fileWriter for the" +
                        " file with the name %s.", file.getName()));
            }

        }
    }

    protected abstract Class<I> getObjectClass();
    protected abstract void insertIntoDb(I object);

    public List<I> importFile() {
        File file = new File(filename);

        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            log.error(String.format("Unable to read lines of the file with " +
                    "name %s.", file.getName()));
        }

        List<I> objects = new ArrayList<I>();
        if (lines != null) {
            for (String line : lines) {
                I object = null;
                switch (format) {
                    case XML:
                        try {
                            object = xmlMapper.readValue(line, getObjectClass());
                        } catch (IOException e) {
                            log.error(String.format("Unable to read objects " +
                                    "from XML line:\n \"%s\"", line), e);
                        }
                        break;

                    case JSON:
                        break;

                    case CSV:
                        break;

                    default:
                        break;
                }

                assert object != null;
                insertIntoDb(object);
                objects.add(object);
            }
        }
        return objects;
    }

}
