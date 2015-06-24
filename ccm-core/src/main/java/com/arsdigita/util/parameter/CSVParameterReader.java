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

import com.arsdigita.util.UncheckedWrapperException;
// import com.arsdigita.util.parameter.ErrorList;
// import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.ParameterLoader;
// import com.arsdigita.util.parameter.ParameterValue;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Processes an input stream (a set of lines, each containing a comma separated
 * list of parameter values) and ....
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public final class CSVParameterReader implements ParameterReader {

    private final LineNumberReader m_reader;
    private final Parameter[] m_params;
    private final HashMap m_line;

    /**
     * Constructor
     * 
     * @param reader: input stream to read values
     * @param params: array of parameter objects to store procecced values
     */
    public CSVParameterReader(final Reader reader, final Parameter[] params) {
        m_reader = new LineNumberReader(reader);  // input stream
        m_params = params;                        // array of parameters
        m_line = new HashMap(params.length);      //
    }

    /**
     * read
     * 
     * 
     * 
     * @param param
     * @param errors
     * @return
     */
    public final String read(final Parameter param, final ErrorList errors) {
        return (String) m_line.get(param);
    }
/*
 * May 2009: Obviously a reminiscence from previous versions of code. This class
 * is currently used by coreloader only and it does not use the load method
 * and it works with load commented out.
 *
 * Code should be removed after extensive testing.
 *
    public final ParameterValue load(final Parameter param) {
        final ParameterValue value = new ParameterValue();

        // XXX this won't work correctly with compound parameters
        value.setObject(param.read(this, value.getErrors()));

        value.getErrors().check();

        return value;
    }
*/
    /**
     * Just a public visible entry point into internalNext, used to process
     * an exception if thrown.
     * 
     * @return: boolean true if any values could be processed.
     */
    public final boolean next() {
        try {
            return internalNext();
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    /**
     * Internally used worker method which processes the next() method.
     * 
     * Reads in a line from input stream and asks parseLine to process it. The
     * resulting array of strings  (each containing a value) 
     * @return
     * @throws java.io.IOException
     */
    private boolean internalNext() throws IOException {
        final String line = m_reader.readLine();

        if (line == null) {
            return false;
        } else {
            final String[] elems = parseLine(line);

            // m_params: array of parameters to store the comma separated values
            // used to determine the max. number of values which can be processed.
            for (int i = 0; i < m_params.length; i++) {
                if (i < elems.length) {
                    // If for the given index into the array of parametes a
                    // corresponding element in the array of strings exist,
                    // store it in a hash map (a hash map per line)
                    m_line.put(m_params[i], elems[i]);
                } else {
                    m_line.put(m_params[i], null);
                }
            }

            return true;
        }
    }

    private static final char ESCAPE = '\\';
    private static final char QUOTE = '"';
    private static final char SEPARATOR = ',';

    /**
     * Internal used helper method of method parseLine.
     *  
     * @param c
     * @return
     */
    private char escape(char c) {
        switch (c) {
        case 'n':
            return '\n';
        case 't':
            return '\t';
        case 'r':
            return '\r';
        default:
            return c;
        }
    }

    /**
     * Takes a string and analyses it as a list of comma separated values.
     * 
     * Internally used to store each value found in a new string and add it
     * to an array of strings.
     * 
     * @param line: string containing a comma separated list of values
     * @return : array of strings, each containing a value of the list
     */
    private String[] parseLine(final String line) {
        int length = line.length();

        // Check here if the last character is an escape character so
        // that we don't need to check in the main loop.
        if (line.charAt(length - 1) == ESCAPE) {
            throw new IllegalArgumentException
                (m_reader.getLineNumber() +
                 ": last character is an escape character\n" + line);
        }

        // The set of parsed fields.
        List result = new ArrayList();

        // The characters between seperators.
        StringBuffer buf = new StringBuffer(length);
        // Marks the begining of the field relative to buf,
        // -1 indicates the beginning of buf.
        int begin = -1;
        // Marks the end of the field relative to buf.
        int end = 0;

        // Indicates whether or not we're in a quoted string.
        boolean quote = false;

        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);
            if (quote) {
                switch (c) {
                case QUOTE:
                    quote = false;
                    break;
                case ESCAPE:
                    buf.append(escape(line.charAt(++i)));
                    break;
                default:
                    buf.append(c);
                    break;
                }

                end = buf.length();
            } else {
                switch (c) {
                case SEPARATOR:
                    result.add(field(buf, begin, end));
                    buf = new StringBuffer(length);
                    begin = -1;
                    end = 0;
                    break;
                case ESCAPE:
                    if (begin < 0) { begin = buf.length(); }
                    buf.append(escape(line.charAt(++i)));
                    end = buf.length();
                    break;
                case QUOTE:
                    if (begin < 0) { begin = buf.length(); }
                    quote = true;
                    end = buf.length();
                    break;
                default:
                    if (begin < 0 &&
                        !Character.isWhitespace(c)) {
                        begin = buf.length();
                    }
                    buf.append(c);
                    if (!Character.isWhitespace(c)) { end = buf.length(); }
                    break;
                }
            }
        }

        if (quote) {
            throw new IllegalArgumentException
                (m_reader.getLineNumber() + ": unterminated string\n" + line);
        } else {
            result.add(field(buf, begin, end));
        }

        String[] fields = new String[result.size()];
        result.toArray(fields);
        return fields;
    }

    /**
     * internal helper method for method parseLine
     * 
     * @param field
     * @param begin
     * @param end
     * @return
     */
    private String field(StringBuffer field, int begin, int end) {
        if (begin < 0) {
            return field.substring(0, end);
        } else {
            return field.substring(begin, end);
        }
    }

}
