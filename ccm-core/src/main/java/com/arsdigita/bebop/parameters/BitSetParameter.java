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


import com.arsdigita.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Encode and decode a bit set as a request parameter. The bit set is
 * stored in a {@link java.util.BitSet} and encoded and decoded from
 * the HTTP request appropriately.
 *
 * Currently two forms of encoding are supported: RAW encoding and
 * DGap. RAW encoding stuffs the bits straight into characters. <a
 * href="http://bmagic.sourceforge.net/dGap.html">DGap</a> is a
 * variation on run length encoding particularly suited to
 * bitsets. The most appropriate encoding to use depends on the
 * distribution of bits. Since it iss not always possible to
 * anticipate this distribution, an automatic encoding mode is
 * provided which chooses the encoding with the shortest resulting
 * string size.
 *
 * @author David Lutterkort
 * @version $Id$
 */
public class BitSetParameter extends ParameterModel {

    /**
     * The radix used to encode/decode the bitset into a BigInteger
     */
    private static final int RADIX = Character.MAX_RADIX; //currently 26

    /** Compression coefficient for the raw encoding.  Base-26 encoding is this
     * many times more compact than base-2 encoding.
     */
    private static final double QUOTIENT = Math.log(2) / Math.log(RADIX);

    private static final char SEPARATOR = '.';

    /**
     * Flag for RAW encoding of bit set
     */
    public static final int ENCODE_RAW = 0;
    /**
     * Flag for  DGap encoding of bit set
     */
    public static final int ENCODE_DGAP = 1;
    /**
     * Flag to automatically choose the shortest encoding scheme
     */
    public static final int ENCODE_AUTO = 2;

    private int m_encode = ENCODE_RAW;

    private static final String FLAG_RAW = "r";
    private static final String FLAG_DGAP = "d";

    /**
     * Create a bit set parameter with the given name.
     *
     * @param name the name of this parameter for use in URLs etc.
     * @param encode the encoding scheme
     */
    public BitSetParameter(String name, int encode) {
        super(name);
        m_encode = encode;
    }

    /**
     * Create a bit set parameter with the given name, defaulting
     * to the RAW encoding scheme
     *
     * @param name the name of this parameter for use in URLs etc.
     */
    public BitSetParameter(String name) {
        this(name, ENCODE_RAW);
    }


    /**
     * Extract a bit set from the HTTP request. Looks for a parameter with
     * the name of this parameter and unmarshals it into a {@link
     * java.util.BitSet}.
     *
     * @param request the HTTP request
     * @return the {@link java.util.BitSet} extracted from the request or
     * <code>null</code>.
     * @throws IllegalArgumentException if the parameter can not be
     * transformed into a bit set.
     */
    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {
        return transformSingleValue(request);
    }

    /**
     * Read a string in the format produced by {@link #marshal marshal} and
     * produce the corresponding {@link java.util.BitSet}.
     *
     * @param value A string representing the bit set.
     * @return the {@link java.util.BitSet} corresponding to the value.
     * @throws IllegalArgumentException if the value can not be
     * transformed into a bit set.
     * @see #marshal
     */
    public Object unmarshal(String value)
        throws IllegalArgumentException {

        if (value.startsWith(FLAG_DGAP)) {
            return unmarshalDGap(value.substring(1));
        } else if (value.startsWith(FLAG_RAW)) {
            return unmarshalRaw(value.substring(1));
        } else {
            throw new IllegalArgumentException
                (getName() + " should start with either '" +
                 FLAG_RAW + "' or '" + FLAG_DGAP + "' : "+ value);
        }
    }

    private Object unmarshalRaw(String value)
        throws IllegalArgumentException {

        BitSet result = new BitSet(32);
        BigInteger n = null;

        try {
            n = new BigInteger(value, RADIX);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException
                (getName() + " should be a BigInteger: '" + value + "'; " +
                 e.getMessage());
        }

        for (int i=0; i < n.bitLength(); i++) {
            if ( n.testBit(i) ) {
                result.set(i);
            }
        }
        return result;
    }

    private Object unmarshalDGap(String value)
        throws IllegalArgumentException {

        BitSet result = new BitSet(32);
        String[] bits = StringUtils.split(value, SEPARATOR);

        boolean state = ("0".equals(bits[0]) ? false : true);
        int current = 0;
	// byline patch
//         if (state) {
//             result.set(1);
//         }
        for (int i = 1 ; i < bits.length ; i++) {
            BigInteger n = null;
            try {
                n = new BigInteger(bits[i], RADIX);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException
                    (getName() + " should be a comma separated list of BigInteger: '" +
                     value + "'; " + e.getMessage());
            }
            int length = n.intValue();

            for (int j = 0 ; j < length ; j++) {
                if (state) {
                    result.set(current);
                }
                current++;
            }

            state = !state;
        }

        return result;
    }

    /**
     * Turn the value, which must be a {@link java.util.BitSet}, into a
     * string that can be read by {@link #unmarshal unmarshal}.
     *
     * @param value a {@link java.util.BitSet} produced by this parameter
     * model.
     * @return a string encoding of the bit set.
     * @see #unmarshal
     */
    public String marshal(Object value) {
        if ( value == null ) {
            return null;
        }

        BitSet set = (BitSet) value;

        if (m_encode == ENCODE_DGAP) {
            return FLAG_DGAP + marshalDGap(set);
        } else if (m_encode == ENCODE_AUTO) {
            String dgap = marshalDGap(set);
            if ( rawLength(set) > dgap.length() ) {
                return FLAG_DGAP + dgap;
            } else {
                return FLAG_RAW + marshalRaw(set);
            }
        } else {
            return FLAG_RAW + marshalRaw(set);
        }
    }

    /**
     * Computes the length of the base-26 representation of the bitset.
     **/
    private static int rawLength(BitSet set) {
        if ( set.length()==0 ) {
            return 1;
        }
        return (int) Math.ceil(set.length() * QUOTIENT);
    }

    private static String marshalRaw(BitSet set) {
        BigInteger n = new BigInteger("0");
        for (int i=0; i<set.length(); i++) {
            if (set.get(i)) {
                // TODO: this may be horribly inefficient. It allocates a new
                // BigInteger for every set bit
                // It is better to convert the bit set to a byte[] by hand
                // and then pass that to the BigInteger constructor
                n = n.setBit(i);
            }
        }
        return n.toString(RADIX);
    }

    private static String marshalDGap(BitSet set) {
        StringBuffer sb = new StringBuffer();
        sb.append(set.get(0) ? "1" : "0");

        boolean current = set.get(0);
        int runLength = 1;
        for (int i=1; i < set.length(); i++) {
            if ( set.get(i) == current ) {
                runLength++;
            } else {
                sb.append(SEPARATOR);
                BigInteger bi = new BigInteger(String.valueOf(runLength));
                sb.append(bi.toString(RADIX));
                runLength = 1;
                current = set.get(i);
            }
        }
        sb.append(SEPARATOR);
        sb.append(String.valueOf(runLength));

        return sb.toString();
    }


    public Class getValueClass() {
        return BitSet.class;
    }

}
