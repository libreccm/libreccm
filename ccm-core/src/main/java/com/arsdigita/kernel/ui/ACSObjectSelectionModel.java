/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel.ui;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.LongParameter;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.lang.reflect.Constructor;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

/**
 * Loads a subclass of an ACSObject from the database. By default, uses a
 * <del>BigDecimal</del> {@code Long} state parameter in order to store and
 * retrieve the item id.
 *
 * <strong>
 * This class has been edited to work with {@link CcmObject} instead of
 * {@code ACSObject}. Most methods etc. are the the same. This should work as
 * before. Also variable names etc. have been changed to match the common Java
 * styles.
 * </strong>
 *
 * <p>
 * The <code>getSelectedKey(PageState state)</code> method will return the
 * BigDecimal id of the currently selected object. This method will return the
 * id even if the object with this id does not actually exist.
 *
 * <p>
 * The <code>getSelectedObject(PageState state)</code> method will return the
 * object whose id is <code>getSelectedKey(PageState state)</code>. If the
 * object does not actually exist, the method will return null
 *
 * <p>
 * Thus, it is possible to implement the following pattern:
 * <blockquote><pre><code>class MyComponent extends SimpleComponent {
 *  ACSObjectSelectionModel m_model;
 *
 *  public MyComponent() {
 *    super();
 *    m_model = new ACSObjectSelectionModel("item_id");
 *  }
 *
 *  public void register(Page p) {
 *    super.register(p);
 *    p.addComponentStateParam(this, p.getStateParameter());
 *  }
 *
 *  public void doSomethingUseful(PageState s) {
 *    if (m_model.isSelected(s)) {
 *      CusomACSObject obj = (CustomACSObject)m_model.getSelectedObject(state);
 *      // Do something with obj..
 *    }
 *  }
 *}</code></pre></blockquote>
 *
 * Naturally, the <code>ACSObjectSelectionModel</code> could also be passed in
 * as a parameter in the <code>MyComponent</code> constructor. In this case, it
 * should be up to the parent of <code>MyComponent</code> to register the
 * model's state parameter.
 * <p>
 * <b>Advanced Usage</b>: The <code>ACSObjectSelectionModel</code> is actually
 * just a wrapper for a {@link SingleSelectionModel} which maintains the
 * currently selected object's ID as a {@link BigDecimal}. By default, a new
 * {@link ParameterSingleSelectionModel} is wrapped in this way; however, any
 * {@link SingleSelectionModel} may be wrapped. Thus, it becomes possible to use
 * the <code>ACSObjectSelectionModel</code> even if the currently selected ID is
 * not stored in a state parameter.
 * <p>
 * <b>Persistence Details:</b> The default constructor of
 * <code>ACSObjectSelectionModel</code> will attempt to use the
 * {@link DomainObjectFactory} to automatically instantiate the correct Java
 * subclass of {@link ACSObject}. However, it is also possible to use an
 * alternate constructor in order to force the
 * <code>ACSObjectSelectionModel</code> to manually instantiate the objects:
 *
 * <blockquote><pre><code>
 * ACSObjectSelectionModel model =
 *     new ACSObjectSelectionModel("com.arsdigita.cms.Article",
 *                                 "com.arsdigita.cms.Article", "item_id");
 * </code></pre></blockquote>
 *
 * In this case, the model will attempt to use reflection to instantiate the
 * correct subclass of <code>ACSObject</code>. In addition, the supplementary
 * constructor makes it possible to create new objects in the database using the
 * {@link #createACSObject(BigDecimal)} method.
 *
 * @see com.arsdigita.bebop.SingleSelectionModel
 * @see com.arsdigita.bebop.ParameterSingleSelectionModel
 *
 * @author Stanislav Freidin
 * @author Jens Pelzetter
 */
public class ACSObjectSelectionModel implements SingleSelectionModel {

    private RequestLocal loaded;
    private Class javaClassName;
//    private Constructor constructor;
    private String objectType;
    private SingleSelectionModel selectionModel;

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>. This model will
     * produce instances of <code>ACSObject</code> by automatically
     * instantiating the correct Java subclass using the
     * {@link DomainObjectFactory}.
     *
     * @param parameter The state parameter which should be used to store the
     * object ID
     */
    public ACSObjectSelectionModel(final LongParameter parameter) {
        this(null, null, parameter);
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>. This model will
     * produce instances of <code>ACSObject</code> by automatically
     * instantiating the correct Java subclass using the
     * {@link DomainObjectFactory}.
     *
     * @param parameterName The name of the state parameter which will be used
     * to store the object ID.
     */
    public ACSObjectSelectionModel(final String parameterName) {
        this(null, null, new LongParameter(parameterName));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>. This model will
     * produce instances of <code>ACSObject</code> by automatically
     * instantiating the correct Java subclass using the
     * {@link DomainObjectFactory}.
     *
     * @param model The {@link SingleSelectionModel} which will supply a
     * {@link BigDecimal} ID of the currently selected object
     */
    public ACSObjectSelectionModel(final SingleSelectionModel model) {
        this(null, null, model);
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents the object.
     * Must be a subclass of {@link ACSObject}. In addition, the class must have
     * a constructor with a single {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type which
     * represents the ACS object. In practice, will often be the same as the
     * javaClass.
     * @param parameterName The name of the state parameter which will be used
     * to store the object ID.
     */
    public ACSObjectSelectionModel(final String javaClass,
                                   final String objectType,
                                   final String parameterName) {
        this(javaClass, objectType, new LongParameter(parameterName));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents the object.
     * Must be a subclass of {@link ACSObject}. In addition, the class must have
     * a constructor with a single {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type which
     * represents the ACS object. In practice, will often be the same as the
     * javaClass.
     * @param parameter The state parameter which should be used to store the
     * object ID
     */
    public ACSObjectSelectionModel(final String javaClass,
                                   final String objectType,
                                   final LongParameter parameter) {
        this(javaClass,
             objectType,
             new ParameterSingleSelectionModel(parameter));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents the object.
     * Must be a subclass of {@link ACSObject}. In addition, the class must have
     * a constructor with a single {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type which
     * represents the ACS object. In practice, will often be the same as the
     * javaClass.
     * @param model The {@link SingleSelectionModel} which will supply a
     * {@link BigDecimal} ID of the currently selected object
     */
    public ACSObjectSelectionModel(final String javaClass,
                                   final String objectType,
                                   final SingleSelectionModel model) {
        loaded = new RequestLocal() {
            @Override
            protected Object initialValue(final PageState state) {
                return Boolean.FALSE;
            }
        };

        if (javaClass != null) {
            // Cache the Class object and the constructor for further use
            try {
                this.javaClassName = Class.forName(javaClass);
//                this.constructor = javaClassName.getConstructor();
            } catch (ClassNotFoundException | SecurityException ex) {
                throw new UncheckedWrapperException(String.format(
                        "Problem loading class %s", javaClass),
                                                    ex);
            }
        }

        this.objectType = objectType;
        this.selectionModel = model;
    }

    /**
     * Set the ID of the current object. The next time
     * {@link #getSelectedObject(PageState)} is called, the object with the
     * specified ID will be loaded from the database.
     *
     * @param state The page state
     * @param key A {@link BigDecimal} primary key for the object, or a String
     * representation of a BigDecimal, such as "42".
     */
    @Override
    public void setSelectedKey(final PageState state, final Object key) {
        //BigDecimal newKey = convertToBigDecimal(key);

        loaded.set(state, Boolean.FALSE);
        selectionModel.setSelectedKey(state, key);
    }

    /**
     * Return the object which was selected and loaded from the database, using
     * the values supplied in the page state. May return <code>null</code> if
     * <code>isSelected(state) == false</code>, or if the object was not found.
     *
     * @param state The page state
     * @return The currently selected domain object, or null if no object is
     * selected.
     */
    public CcmObject getSelectedObject(final PageState state) {

        Long id = convertToLong(getSelectedKey(state));
        if (id == null) {
            return null;
        }

        return loadACSObject(state, id);
    }

    /**
     * Load the selected object for the first time. Child classes may choose to
     * override this method in order to load the object in nonstandard ways. The
     * default implementation merely instantiates an {@link ACSObject} whose ID
     * is the passed-in key.
     *
     * @param state the current page state
     * @param key the currently selected key; guaranteed to be non-null
     * @return the object identified by the specified key
     */
    protected CcmObject loadACSObject(final PageState state, final Object key) {
        // Cheesy back-and-forth conversion to ensure that
        // the result will be a BigDecimal, not a String or
        // something else. Should go away when ListModel.getKey is
        // changed to return an Object.
        final Long objectId = convertToLong(key);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CcmObjectRepository repository = cdiUtil.findBean(
                CcmObjectRepository.class);

        return repository.findById(objectId).get();

    }

    /**
     * Select the specified object.
     *
     * @param state The page state
     * @param object The content item to set
     */
    public void setSelectedObject(final PageState state,
                                  final CcmObject object) {
        CcmObject item = object;

        if (item == null) {
            loaded.set(state, Boolean.FALSE);
            selectionModel.setSelectedKey(state, null);
        } else {
            loaded.set(state, Boolean.TRUE);
            selectionModel.setSelectedKey(state, item.getObjectId());
        }
    }

    /**
     * Determine if the attempt to load the selected object has been made yet.
     * Child classes may use this method to perform request-local
     * initialisation.
     *
     * @param state the page state
     * @return true if the attempt to load the selected object has already been
     * made, false otherwise
     */
    public boolean isInitialized(final PageState state) {
        return ((Boolean) loaded.get(state));
    }

    /**
     * A utility function which creates a new object with the given ID. Uses
     * reflection to create the instance of the class supplied in the
     * constructor to this <code>ACSObjectSelectionModel</code>. If no class
     * name was supplied in the constructor, an assertion failure will occur.
     *
     * @param id The id of the new item - this is ignored and the object will
     * have a different id
     * @return The newly created item
     * @throws javax.servlet.ServletException
     * @post return != null
     * @deprecated This ignores the ID since ACSObject.setID is a no-op
     */
    public CcmObject createACSObject(final Long id) throws ServletException {
        return createACSObject();
    }

    /**
     * A utility function which creates a new object with the given ID. Uses
     * reflection to create the instance of the class supplied in the
     * constructor to this <code>ACSObjectSelectionModel</code>. If no class
     * name was supplied in the constructor, an assertion failure will occur.
     *
     * @return The newly created item
     * @throws javax.servlet.ServletException
     * @post return != null
     */
    public CcmObject createACSObject() throws ServletException {
        Assert.exists(javaClassName, Class.class);

        try {
            final CcmObject object = (CcmObject) javaClassName.newInstance();
            return object;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * @return the Class of the content items which are produced by this model
     */
    public Class getJavaClass() {
        return javaClassName;
    }

    /**
     * @return The name of the object type of the content items which are
     * produced by this model
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @return the underlying {@link SingleSelectionModel} which maintains the
     * ID of the selected object
     */
    public SingleSelectionModel getSingleSelectionModel() {
        return selectionModel;
    }

    ////////////////////////
    //
    // Passthrough methods
    /**
     * Return <code>true</code> if there is a selected object.
     *
     * @param state represents the state of the current request
     * @return <code>true</code> if there is a selected component.
     */
    @Override
    public boolean isSelected(final PageState state) {
        return selectionModel.isSelected(state);
    }

    /**
     * Return the key that identifies the selected object.
     *
     * @param state the current page state
     * @return the <del>{@link BigDecimal}</del> {@link Long} ID of the
     * currently selected object, or null if no object is selected.
     *
     */
    @Override
    public Object getSelectedKey(final PageState state) {
        Object key = selectionModel.getSelectedKey(state);
        return key;
    }

    /**
     * Clear the selection.
     *
     * @param state the current page state.
     * @post ! isSelected(state)
     * @post ! isInitialized(state)
     */
    @Override
    public void clearSelection(final PageState state) {
        selectionModel.clearSelection(state);
        loaded.set(state, Boolean.FALSE);
    }

    /**
     * Add a change listener to the model. The listener's
     * <code>stateChanged</code> is called whenever the selected key changes.
     *
     * @param listener a listener to notify when the selected key changes
     */
    @Override
    public void addChangeListener(final ChangeListener listener) {
        selectionModel.addChangeListener(listener);
    }

    /**
     * Remove a change listener from the model.
     *
     * @param listener the listener to remove.
     */
    @Override
    public void removeChangeListener(final ChangeListener listener) {
        selectionModel.removeChangeListener(listener);
    }

    /**
     * Return the state parameter which will be used to keep track of the
     * currently selected key. Most likely, the parameter will be a
     * {@link BigDecimalParameter}.
     *
     * @return The state parameter which should be used to keep track of the ID
     * of the currently selected object, or null if the ID is computed in some
     * other way
     * @see SingleSelectionModel#getStateParameter()
     */
    @Override
    public ParameterModel getStateParameter() {
        return selectionModel.getStateParameter();
    }

    private static Long convertToLong(final Object value) {
        Long newValue = null;

        if (value instanceof Long) {
            newValue = (Long) value;
        } else if (value != null) {
            newValue = Long.parseLong(value.toString());
        }

        return newValue;
    }
}
