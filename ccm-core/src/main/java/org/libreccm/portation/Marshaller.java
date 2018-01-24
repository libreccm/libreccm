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

import org.libreccm.core.Identifiable;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Central class for exporting and importing objects of this system stored in
 * the database. Retrieves all available implementations of
 * {@link AbstractMarshaller} using CDI. The implementations have to be CDI
 * beans of course. Also they must be annotated with the {@link Marshals}
 * annotation.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created the 03.02.2016
 */
@RequestScoped
@Deprecated /*needs to be reviewed when integrating import/export in gui*/
public class Marshaller implements Serializable {

    private static final long serialVersionUID = 6769177147719834999L;

    @Inject
    @Any
    private Instance<AbstractMarshaller<? extends Portable>> marshallerInstances;

    // Maps lists with objects of the same type to their typ.
    // The type represents the key, the lists the values
    private Map<Class<? extends Portable>, List<Portable>> classListMap;

    /**
     * Main export method. Organizes the objects into list of the same type
     * and calls a second export method for each list.
     *
     * @param objects All objects to be exported
     * @param format The export style/format e.g. CSV or JSON
     * @param filename The name of the file to be exported to
     */
    public void exportObjects(List<Portable> objects,
                              Format format,
                              String filename) {
        putObjects(objects);

        for (Map.Entry<Class<? extends Portable>, List<Portable>>
            classListEntry : classListMap.entrySet()) {
            exportList(classListEntry.getValue(), classListEntry.getKey(),
                    format, filename);
        }
    }

    /**
     * Organizes a list of different {@link Identifiable} objects into a map
     * assigning lists of the same type to their type as values to a key. The
     * type which all objects of that list have in common is their key.
     * That opens the possibility of being certain of the objects types in
     * the list. Guarantied through this implementation.
     *
     * @param objects list of all objects being organized
     */
    private void putObjects(List<Portable> objects) {
        for (Portable object : objects) {
            Class<? extends Portable> type = object.getClass();

            if (classListMap.containsKey(type)) {
                classListMap.get(type).add(object);
            } else {
                List<Portable> values = new ArrayList<>();
                values.add(object);
                classListMap.put(type, values);
            }
        }
    }

    /**
     * Selects the right marshaller for the given type, initializes that
     * marshaller for the given export wishes and calls the export method of
     * that marshaller upon the given list of same typed objects.
     *
     * Naming convention for the export file name:
     *      <basic file name>__<type/class name>.<format>
     *
     * @param list List of objects to be exported of the same type
     * @param type The class of the type
     * @param format The export style
     * @param filename The filename
     * @param <P> The type of the current marshaller
     */
    private <P extends Portable> void exportList(List<P> list,
                                                 Class<? extends P> type,
                                                 Format format,
                                                 String filename)
            throws IllegalArgumentException {

        final Instance<AbstractMarshaller<? extends Portable>>
                marshallerInstance = marshallerInstances.select(new
                MarshalsLiteral(type));

        if (marshallerInstance.isUnsatisfied()) {
            //If there are no marshallers we have a problem...
            throw new IllegalArgumentException(String.format(
                    "No marshallers for \"%s\" found.", type.getName()));
        } else if (marshallerInstance.isAmbiguous()) {
            //If there is more than one marshaller something is wrong...
            throw new IllegalArgumentException(String.format(
                    "More than one marshaller for \"%s\" found.", type
                            .getName()));
        } else {
            // Get the marshaller for this list and call the export method.
            final Iterator<AbstractMarshaller<? extends Portable>>
                    iterator = marshallerInstance.iterator();
            @SuppressWarnings("unchecked")
            final AbstractMarshaller<P> marshaller = (AbstractMarshaller<P>)
                    iterator.next();

            marshaller.prepare(format, filename + "__" + type.toString(),
                    false);
            marshaller.exportList(list);
        }
    }

    /**
     * Selects the right marshaller for each file being imported depending on
     * the filename. Therefore the filename has to contain the name of the
     * class this file stores objects for. The marshaller will then be
     * initialized and be called for importing the objects contained in the
     * file being processed.
     *
     * Naming convention for the import file name:
     *      <basic file name>__<type/class name>.<format>
     *
     * @param filenames List of filenames for the files wishing to be imported
     * @param format The import style
     * @param <P> The type of the current marshaller
     */
    public <P extends Portable> void importFiles(List<String> filenames,
                                                 Format format)
            throws IllegalArgumentException {

        for (String filename : filenames) {
            String[] splitFilename = filename.split("__");
            String className =
                    splitFilename[splitFilename.length].split(".")[0];

            try {
                Class clazz = Class.forName(className);
                @SuppressWarnings("unchecked")
                Class<P> type = clazz.asSubclass(Portable.class);

                final Instance<AbstractMarshaller<? extends Portable>>
                        marshallerInstance = marshallerInstances.select(new
                        MarshalsLiteral(type));

                if (marshallerInstance.isUnsatisfied()) {
                    //If there are no marshallers we have a problem...
                    throw new IllegalArgumentException(String.format(
                            "No marshallers for \"%s\" found.", type.getName()));
                } else if (marshallerInstance.isAmbiguous()) {
                    //If there is more than one marshaller something is wrong...
                    throw new IllegalArgumentException(String.format(
                            "More than one marshaller for \"%s\" found.", type
                                    .getName()));
                } else {
                    // Get the marshaller for this list and call the export method.
                    final Iterator<AbstractMarshaller<? extends Portable>>
                            iterator = marshallerInstance.iterator();
                    @SuppressWarnings("unchecked")
                    final AbstractMarshaller<P> marshaller = (AbstractMarshaller<P>)
                            iterator.next();

                    marshaller.prepare(format, filename, false);
                    marshaller.importFile();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@link AnnotationLiteral} used for filtering the available marshallers.
     */
    private class MarshalsLiteral extends AnnotationLiteral<Marshals>
            implements Marshals {

        private static final long serialVersionUID = -8093783826632252875L;
        private final Class<? extends Portable> entityClass;

        public MarshalsLiteral(Class<? extends Portable> entityClass) {
            this.entityClass = entityClass;
        }

        @Override
        public Class<? extends Portable> value() {
            return entityClass;
        }
    }
}

