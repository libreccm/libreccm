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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.util.Assert;
import com.arsdigita.globalization.Globalization;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 *  Reads an array of values
 * and converts them to a Java object array. The values in the array can be
 * any parameter type, for example strings, big decimals etc. The values
 * are read from a request either as multiple repeated values with the same
 * parameter name, i.e. from a URL like
 * <tt>http://foo.com/page?array=one&array=two</tt> or from an encoded
 * string that was previously produced by {@link #unmarshal unmarshal}.
 * <p>
 * Either way, the transformed value of the parameters is an array of the
 * type of <code>getElementParameter().getValueClass()[]</code>, so if the
 * element parameter is a <code>StringParameter</code>, the transformed
 * value will be of type <code>String[]</code>, and if it is a
 * <code>BigDecimalParameter</code>, it will be of type
 * <code>BigDecimal[]</code>.
 *
 * <p> <b>Warning:</b> The array parameter does currently no checking
 * related to the size of the array, no matter what you set min and max
 * value counts to.
 *
 * @author David Lutterkort
 * @version $Id$
 */
public class ArrayParameter extends ParameterModel {

    // Used to separate entries in the marshalled form.
    private static final char SEP_CHAR = '^';
    // Used to escape SEP_CHAR and ESCAPE_CHAR in the marshalled form.
    private static final char ESCAPE_CHAR = '.';


    protected int maxCount=Integer.MAX_VALUE;
    protected int minCount=0;

    // The model for the entries in the array
    private ParameterModel m_element;

    /**
     * Creates a new array parameter. Entries in the array are of type
     * <code>String</code>.
     *
     * @param name the name of the array parameter in URIs.
     */
    public ArrayParameter(String name) {
        this(new StringParameter(name));
    }

    /**
     * Create a new array parameter. Entries in the array are of the type
     * produced by <code>element</code>. Validation listeners on the
     * <code>element</code> are run for each array entry.
     *
     * @param element the parameter model for entries in the array.
     */
    public ArrayParameter(ParameterModel element) {
        super(element.getName());
        m_element = element;
    }

    /**
     * Return the parameter model that transforms and validates individual
     * entries in the array.
     *
     * @return the parameter model for individual entries in the array.
     */
    public final ParameterModel getElementParameter() {
        return m_element;
    }

    /**
     * Set the parameter model that transforms and validates individual
     * entries in the array.
     *
     * @param v the parameter model for entries in the array.
     */
    public final void setElementParameter(ParameterModel  v) {
        Assert.isUnlocked(this);
        m_element = v;
        setName(v.getName());
    }

    /**
     * Encode the given value into a string. The returned string can be
     * turned back into an object that equals <code>value</code> by {@link
     * #unmarshalElement}. Uses the marshalling of the element parameter
     * model.
     *
     * @param value an object with the type of array elements
     * @return the value encoded in a string
     * @see ParameterModel#marshal
     * @see #unmarshalElement
     */
    public final String marshalElement(Object value) {
        return getElementParameter().marshal(value);
    }

    /**
     * Decode one array element from its string representation. The string
     * <code>encoded</code> must have been produced through the marshalling
     * of the element parameter.
     *
     * @param encoded the marshalled representation of an array element
     * @return the array element represented by <code>encoded</code>
     * @see ParameterModel#unmarshal
     * @see #marshalElement
     */
    public final Object unmarshalElement(String encoded) {
        return getElementParameter().unmarshal(encoded);
    }

    /*
     *   private void checkValueCount(Object[] parameterValues,
     *                  ParameterModel parameterModel) {
     *
     *     if (parameterValues == null) return;
     *
     *     int parameterValueCount =  (parameterValues == null) ? 0 :
     *       parameterValues.length;
     *
     *     if (! parameterModel.checkValueCount(parameterValueCount)) {
     *
     *       String msgFormat = resources.getString("INVALID_NUMBER_OF_VALUES");
     *       Object[] msgArgs = { new Integer(parameterModel.getMinValueCount()),
     *              new Integer(parameterModel.getMaxValueCount()) };
     *
     *       String msg = MessageFormat.format(msgFormat, msgArgs);
     *
     *       addError(parameterModel.getName(), msg);
     *     }
     *   }
     *
     */

    /**
     * Extract an array of values from the request. Autodetects whether the
     * request parameter contains the array in several parameters with
     * identical name or as just one value in an encoded form produced by
     * {@link #marshal}.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @return an <code>Object</code> value
     */
    public Object transformValue(HttpServletRequest request) {
        String[] values = Globalization.decodeParameters(request, getName());

        if (values == null) {
            return null;
        }
        if ( values.length != 1 ) {
            Object[] result = makeElementArray(values.length);
            for ( int i=0; i < result.length; i++ ) {
                result[i] = unmarshalElement(values[i]);
            }
            return result;
        }

        return unmarshal(values[0]);
    }

    /**
     * Encode the value which must be an array of objects into one
     * string. The entries in the array must be of a type that can be
     * understood by the {@link #getElementParameter element
     * parameter}. The {@link #unmarshal} method can read this format and
     * reconstruct the array from that string again.
     *
     * @param value an array of values
     * @return the array encoded into one string
     * @pre value instanceof Object[]
     */
    public String marshal(Object value) {
        if ( value == null ) return null;

        Object[] values = (Object[]) value;

        if ( values.length == 0 ) {
            return null;
        } else if ( values.length == 1 ) {
            return marshalElement(values[0]);
        }
        StringBuffer result = new StringBuffer(400);
        for (int i=0; i < values.length; i++) {
            result.append(SEP_CHAR);
            encode(result, marshalElement(values[i]));
        }
        return result.toString();
    }

    /**
     * Encode one entry in the array by escaping the 'right'
     * characters. The <code>value</code> should be the result of
     * marshalling one array entry through the element parameter.
     *
     * @param buf the buffer to whic hthe escaped string is appended
     * @param value marshalled representation of one array element
     */
    private void encode(StringBuffer buf, String value) {
        for (int i=0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ( c == SEP_CHAR || c == ESCAPE_CHAR ) {
                buf.append(ESCAPE_CHAR);
            }
            buf.append(c);
        }
    }

    /**
     * Decode the string representation of an array into an array of
     * objects. The returned value is of type <code>Object[]</code>. The
     * type of the entries in the array depends on the {@link
     * #getElementParameter element parameter}.
     *
     * @param value the marshalled version of an array.
     * @return the <code>Object[]</code> reconstructed from
     * <code>value</code>.
     */
    public Object unmarshal(String value) {
        if ( value.length() == 0 ) {
            return null;
        }
        if ( ! value.startsWith("" + SEP_CHAR) ) {
            Object[] result = makeElementArray(1);
            result[0] = unmarshalElement(value);
            return result;
        }
        // This is the hard part: we have an array encoded in one string
        // before us.
        boolean escape = false;
        StringBuffer buf = new StringBuffer();
        ArrayList l = new ArrayList();
        for (int end = 1; end < value.length(); end++) {
            char c = value.charAt(end);
            if ( escape ) {
                buf.append(c);
                escape = false;
            } else {
                if ( c == SEP_CHAR ) {
                    l.add(unmarshalElement(buf.toString()));
                    buf = new StringBuffer();
                } else if ( c == ESCAPE_CHAR ) {
                    escape = true;
                } else {
                    buf.append(c);
                }
            }
        }
        if ( buf.length() > 0 ) {
            if ( escape ) {
                throw new IllegalArgumentException
                    ("Garbled string encoding of array: '" + value + "'");
            } else {
                l.add(unmarshalElement(buf.toString()));
            }
        }
        // There was only one value. Since we don't encode arrays of length
        // one, this means that the one entry really did start with the
        // SEP_CHAR
        if ( l.size() == 1 ) {
            Object[] result = makeElementArray(1);
            result[0] = unmarshalElement((String) l.get(0));
            return result;
        }
        return l.toArray(makeElementArray(l.size()));
    }

    /**
     *      Sets the minimum number of values for this parameter that may be
     *      submitted for the request to be valid.  If the parameter is required,
     *      the minimum must be at least one.
     */
    public final void setMinValueCount(int count) {
        minCount=count;
    }

    /**
     *      Sets the maximum number of values for this parameter that may be
     *      submitted for the request to be valid.
     */
    public final void setMaxValueCount(int count) {
        minCount=count;
    }

    /**
     *      Gets the minimum number of values for this parameter that may be
     *      submitted for the request to be valid.
     */
    public final int getMinValueCount() {
        return minCount;
    }

    /**
     *    Gets the maximum number of values for this parameter that may be
     *    submitted for the request to be valid.
     */
    public final int getMaxValueCount() {
        return maxCount;
    }

    /**
     * Validate the array extracted from the request by running the
     * validation listeners of this parameter, and by running thte
     * validation for the element parameter for each entry in the array
     * separately.
     *
     * @param data the parameter data to validate.
     * @throws FormProcessException if the data can not be validated.
     */
    public void validate(ParameterData data) throws FormProcessException {
        super.validate(data);
        Object[] values = (Object[]) data.getValue();
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                ParameterData p = new ParameterData(
                                                    getElementParameter(), values[i]
                                                    );
                getElementParameter().validate(p);
                data.copyErrors(p);
            }
        }
    }

    public synchronized void lock() {
        getElementParameter().lock();
        super.lock();
    }

    public Class getValueClass() {
        return makeElementArray(0).getClass();
    }

    private Object[] makeElementArray(int length) {
        return (Object[]) Array.newInstance(getElementParameter().getValueClass(),
                                            length);
    }
}
