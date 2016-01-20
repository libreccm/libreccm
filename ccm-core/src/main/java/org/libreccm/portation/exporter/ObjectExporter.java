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
package org.libreccm.portation.exporter;

import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.libreccm.security.User;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class for exporting database objects as .csv-textfiles. Subclasses
 * are required to implement the methods {@code getClassName}, {@code
 * getAttributeNames} and {@code reduceToStrings} matching their own
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

    private String filename = null;
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
     * @throws FileNotFoundException
     */
    public void exportToCSV(List<T> exportObjects) throws
            FileNotFoundException {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(filename),
                    separator);
            csvWriter.writeAll(asList(exportObjects));
            csvWriter.close();
            log.info(String.format("The given objects have been successfully " +
                    "exported into " +
                    "the file %s.", filename));
        } catch (IOException e) {
            log.error(String.format("A FileWriter with the name %s has not " +
                    "been able to be created.", filename));

            // Todo: throw Exception to modify in ui
            throw new FileNotFoundException();
        }
    }

    /**
     * Transforms the list of export objects into a list of {@link String}s
     * by calling the overriding methods declared as abstract in this class.
     *
     * @param exportObjects List of objects of type {@code T} to be exported
     * @return  A list of strings containing all database information of the
     *          wanted object class.
     */
    private List<String[]> asList(List<T> exportObjects) {
        List<String[]> exportList = new ArrayList<>();

        exportList.add(getClassName());
        exportList.add(getAttributeNames());
        exportList.addAll(exportObjects.stream().map(
                this::reduceToStrings).collect(Collectors.toList()));

        return exportList;
    }

    /**
     * Abstract method to get the class name for the first line in the
     * .csv-textfile.
     *
     * @return A list containing just one string, the class name
     */
    protected abstract String[] getClassName();

    /**
     * Abstract method to get the class header for the first and second line in
     * the .csv-textfile.
     *
     * @return A list of strings representing the object attributes
     */
    protected abstract String[] getAttributeNames();

    /**
     * Abstract method to reduce the types of a single export object to
     * strings. Implementing subclass has to pay attention to attribute
     * fields containing null values. Null values are not always forbidden,
     * but when reducing to strings it will cause a NullPointerException, if
     * not handled accurately.
     *
     * @param exportObject A single exportObject
     * @return  A list of strings representing the parameters of the
     *          export object
     */
    protected abstract String[] reduceToStrings(T exportObject);
}
