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
public class Marshaller {

    @Inject
    @Any
    private Instance<AbstractMarshaller<? extends Identifiable>>
            marshallerInstances;

    // Assigns lists with objects of the same type as values to their typ as
    // key.
    private Map<Class<? extends Identifiable>, List<Identifiable>> classListMap;


    /**
     * Main export method. Organizes the objects into list of the same type
     * and calls a second export method for each list.
     *
     * @param objects All objects to be exported
     * @param format The export style/format e.g. CSV or JSON
     * @param filename The name of the file to be exported to
     */
    public void exportObjects(List<Identifiable> objects, Format format,
                               String filename) {
        putObjects(objects);

        for (Map.Entry<Class<? extends Identifiable>, List<Identifiable>>
            classListEntry : classListMap.entrySet()) {
            exportList( classListEntry.getValue(), classListEntry.getKey(),
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
    private void putObjects(List<Identifiable> objects) {
        for (Identifiable object : objects) {
            Class<? extends Identifiable> type = object.getClass();

            if (classListMap.containsKey(type)) {
                classListMap.get(type).add(object);
            } else {
                List<Identifiable> values = new ArrayList<>();
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
     * @param list List of objects to be exported of the same type
     * @param type The class of the type
     * @param format The export style
     * @param filename The filename
     * @param <I> The type
     */
    private <I extends Identifiable> void exportList(List<I> list, Class<?
            extends I> type, Format format, String filename) {

        final Instance<AbstractMarshaller<? extends Identifiable>>
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
            final Iterator<AbstractMarshaller<? extends Identifiable>>
                    iterator = marshallerInstance.iterator();
            @SuppressWarnings("unchecked")
            final AbstractMarshaller<I> marshaller = (AbstractMarshaller<I>)
                    iterator.next();

            marshaller.init(format, filename);
            marshaller.exportList(list);
        }
    }

    /**
     * {@link AnnotationLiteral} used for filtering the available marshallers.
     */
    private class MarshalsLiteral extends AnnotationLiteral<Marshals>
            implements Marshals {

        private static final long serialVersionUID = -8093783826632252875L;
        private final Class<? extends Identifiable> entityClass;

        public MarshalsLiteral(Class<? extends Identifiable> entityClass) {
            this.entityClass = entityClass;
        }

        @Override
        public Class<? extends Identifiable> value() {
            return entityClass;
        }
    }
}
