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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created the 2/10/16
 * @param <P>
 */
public abstract class AbstractMarshaller<P extends Portable> {

    private static final Logger LOGGER = LogManager.getLogger(AbstractMarshaller
            .class);

    private Format format;
    private String filename;

    // XML specifics
    ObjectMapper xmlMapper;

    /**
     * Prepares import and export routine. Sets the format in which the ex-
     * or import will take place and sets the name of the file exported to or
     * imported from. Furthermore it is possible to decide for or against
     * indentation in the output file.
     *
     * @param format The format of the ex-/import
     * @param filename The filename of the file exported to or imported from
     * @param indentation whether or not indentation
     */
    public void prepare(final Format format,
                        String filename,
                        boolean indentation) {
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


            default:
                break;
        }
    }

    /**
     * Same as {@code prepare}-methode above. Adds distinction between path
     * and filename.
     *
     * @param format The format of the ex-/import
     * @param folderPath The folderPath of the file exported to or imported
     *                   from
     * @param filename The filename of the file exported to or imported from
     * @param indentation whether or not indentation
     */
    public void prepare(final Format format,
                        String folderPath, String filename,
                        boolean indentation) {
        File file = new File(folderPath);
        if (file.exists() || file.mkdirs()) {
            prepare(format, folderPath + "/" + filename, indentation);
        }
    }


    /**
     * Export routine for lists with the same object type {@code P}. Creates a
     * new file with the prepared filename and starts, depending on the set
     * format, the export process, object after object.
     *
     * @param exportList List of equally typed objects.
     */
    public void exportList(final List<P> exportList) {
        File file = new File(filename);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            LOGGER.error("Unable to open a fileWriter for the file" +
                    " with the name {}.", file.getName());
            LOGGER.error(e);
        }
        if (fileWriter != null) {
            for (P object : exportList) {
                String line = null;

                switch (format) {
                    case XML:
                        try {
                            line = xmlMapper.writeValueAsString(object);
                            //LOGGER.info(line);
                        } catch (IOException e) {
                            LOGGER.error("Unable to write objetct " +
                                    "of class {} as XML string with name {}.",
                                    object.getClass(), file.getName());
                            LOGGER.error(e);
                        }
                        break;

                    default:
                        break;
                }
                if (line != null) {
                    try {
                        fileWriter.write(line);
                        fileWriter.write(System.getProperty("line.separator"));
                    } catch (IOException e) {
                        LOGGER.error("Unable to write to file with" +
                                " the name {}.", file.getName());
                        LOGGER.error(e);
                    }
                }
            }
            try {
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.error("Unable to close a fileWriter for the" +
                        " file with the name {}.", file.getName());
                LOGGER.error(e);
            }
        }
    }

    /**
     * Import routine for files containing objects of the same type {@code P}.
     * Creates a new list of strings to read the file line by line. Each line
     * of the list represents an object of the file. Then retrieves each object
     * from the corresponding string in the list.
     *
     * @return If error occurs true, otherwise false.
     */
    public boolean importFile() {
        boolean error = false;
        File file = new File(filename);

        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            LOGGER.error("Unable to read lines of the file with " +
                    "name {}.", file.getName());
            LOGGER.error(e);
            error = true;
        }

        List<P> objects = new ArrayList<>();
        if (lines != null) {
            int emptyObjects = 0;
            for (String line : lines) {
                P object = null;
                switch (format) {
                    case XML:
                        try {
                            object = xmlMapper.readValue(line, getObjectClass());
                        } catch (IOException e) {
                            LOGGER.error("Unable to read objects " +
                                    "from XML line:\n \"{}\"", line);
                            LOGGER.error(e);
                            error = true;
                        }
                        break;

                    default:
                        break;
                }

                if (object != null) {
                    insertIntoDb(object);
                    objects.add(object);
                } else {
                    emptyObjects+=1;
                    LOGGER.info("Count of empty objects: {}", emptyObjects);
                    error = true;
                }
            }
        }
        return error;
    }

    /**
     * Abstract method to get the object class to determine the type {@code P}.
     *
     * @return The object class
     */
    protected abstract Class<P> getObjectClass();

    /**
     * Abstract method to insert every imported object into the database.
     *
     * @param portableObject An object of type {@code P}
     */
    protected abstract void insertIntoDb(P portableObject);
}
