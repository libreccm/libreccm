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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
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


    /**
     *
     * @param objects
     * @param format
     * @param filename
     */
    @SuppressWarnings("unchecked")
    public void exportObjects(List<? extends
            Identifiable> objects, Format format, String filename) {

        List<List<? extends Identifiable>> objectsByClass = new ArrayList<>();
        Queue<Class<? extends Identifiable>> queue = new LinkedList<>();

        // Splits list of all entities into lists of the same entity class
        while (!objects.isEmpty()) {
            Class<? extends Identifiable> clazz = objects.get(0).getClass();
            objectsByClass.add(objects.stream()
                    .filter(t -> t.getClass() == clazz)
                    .collect(Collectors.toList()));
            queue.add(clazz);
        }

        // Exports list of the same class
        for (List objectList : objectsByClass) {
            final Instance<AbstractMarshaller<? extends Identifiable>>
                    marshallerInstance = marshallerInstances.select(new
                    MarshalsLiteral(queue.peek()));

            if (marshallerInstance.isUnsatisfied()) {
                // If there are no marshals we have a problem...
                throw new IllegalArgumentException(String.format("No " +
                        "marshaller for \"%s\" found.", queue.peek()
                        .getName()));
            } else if (marshallerInstance.isAmbiguous()) {
                // If there is more than one marshaller something is wrong...
                throw new IllegalArgumentException(String.format("More than " +
                        "one marshaller for \"%s\" found.", queue.peek()
                        .getName()));
            } else {
                // Gets the marshaller and calls the export method
                final Iterator<AbstractMarshaller<? extends Identifiable>> it =
                        marshallerInstance.iterator();

                AbstractMarshaller<? extends Identifiable> marshaller = it
                        .next();
                marshaller.init(format, filename);
                marshaller.exportEntities(objectList);
            }
            queue.remove();
        }
    }

    private <T extends Identifiable> List<T> getListForClass(List<? extends
            Identifiable> objList, Class<T> objClass) {

        return null;
        //objList.stream().filter(obj -> obj.getClass() == objClass)
        //        .collect(Collectors.toList());
    }




    /**
     *
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
