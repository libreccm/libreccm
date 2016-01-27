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
package org.libreccm.portation.importer;

import com.opencsv.CSVReader;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class for importing .csv-textfiles by extracting all their information
 * and storing them in new instances of existing database objects. Its
 * important, to pay attention to the right order of the information in the
 * .csv-textfile to correctly match information with the attributes of the
 * database objects. The UUID is being used for reconnecting the referenced
 * id's of other objects types with there actual object instance.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 07/01/2016
 */
public abstract class AbstractImporter<T> {

    private static final Logger log = Logger.getLogger(AbstractImporter.class);

    private String filename = null;
    private char separator = ',';

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
    public AbstractImporter() {}

    /**
     * Imports object information as {@link String} from a .csv-textfile as new
     * objects of type {@code T} into the database.
     *
     * @return  A list of objects of type {@code T} having been imported from a
     *          .csv-textfile.
     */
    public List<T> importFromCSV() {
        try {
            CSVReader csvReader = new CSVReader(new FileReader(filename),
                    separator);
            List<String[]> importList = csvReader.readAll();
            if (importList.size() < 3) {
                log.error("The given file does not contain any importable " +
                        "data.");
                return null;
            }

            log.info(String.format("All objects imported from the file %s " +
                    "have been successfully imported into this systems " +
                    "database.", filename));
            return fromList(importList);

        } catch (IOException e) {
            log.error(String.format("Either a FileReader with the name %s has " +
                    "not been able to be created or the file could not be " +
                    "read.", filename));
            return null;
        }
    }

    /**
     * Transforms the list of {@link String}s into a list of import objects
     * by calling  the overriding methods declared as abstract in this class.
     *
     * @param importList List of {@link String}s being imported
     * @return  A list of objects of type {@code T} containing all the
     *          information imported from the .csv-textfile.
     */
    private List<T> fromList(List<String[]> importList) {
        // removes class name and attribute names
        importList.remove(0);
        if (checkAttributeNames(importList.remove(0))) {
            log.error("The attributes in the import file does not match " +
                    "the attributes of this system");
            return null;
        }
        return importList.stream().map(this::expandFromStrings).collect
                (Collectors.toList());
    }

    /**
     * Abstract method to check if the names of the attributes read from the
     * .csv-textfile correctly correspond to the attributes of this systems
     * database object of type {@code T}.
     *
     * @param attributeNames List of names hopefully representing the right
     *                       attribute names
     * @return true on success, false otherwise
     */
    protected abstract boolean checkAttributeNames(String[] attributeNames);

    /**
     * Abstact method to create a new object of type {@code T} with the imported
     * attributes given as parameter and storing that object into the
     * systems database.
     *
     * @param importStrings The attributes belonging to the new database object
     * @return The new database object, already inserted into the database
     */
    protected abstract T expandFromStrings(String[] importStrings);



}
