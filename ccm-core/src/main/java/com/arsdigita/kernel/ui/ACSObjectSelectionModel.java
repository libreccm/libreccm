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
 * Loads a subclass of an ACSObject from the database.
 * By default, uses a BigDecimal state parameter in order to
 * store and retrieve the item id.
 *
 * <p>
 * The <code>getSelectedKey(PageState state)</code> method will return the
 *   BigDecimal id of the currently selected object. This method
 *   will return the id even if the object with this id does not
 *   actually exist.
 *
 * <p>
 * The <code>getSelectedObject(PageState state)</code> method will return the
 *   object whose id is <code>getSelectedKey(PageState state)</code>. If the
 *   object does not actually exist, the method will return null
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
 * Naturally, the <code>ACSObjectSelectionModel</code> could also be passed
 * in as a parameter in the <code>MyComponent</code> constructor. In this
 * case, it should be up to the parent of <code>MyComponent</code> to
 * register the model's state parameter.
 * <p>
 * <b>Advanced Usage</b>: The <code>ACSObjectSelectionModel</code>
 * is actually just a wrapper for a {@link SingleSelectionModel}
 * which maintains the currently selected object's ID as a
 * {@link BigDecimal}. By default, a new
 * {@link ParameterSingleSelectionModel} is wrapped in this way;
 * however, any {@link SingleSelectionModel} may be wrapped.
 * Thus, it becomes possible to use the <code>ACSObjectSelectionModel</code>
 * even if the currently selected ID is not stored in a state parameter.
 * <p>
 * <b>Persistence Details:</b> The default constructor of
 * <code>ACSObjectSelectionModel</code> will attempt to use the
 * {@link DomainObjectFactory} to automatically instantiate the correct Java 
 * subclass of {@link ACSObject}. However, it is also possible to use an
 * alternate constructor in order to force the <code>ACSObjectSelectionModel</code>
 * to manually instantiate the objects:
 *
 * <blockquote><pre><code>
 * ACSObjectSelectionModel model = 
 *     new ACSObjectSelectionModel("com.arsdigita.cms.Article", 
 *                                 "com.arsdigita.cms.Article", "item_id");
 * </code></pre></blockquote>
 *
 * In this case, the model will attempt to use reflection to instantiate the
 * correct subclass of <code>ACSObject</code>. In addition, the supplementary
 * constructor makes it possible to create new objects in the database
 * using the {@link #createACSObject(BigDecimal)} method.
 *
 * @see com.arsdigita.bebop.SingleSelectionModel
 * @see com.arsdigita.bebop.ParameterSingleSelectionModel
 *
 * @author Stanislav Freidin
 * @version $Id$
 */
public class ACSObjectSelectionModel implements SingleSelectionModel {

    private static final Logger s_log =
        Logger.getLogger(ACSObjectSelectionModel.class);

    private RequestLocal m_loaded;
    private Class m_javaClass;
    private Constructor m_constructor;
    private String m_objectType;
    private SingleSelectionModel m_model;

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>.
     * This model will produce instances of <code>ACSObject</code>
     * by automatically instantiating the correct Java subclass using
     * the {@link DomainObjectFactory}.
     *
     * @param parameter The state parameter which should be used to store
     *   the object ID
     */
    public ACSObjectSelectionModel(BigDecimalParameter parameter) {
        this(null, null, parameter);
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>.
     * This model will produce instances of <code>ACSObject</code>
     * by automatically instantiating the correct Java subclass using
     * the {@link DomainObjectFactory}.
     *
     * @param parameterName The name of the state parameter which will
     *    be used to store the object ID.
     */
    public ACSObjectSelectionModel(String parameterName) {
        this(null, null, new BigDecimalParameter(parameterName));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>.
     * This model will produce instances of <code>ACSObject</code>
     * by automatically instantiating the correct Java subclass using
     * the {@link DomainObjectFactory}.
     *
     * @param model The {@link SingleSelectionModel} which will supply
     *    a {@link BigDecimal} ID of the currently selected object
     */
    public ACSObjectSelectionModel(SingleSelectionModel model) {
        this(null, null, model);
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents
     *    the object. Must be a subclass of {@link ACSObject}. In
     *    addition, the class must have a constructor with a single
     *    {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the ACS object. In practice, will often be
     *    the same as the javaClass.
     * @param parameterName The name of the state parameter which will
     *    be used to store the object ID.
     */
    public ACSObjectSelectionModel( String javaClass, 
                                    String objectType, 
                                    String parameterName ) {
        this(javaClass, objectType, new BigDecimalParameter(parameterName));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents
     *    the object. Must be a subclass of {@link ACSObject}. In
     *    addition, the class must have a constructor with a single
     *    {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the ACS object. In practice, will often be
     *    the same as the javaClass.
     * @param parameter The state parameter which should be used to store
     *    the object ID
     */
    public ACSObjectSelectionModel( String javaClass, 
                                    String objectType, 
                                    BigDecimalParameter parameter ) {
        this(javaClass, objectType,
             new ParameterSingleSelectionModel(parameter));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents
     *    the object. Must be a subclass of {@link ACSObject}. In
     *    addition, the class must have a constructor with a single
     *    {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the ACS object. In practice, will often be
     *    the same as the javaClass.
     * @param model The {@link SingleSelectionModel} which will supply
     *    a {@link BigDecimal} ID of the currently selected object
     */
    public ACSObjectSelectionModel( String javaClass, 
                                    String objectType, 
                                    SingleSelectionModel model ) {
        m_loaded = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    return Boolean.FALSE;
                }
            };

        if (javaClass != null) {
            // Cache the Class object and the constructor for further use
            try {
                m_javaClass = Class.forName(javaClass);
                m_constructor = m_javaClass.getConstructor();
            } catch (Exception e) {
                throw new UncheckedWrapperException( "Problem loading class " 
                                                     + javaClass, e );
            }
        }

        m_objectType = objectType;
        m_model = model;
    }

    /**
     * Set the ID of the current object. The next time
     * {@link #getSelectedObject(PageState)} is called, the object
     * with the specified ID will be loaded from the database.
     *
     * @param state The page state
     * @param key A {@link BigDecimal} primary key for the object,
     *   or a String representation of a BigDecimal, such as "42".
     */
    public void setSelectedKey(PageState state, Object key) {
        //BigDecimal newKey = convertToBigDecimal(key);

        m_loaded.set(state, Boolean.FALSE);
        m_model.setSelectedKey(state, key);
    }

    /**
     * Return the object which was selected and loaded from the database,
     * using the values supplied in the page state. May return <code>null</code>
     * if <code>isSelected(state) == false</code>, or if the object was not found.
     *
     * @param state The page state
     * @return The currently selected domain object, or null if no object is
     *         selected.
     */
    public CcmObject getSelectedObject(PageState state) {

        Long id = convertToLong(getSelectedKey(state));
        if (id == null) {
            return null;
        }
        
        return loadACSObject(state, id);
    }

    /**
     * Load the selected object for the first time. Child classes
     * may choose to override this method in order to load the object
     * in nonstandard ways. The default implementation merely
     * instantiates an {@link ACSObject} whose ID is the passed-in key.
     *
     * @param state the current page state
     * @param key the currently selected key; guaranteed to be non-null
     * @return the object identified by the specified key
     * @pre key != null
     */
    protected CcmObject loadACSObject(PageState state, Object key) {
        CcmObject item = null;

        // Cheesy back-and-forth conversion to ensure that
        // the result will be a BigDecimal, not a String or
        // something else. Should go away when ListModel.getKey is
        // changed to return an Object.
        Long id = convertToLong(key);

        return CdiUtil.createCdiUtil().findBean(CcmObjectRepository.class).findById(id);
        
    }

    /**
     * Select the specified object.
     *
     * @param state The page state
     * @param object The content item to set
     */
    public void setSelectedObject(PageState state, CcmObject object) {
        CcmObject item = object;

        if (item == null) {
            m_loaded.set(state, Boolean.FALSE);
            m_model.setSelectedKey(state, null);
        } else {
            m_loaded.set(state, Boolean.TRUE);
            m_model.setSelectedKey(state, item.getObjectId());
        }
    }

    /**
     * Determine if the attempt to load the selected object has
     * been made yet. Child classes may use this method to
     * perform request-local initialization.
     *
     * @param state the page state
     * @return true if the attempt to load the selected object has
     *   already been made, false otherwise
     */
    public boolean isInitialized(PageState state) {
        return ((Boolean)m_loaded.get(state)).booleanValue();
    }

    /**
     * A utility function which creates a new object with the given ID.
     * Uses reflection to create the instance of the class supplied
     * in the constructor to this <code>ACSObjectSelectionModel</code>.
     * If no classname was supplied in the constructor, an assertion
     * failure will occur.
     *
     * @param id The id of the new item - this is ignored and the object
     *           will have a different id
     * @return The newly created item
     * @post return != null
     * @deprecated This ignores the ID since ACSObject.setID is a no-op
     */
    public CcmObject createACSObject(Long id) throws ServletException {
        return createACSObject();
    }


    /**
     * A utility function which creates a new object with the given ID.
     * Uses reflection to create the instance of the class supplied
     * in the constructor to this <code>ACSObjectSelectionModel</code>.
     * If no classname was supplied in the constructor, an assertion
     * failure will occur.
     *
     * @param id The id of the new item
     * @return The newly created item
     * @post return != null
     */
    public CcmObject createACSObject() throws ServletException {
        Assert.exists(m_javaClass, Class.class);

        try {
            CcmObject item = (CcmObject)m_javaClass.newInstance();
            return item;
        } catch (InstantiationException e) {
            throw new ServletException(e);
        } catch (IllegalAccessException e) {
            throw new ServletException(e);
        }
    }


    /**
     * @return the Class of the content items which are produced
     *         by this model
     */
    public Class getJavaClass() {
        return m_javaClass;
    }

    /**
     * @return The name of the object type of the
     *         content items which are produced by this model
     */
    public String getObjectType() {
        return m_objectType;
    }

    /**
     * @return the underlying {@link SingleSelectionModel} which
     *   maintains the ID of the selected object
     */
    public SingleSelectionModel getSingleSelectionModel() {
        return m_model;
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
    public boolean isSelected(PageState state) {
        return m_model.isSelected(state);
    }

    /**
     * Return the key that identifies the selected object.
     *
     * @param state the current page state
     * @return the {@link BigDecimal} ID of the currently selected
     *   object, or null if no object is selected.
     * @post return instanceof BigDecimal
     *
     */
    public Object getSelectedKey(PageState state) {
        Object key = m_model.getSelectedKey(state);
        return key;
    }


    /**
     * Clear the selection.
     *
     * @param state the current page state.
     * @post ! isSelected(state)
     * @post ! isInitialized(state)
     */
    public void clearSelection(PageState state) {
        m_model.clearSelection(state);
        m_loaded.set(state, Boolean.FALSE);
    }

    /**
     * Add a change listener to the model. The listener's
     * <code>stateChanged</code> is called whenever the selected key changes.
     *
     * @param l a listener to notify when the selected key changes
     */
    public void addChangeListener(ChangeListener l) {
        m_model.addChangeListener(l);
    }

    /**
     * Remove a change listener from the model.
     *
     * @param l the listener to remove.
     */
    public void removeChangeListener(ChangeListener l) {
        m_model.removeChangeListener(l);
    }

    /**
     * Return the state parameter which will be used to keep track
     * of the currently selected key. Most likely, the parameter will
     * be a {@link BigDecimalParameter}.
     *
     * @return The state parameter which should be used to keep
     *         track of the ID of the currently selected object, or null
     *         if the ID is computed in some other way
     * @see SingleSelectionModel#getStateParameter()
     */
    public ParameterModel getStateParameter() {
        return m_model.getStateParameter();
    }

    private static Long convertToLong(Object value) {
        Long newValue = null;

        if ( value instanceof Long ) {
            newValue = (Long) value;
        } else if ( value != null ) {
            newValue = Long.parseLong(value.toString());
        }

        return newValue;
    }
}
