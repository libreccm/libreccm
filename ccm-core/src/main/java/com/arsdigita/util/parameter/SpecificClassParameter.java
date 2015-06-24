/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util.parameter;

/**
 * A parameter representing a Java <code>Class</code> which is checked to be
 * an implementation of a required class / interface.
 *
 * Subject to change.
 *
 * @see java.lang.Class
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class SpecificClassParameter extends ClassParameter {


        private Class m_requiredClass;

        /**
         * Constructor
         * @param name
         * @param multiplicity
         * @param defaultObj
         * @param requiredClass
         */
        public SpecificClassParameter(final String name,
                                      final int multiplicity,
                                      final Object defaultObj,
                                      final Class requiredClass) {
            super(name, multiplicity, defaultObj);
            m_requiredClass = requiredClass;
        }

        /**
         * Unmarshals a string representation of the parameter.
         * 
         * @param value  string representation of class, must be value != null
         * @param errors
         * @return
         */
        @Override
        protected Object unmarshal(String value, ErrorList errors) {
            Class theClass = (Class) super.unmarshal(value,errors);
            if (theClass != null) {
                if (!m_requiredClass.isAssignableFrom(theClass)) {
                    errors.add(new ParameterError(this, "class " + value +
                                                  "  must implement : " +
                                                  m_requiredClass.getName()));
                }
            }

            return theClass;
        }

}
