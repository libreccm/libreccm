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
package org.libreccm.exchange.exporter;

import com.opencsv.CSVWriter;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.libreccm.security.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Main class for exporting database objects as .csv-textfiles. Subclasses
 * are required to implement the method {@code asList} matching their own
 * needs. This is necessary, because every object class stored in the
 * database has its own parameters which refer sometimes to other object
 * classes. But these other object classes do not need to be exported in
 * their entirety, only their uuid for later re-identification.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
public abstract class ObjectExporter<T> {

    private static final Logger log = Logger.getLogger(ObjectExporter.class);

    private String filename = "ccm_ng-defaultExportFilename.csv";
    private char separator  = ',';

    //> Begin GETTER & SETTER

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    //< End GETTER & SETTER

    /**
     * Empty constructor.
     */
    public ObjectExporter() {}

    /**
     * Exports a list of objects of type {@code T}, e.g. a list of
     * {@link User}s, as a .csv-textfile with the specified {@code filename}.
     *
     * @param exportObjects List of objects of type {@code T} to be exported
     */
    public void export(List<T> exportObjects) throws NullArgumentException {
        CSVWriter csvWriter = null;
        if (filename == null) {
            throw new NullArgumentException(filename);
        }
        try {
            csvWriter = new CSVWriter(new FileWriter(filename),
                    separator);
            csvWriter.writeAll(asList(exportObjects));
            csvWriter.close();
        } catch (IOException e) {
            //Todo: what to do
            e.printStackTrace();
        }
    }

    /**
     * Abstract method to force extending subclasses to implement this
     * method, so the needed list for the export is matching their special
     * needs.
     *
     * @param exportObjects List of objects of type {@code T} to be exported
     * @return  A list of strings containing all database information of the
     *          wanted object class.
     */
    public abstract List<String[]> asList(List<T> exportObjects);

    /**
     * Abstract method to reduce the types of a single export object to
     * strings.
     *
     * @param exportObject A single exportObject
     * @return  A list of strings representing the parameters of the
     *          export object
     */
    protected abstract String[] reduceToString(T exportObject);
}
