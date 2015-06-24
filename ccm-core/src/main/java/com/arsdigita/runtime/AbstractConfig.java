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
package com.arsdigita.runtime;

import com.arsdigita.util.parameter.AbstractParameterContext;
import com.arsdigita.util.parameter.ErrorList;
import org.apache.log4j.Logger;

/**
 * AbstractConfig is a base class for groups of customizable
 * configuration {@link com.arsdigita.util.parameter parameters}. A
 * CCM Developer wishing to add a new group of configuration
 * parameters to his application will extend this class and provide a
 * public noargs constructer that registers his parameters with the
 * superclass. For example:
 *
 * <blockquote><pre>
 * package com.arsdigita.exampleApp;
 *
 * public final class ExampleConfig extends AbstractConfig {
 *
 *     private Parameter m_string = new StringParameter
 *         ("example.string", Parameter.OPTIONAL, "default");
 *     private Parameter m_integer = new IntegerParameter
 *         ("example.integer", Parameter.OPTIONAL, new Integer(0));
 *     private Parameter m_boolean = new BooleanParameter
 *         ("example.boolean", Parameter.OPTIONAL, Boolean.TRUE);
 *
 *     public ExampleConfig() {
 *         register(m_string);
 *         register(m_integer);
 *         register(m_boolean);
 *         loadInfo();
 *     }
 *
 *     public String getString() {
 *         return (String) get(m_string);
 *     }
 *
 *     public int getInteger() {
 *         return ((Integer) get(m_integer)).intValue();
 *     }
 *
 *     public boolean getBoolean() {
 *         return Boolean.TRUE.equals(get(m_boolean));
 *     }
 *
 * }
 * </pre></blockquote>
 *
 * When this pattern is followed, the resulting subclass of abstract
 * config may be used by developers writing java code to access the
 * values of customizable configuration parameters in a convenient and
 * type safe manner. In addition, the very same class is also usable
 * by the ccm configuration tools to allow customization and
 * validation of the new parameters.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public abstract class AbstractConfig extends AbstractParameterContext {

    private static final Logger s_log = Logger.getLogger
        (AbstractConfig.class);

    /**
     * Default constructor for subclasses.
     */
    protected AbstractConfig() {}

    /**
     * Loads this AbstractConfig object with values from the default
     * configuration registry. Any errors encountered during
     * unmarshaling and loading of configuration values are added to
     * the <code>errors</code> ErrorList. This method should not be
     * called from the constructor of a config object since the ccm
     * configuration tools need to be able to construct empty config
     * objects.
     *
     * @param errors The ErrorList used to record errors during
     *               unmarshaling and loading.
     *
     * @see ConfigRegistry
     */
    public final void load(ErrorList errors) {
        ConfigRegistry reg = new ConfigRegistry();
        reg.load(this, errors);
    }

    /**
     * Invokes the {@link #load(ErrorList)} method with a new and
     * empty ErrorList for accumulating errors, and returns that
     * ErrorList. This method can be used in combination with the
     * {@link ErrorList#check()} method to load and assert that this
     * configuration object is valid in one simple idiom. For example:
     *
     * <blockquote><pre>
     *     ExampleConfig conf = new ExampleConfig();
     *     conf.load().check();
     *     ...
     * </pre></blockquote>
     *
     * @return Errors that may have been encountered during
     *         configuration loading.
     *
     * @see #load(ErrorList)
     */
    public final ErrorList load() {
        ErrorList errs = new ErrorList();
        load(errs);
        return errs;
    }

    /**
     * @deprecated Use @{link #load()} instead.
     */
    public final ErrorList load(final String resource) {
        return load();
    }

    /**
     * @deprecated Use @{link #load()} instead.
     */
    public final ErrorList require(final String resource) {
        return load();
    }
}
