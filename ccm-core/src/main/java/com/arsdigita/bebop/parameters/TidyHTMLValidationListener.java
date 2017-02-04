/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.BebopConfig;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.w3c.tidy.Tidy;

/**
 * Verifies that the parameter's value is well-formed HTML by running it through
 * <a href="http://sourceforge.net/projects/jtidy">JTidy</a>.
 *
 * <p><em>Side effect:</em> <code>Tidy</code> may raise errors and warnings.  If
 * errors are found, then the input HTML is broken in a way that Tidy cannot
 * repair. If only warnings are found, Tidy will clean up the HTML.  This
 * validation listener will <em>replace</em> the original data with the tidied
 * up version.(But only if the {@link #setRepairPolicy(boolean
 * repairOriginalData) repair policy} is set accordingly.) The user will be
 * given a chance to examine and accept the changes that Tidy made.  Note that
 * only markup is changed. The actual text will not be lost as a result of
 * replacement.</p>
 *
 * <p>This validation listener will not pass until Tidy is satisfied with the
 * user's HTML. </p>
 *
 * <p><em>Issues:</em> JTidy came to life a as port of <a
 * href="http://www.w3.org/People/Raggett/">Dave Ragget</a>'s <a
 * href="http://tidy.sourceforge.net/">HTML tidy</a> C utility. As a result of
 * its C heritage, JTidy doesn't use Java's built-in support for different
 * character encodings.  Rather than operating on {@link String Strings} or
 * {@link java.io.Writer writers} and {@link java.io.Reader readers} for working
 * with character data, JTidy works directly with {@link java.io.InputStream
 * input}/{@link java.io.OutputStream output} streams. The resulting support for
 * character encodings is limited to "ascii", "latin1", "raw", "utf8",
 * "iso2022", and "mac". (See the <a
 * href="http://tidy.sourceforge.net/docs/quickref.html#char-encoding">quickref
 * document</a> distributed by the <a
 * href="http://tidy.sourceforge.net/">tidy</a> project for an explanation of
 * these encoding schemes. Although not necessarily directly applicable to
 * JTidy, it provides a good overview.)  </p>
 *
 * <p>The bottom line is, JTidy does not behave like a "native" Java application
 * when it comes to supporting character encodings. However, it is probably the
 * most robust and widely used HTML validator written in Java. Therefore, we are
 * providing a validation listener based on JTidy. </p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 **/
public class TidyHTMLValidationListener implements ParameterListener {
    
    private static String LINE_SEPARATOR = System.getProperty("line.separator");

    private static LockableProperties s_tidyProperties;

    /**
     * Returns the {@link Properties} object represeting the JTity properties
     * specified in the config file pointed to by the {@link #JTIDY_CONFIG_FILE}
     * parameter.
     *
     * <p>Once loaded from the file, we don't want the Properties object
     * representing JTidy properties to be programmatically modified.
     * Therefore, the object is "locked" once initialized. If the Tidy object
     * needs to change the default value of any of its configuration options,
     * the appropriate setter method can be called directory on Tidy. </p>
     */
    private static Properties getJTidyProperties() {
        if (s_tidyProperties == null) {
            s_tidyProperties = new LockableProperties();
            String path = BebopConfig.getConfig().getTidyConfigFile();
            
            InputStream is = TidyHTMLValidationListener.class.getClassLoader
                ().getResourceAsStream(path);
            
            if (is == null) {
                throw new IllegalStateException("Couldn't find " + path);
            }
            
            try {
                s_tidyProperties.load(is);
            } catch (IOException ex) {
                throw new UncheckedWrapperException
                    ("Couldnt' load JTidy properties", ex);
            }
            
            // Check if we actually loaded any properties.
            
            if (!s_tidyProperties.propertyNames().hasMoreElements()) {
                throw new
                    IllegalStateException("No properties have been loaded" +
                            " from " + path);
            }
            
            s_tidyProperties.lock();
        }
        return s_tidyProperties;
    }

    private Tidy m_tidy;
    private boolean m_repairOriginalData = true;

    public TidyHTMLValidationListener() {}

    /**
     * Specifies whether the original form data should be replaced with the
     * tidied up version, if it fails to validate against JTidy.
     *
     * @see #repairsOriginalData()
     * @param repairOriginal if set to to true, then this validation listener
     * will replace the original form data with the tidied up version.
     **/
    public void setRepairPolicy(boolean repairOriginalData) {
        m_repairOriginalData = repairOriginalData;
    }

    /**
     * @see #setRepairPolicy(boolean)
     * @return defaults to <code>true</code>, unless modified via {@link #setRepairPolicy(boolean)}.
     **/
    public boolean repairsOriginalData() {
        return m_repairOriginalData;
    }

    /**
     * Lazily instantiates Tidy, so as to avoid creating a massive Tidy object
     * for those widgets that are instantiated but never used on a running
     * server. So, for example, if an authoring kit in CMS relies on this
     * validation listener but is never used on a site, the validation listener
     * will be instantiated, but the Tidy object won't.
     **/
    private Tidy getInstance() {
        if ( m_tidy == null ) {
            m_tidy = new Tidy();
            m_tidy.setConfigurationFromProps(getJTidyProperties());
        }
        return m_tidy;
    }

    public void validate (ParameterEvent e) {
        ParameterData data = e.getParameterData();
        String html = (String) data.getValue();
        if ( html == null ) {
            return;
        }
        html = html.trim();
        if ( "".equals(html) ) {
            return;
        }
        StringWriter errors = new StringWriter();
        PrintWriter errorWriter = new PrintWriter(errors);
        getInstance().setErrout(errorWriter);
        // FIXME: we should use the getBytes(String enc) method here instead.
        // vadimn@redhat.com, 2002-08-16 22:37:46 -0400
        InputStream in = new ByteArrayInputStream(addTitle(html).getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        getInstance().parse(in, out);
        try {
            out.close();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Error flushig output stream: " + ex.getMessage(), ex);
        }

        ErrorMessage errorMsg = new ErrorMessage(errors.toString());

        if ( errorMsg.isEmpty() ) {
            if ( !repairsOriginalData() ) {
                return;
            }
            String tidiedHTML = removeJTidyCruft(out.toString());
            if ( !equalLineByLine(html, tidiedHTML) ) {
                data.setValue(tidiedHTML);
                data.addError("<p>Please verify and resubmit tidied up HTML.</p>");
            }
        } else {
            StringBuffer sb = null;
            if ( errorMsg.hasErrors() ) {
                sb = new StringBuffer(errorMsg.toString());
                sb.append("<p>");
                final String correctErrors =
                    "The above error(s) must be corrected, before this HTML " +
                    "can be tidied up.";
                sb.append(correctErrors);
            } else {
                sb = new StringBuffer();
                sb.append("<p>The following errors have been found");
                if ( repairsOriginalData() ) {
                    sb.append(" and corrected");
                }
                sb.append(":</p>").append(LINE_SEPARATOR);
                sb.append(errorMsg.toString());
                if ( repairsOriginalData() ) {
                    sb.append("<p>Please resubmit the corrected HTML, if you are ");
                    sb.append("satisfied with it.</p>");
                } else {
                    sb.append("<p>Please correct and resubmit.</p>");
                }
                sb.append(LINE_SEPARATOR);

                if ( repairsOriginalData() ) {
                    data.setValue(removeJTidyCruft(out.toString()));
                }
            }
            data.addError(sb.toString());
        }
    }

    /**
     * Avoid gratuitous complaints about the missing &lt;title> element.
     **/
    private static String addTitle(String  html) {
        return "<title></title>\n" + html;
    }

    private static String removeJTidyCruft(String html) {
        int beginIndex = html.indexOf("<body>");
        if  ( beginIndex < 0 ) {
            beginIndex = html.indexOf("<BODY>");
        }
        int endIndex = html.indexOf("</body>");
        if ( endIndex < 0 ) {
            endIndex = html.indexOf("</BODY>");
        }

        // Note: In theory, this may blow up in our face with an
        // StringIndexOutOfBoundsException. -- 2002-08-19 15:49
        return html.substring(beginIndex + ("<body>" + LINE_SEPARATOR).length(),
                              endIndex).trim();
    }

    /**
     * Returns true if s1 and s2 only differ in terms of line termination. That
     * is to say, if s1 and s2 are identical except for the fact that one uses
     * the linefeed character '\n' and the other curliff "\r\n" for line
     * termination, the method will return true.
     **/
    private static boolean equalLineByLine(String s1, String s2) {
        if ( s1 == s2 ) {
            return true;
        }

        if ( s1 == null || s2 == null ) {
            return false;
        }

        final char CR = '\r';
        final char LF = '\n';

        for (int i1 = 0, i2 = 0; i1 < s1.length() || i2 < s2.length(); ) {
            boolean s1Finished = i1 >= s1.length();
            if ( !s1Finished ) {
                char c1 = s1.charAt(i1);
                if ( c1 == CR || c1 == LF ) {
                    i1++;
                    continue;
                }
            }

            boolean s2Finished = i2 >= s2.length();
            if ( !s2Finished ) {
                char c2 = s2.charAt(i2);
                if ( c2 == CR || c2 == LF ) {
                    i2++;
                    continue;
                }
            }

            if ( s1Finished || s2Finished ) {
                return false;
            }

            if ( s1.charAt(i1) != s2.charAt(i2) ) {
                return false;
            }
            i1++;
            i2++;
        }
        return true;
    }

    private static class ErrorMessage {
        private static final String LINE_WARNING_START = "line ";
        private boolean m_hasWarnings;
        private boolean m_hasErrors;
        private String m_formattedMessage;
        private static final List s_ignorableWarnings;
        static {
            s_ignorableWarnings = new ArrayList();
            s_ignorableWarnings.add("<table> lacks \"summary\" attribute");
        }

        private static final List s_knownWarningSummaries;
        static {
            s_knownWarningSummaries = new ArrayList();
            s_knownWarningSummaries.add("The table summary attribute");
            s_knownWarningSummaries.add("Characters codes for");
        }
        public ErrorMessage(String msg) {
            if ( msg != null || msg.length() > 0 ) {
                format(msg);
            }
        }

        public boolean isEmpty() {
            return !hasWarnings() && !hasErrors();
        }

        public boolean hasWarnings() {
            return m_hasWarnings;
        }

        public boolean hasErrors() {
            return m_hasErrors;
        }

        public String toString() {
            return m_formattedMessage;
        }

        private static boolean isWarning(String line) {
            if ( line == null ) return false;

            for (Iterator i=s_knownWarningSummaries.iterator(); i.hasNext(); ) {
                if ( line.startsWith((String) i.next()) ) return true;
            }
            return false;
        }

        private static boolean canBeIgnored(String warning) {
            if ( warning == null ) return true;

            for (Iterator i= s_ignorableWarnings.iterator(); i.hasNext(); ) {
                if ( warning.indexOf((String) i.next()) > -1 ) {
                    return true;
                }
            }
            return false;
        }

        private void format(String errorMsg) {
            StringTokenizer st = new StringTokenizer(errorMsg, LINE_SEPARATOR, false);

            List warningsAndErrors = new ArrayList();

            /* Given the following input,
             * <p>This is an article.</p>
             * bar
             * <b><i>bold italic</b></i>
             * </table>
             * <foo>bar</foo>
             *
             * the error message generated by Tidy has the following structure:
             * line 4 column 18 - Warning: replacing unexpected </b> by </i>
             * line 4 column 22 - Warning: replacing unexpected </i> by </b>
             * line 5 column 1 - Warning: discarding unexpected </table>
             * line 6 column 2 - Error: <foo> is not recognized!
             * line 6 column 2 - Warning: discarding unexpected <foo>
             * line 6 column 9 - Warning: discarding unexpected </foo>
             * This document has errors that must be fixed before
             * using HTML Tidy to generate a tidied up version.
             *
             * If there are no errors (meaning, there are only warning or
             * no warnings), then the last chunk does not appear.
             */

            String summary = null;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if ( token.startsWith(LINE_WARNING_START) ) {
                    if ( !canBeIgnored(token) ) {
                        warningsAndErrors.add(StringUtils.quoteHtml(token));
                        m_hasWarnings = true;
                    }
                } else {
                    summary = token;
                    if ( st.hasMoreTokens() ) {
                        summary += st.nextToken("");
                    }
                    break;
                }
            }

            StringBuffer sb = new StringBuffer();

            if ( warningsAndErrors.size() > 0 ) {
                if ( warningsAndErrors.size() == 1 ) {
                    sb.append("<p>").append((String) warningsAndErrors.get(0));
                    sb.append("</p>").append(LINE_SEPARATOR);
                } else {
                    sb.append("<ol>").append(LINE_SEPARATOR);
                    for (Iterator i=warningsAndErrors.iterator(); i.hasNext(); ) {
                        sb.append("<li>").append((String) i.next());
                        sb.append("</li>").append(LINE_SEPARATOR);
                    }
                    sb.append("</ol>").append(LINE_SEPARATOR);
                }
            }
            if ( summary != null ) {
                sb.append("<blockquote><p>").append(StringUtils.quoteHtml(summary)).append("</p></blockquote>");
                m_hasErrors = !isWarning(summary);
            }
            m_formattedMessage = sb.toString();
        }
    }

    private static class LockableProperties extends Properties {
        private boolean m_isLocked = false;

        public LockableProperties() {
            super();
        }

        public LockableProperties(Properties defaults) {
            super(defaults);
        }

        public void lock() {
            m_isLocked = true;
        }

        private void checkIfLocked() {
            if (m_isLocked) {
                throw new RuntimeException
                    ("The object cannot be modified once initialized.");
            }
        }

        public Object setProperty(String key, String value) {
            checkIfLocked();
            return super.setProperty(key, value);
        }

        public void load(InputStream is) throws IOException {
            checkIfLocked();
            super.load(is);
        }

        public Object put(Object key, Object value) {
            checkIfLocked();
            return super.put(key, value);
        }

        public void putAll(java.util.Map t) {
            checkIfLocked();
            super.putAll(t);
        }
    }
}
