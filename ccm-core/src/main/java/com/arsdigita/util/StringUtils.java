/*
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
 */

package com.arsdigita.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;

import org.apache.log4j.Logger;

/**
 * A (static) class of generally-useful string utilities.
 *
 * @author Bill Schneider
 */
public class StringUtils {

    private static final Logger s_log = Logger.getLogger(StringUtils.class);

    private static Perl5Util s_re = new Perl5Util();

    public static final String NEW_LINE = System.getProperty("line.separator");


    private StringUtils() {
        // can't instantiate me!
    }


    /**
     * Tests if a string is empty.
     * @param s A string to test
     * @return <code>true</code> if <code>s</code> is null or empty;
     * otherwise <code>false</code>
     */
    public static boolean emptyString(String s) {
        boolean expr = (s == null || s.trim().length() == 0);
        return expr;
    }

    /**
     * Tests if a string is empty.
     * @param o A string to test
     * @return <code>true</code> if <code>o</code> is null or empty;
     * otherwise <code>false</code>
     */
    public static boolean emptyString(Object o) {
        boolean expr =
            (o == null || (o instanceof String && ((String)o).length() ==0));
        return expr;
    }

    /**
     * If the String is null, returns an empty string.  Otherwise,
     * returns the string unaltered
     */
    public static String nullToEmptyString(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * Escapes some "special" characters in HTML text (ampersand, angle
     * brackets, quote).
     * @param s The plain-text string to quote
     * @return The string with special characters escpaed.
     */
    public static String quoteHtml(String s) {
        if (s != null) {
            StringBuffer result = new StringBuffer(s.length() + 10);
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                switch (ch) {
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                default:
                    result.append(ch);
                }
            }
            return result.toString();
        } else {
            return "";
        }
    }

    /**
     * Takes a plaintext string, and returns an HTML string that, when
     * rendered by a web browser, will appear as the original input string
     *
     * @param s The input plaintext string
     * @return A HTML string with blank lines coverted to <pre>&lt;p></pre>
     * and ampersands/angle brackets escaped.
     */
    public static String textToHtml(String s) {
        s = quoteHtml(s);
        s = s_re.substitute("s/\r\n\r\n/<p>/g", s);
        s = s_re.substitute("s/\n\n/<p>/g", s);
        s = s_re.substitute("s/\r\r/<p>/g", s);
        s = s_re.substitute("s/\r\n/<br>/g", s);
        s = s_re.substitute("s/\n/<br>/g", s);
        s = s_re.substitute("s/\r/<br>/g", s);
        return s;
    }

    /**
     * Removes tags and substitutes P tags with newlines.  For much
     * more extensive conversion of HTML fragments to plain text
     * equivalents, see {@link HtmlToText}.
     */
    public static String htmlToText(String s) {
        if (s != null) {
            // first take out new-lines
            s = s_re.substitute("s/\n//g", s);
            s = s_re.substitute("s/\r//g", s);
            s = s_re.substitute("s/<[Pp]>/\n\n/g", s);
            s = s_re.substitute("s/<br>/\n/ig", s);
            // take out other tags
            s = s_re.substitute("s/<([^>]*)>/ /g", s);
            return s;
        } else {
            return "";
        }
    }


    /**
     * Converts plain text with simple inline markup
     * into HTML. The following constructs are recognised:
     *
     * <ul>
     * <li>*text* generates bold <strong>text</strong>
     * <li>/text/ generates italic <em>text</em>
     * <li>=text= generates fixed <code>text</code>
     * <li>@text(http://www.google.com) generates a titled link
     *     <a href="http://www.google.com">text</a>
     * <li>http://www.google.com generates an untitled link
     *     <a href="http://www.google.com">http://www.google.com</a>
     * <li>--- <br/>generates a horizontal line<br/> <hr/>
     * <li>___ <br/>generates a horizontal line<br/> <hr/>
     * <li><p>* my item <br>
     * * my next item<br>
     * * my final item </p>
     * generates an bulleted list
     * <ul>
     *  <li>my item
     *  <li>my next item
     *  <li>my final item
     * </ul>
     * <li><p>+ my item <br>
     * + my next item<br>
     * + my final item </p>
     * generates an enumerated list
     * <ol>
     *  <li>my item
     *  <li>my next item
     *  <li>my final item
     * </ol>
     * <li>1/2, 1/4, 3/4, (C), (TM), (R) generate entities
     *  &frac12;, &frac14, &frac34;, &copy; <sup>TM</sup>
     * &reg;
     * </ul>
     */
    public static String smartTextToHtml(String s) {
        ArrayList blocks = new ArrayList();
        s_re.split(blocks, "/\\r?\\n(\\r?\\n)+/", s);

        StringBuffer html = new StringBuffer("");
        Iterator i = blocks.iterator();
        while (i.hasNext()) {
            String block = (String)i.next();
            if (s_re.match("/^\\s*(___+|---+)\\s*$/", block)) {
                html.append("<hr/>");
            } else if (s_re.match("/^\\*\\s/", block)) {
                html.append(smartTextList("/^\\*\\s+/m", "ul", block));
            } else if (s_re.match("/^\\+\\s/", block)) {
                html.append(smartTextList("/^\\+\\s+/m", "ol", block));
            } else if (s_re.match("/\\w/", block)) {
                html.append("<div>\n" + smartTextInline(block) + "\n</div>");
            }
            html.append("\n");
        }
        return html.toString();
    }

    private static String smartTextList(String match,
                                        String type,
                                        String s) {
        ArrayList blocks = new ArrayList();
        s_re.split(blocks, match, s);

        StringBuffer list = new StringBuffer("<" + type + ">\n");
        Iterator i = blocks.iterator();
        while (i.hasNext()) {
            String block = (String)i.next();

            if ("".equals(block)) {
                continue;
            }

            list.append("<li>\n");
            list.append(smartTextInline(block));
            list.append("</li>\n");
        }
        list.append("</" + type + ">");

        return list.toString();
    }

    private static Map s_entities = new HashMap();
    static {
        s_log.debug("Static initalizer starting...");
        s_entities.put("fraction12", "&frac12;");
        s_entities.put("fraction14", "&frac14;");
        s_entities.put("fraction34", "&frac34;");
        s_entities.put("copyright", "&copy;");
        s_entities.put("registered", "&reg;");
        s_entities.put("trademark", "<sup>TM</sup>");
        s_log.debug("Static initalizer finished.");
    }

    private static String smartTextInline(String s) {
        HashMap links = new HashMap();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Input {" + s + "}");
        }

        // We're going to use the octal characters \u0001 and \u0002 for
        // escaping stuff, so we'd better make sure there aren't any
        // in the text.
        s = s_re.substitute("s/\u0001|\u0002|\u0003//g", s);

        // We transform a few common symbols
        // We don't substitute them straight in because the
        // substituted text might interfere with stuff that
        // follows...
        s = s_re.substitute("s|\\b1/4\\b|\u0003fraction14\u0003|gx", s);
        s = s_re.substitute("s|\\b1/2\\b|\u0003fraction12\u0003|gx", s);
        s = s_re.substitute("s|\\b3/4\\b|\u0003fraction34\u0003|gx", s);
        s = s_re.substitute("s|\\(C\\)|\u0003copyright\u0003|gx", s);
        s = s_re.substitute("s|\\(R\\)|\u0003registered\u0003|gx", s);
        s = s_re.substitute("s|\\(TM\\)|\u0003trademark\u0003|gx", s);
        if (s_log.isDebugEnabled()) {
            s_log.debug("After entities {" + s + "}");
        }

        // We've got to protect the url of titled links before we go further,
        // however we can't actually generate the link yet because
        // that interferes with the monospace stuff below....
        s = s_re.substitute("s|@@|\u0001|gx", s);
        s = smartTextReplace(new TitledLinkSubstitution(links),
                             "@([^\\(@]+)\\(([^\\)]+)\\)", s);

        // We protect hyperlinks so that the '/' or '@' doesn't get
        // mistaken for a block of italics / link
        s = smartTextReplace(new UntitledLinkSubstitution(links),
                             "([a-z]+:\\/\\/[^\\s,\\(\\)><]*)", s);
        s = smartTextReplace(new UntitledLinkSubstitution(links),
                             "(mailto:[^\\s,\\(\\)><]*)", s);
        if (s_log.isDebugEnabled()) {
            s_log.debug("After links {" + s + "}");
        }


        // Next lets process italics /italic/
        // NB. this must be first, otherwise closing tags </foo>
        // interfere with the pattern matching
        s = s_re.substitute("s|//|\u0001|gx", s);
        //s = s_re.substitute("s|(?<!\\w)/([^/]+)/(?!\\w)|<em>$1</em>|gx", s);
        s = s_re.substitute("s|(\\W)/([^/]+)/(?!\\w)|$1<em>$2</em>|gx", s);
        s = s_re.substitute("s|\u0001|/|gx", s);

        // Lets process bold text *bold*
        s = s_re.substitute("s|\\*\\*|\u0001|gx", s);
        //s = s_re.substitute("s|(?<!\\w)\\*([^\\*]+)\\*(?!\\w)|<strong>$1</strong>|gx", s);
        s = s_re.substitute("s|(\\W)\\*([^\\*]+)\\*(?!\\w)|$1<strong>$2</strong>|gx", s);
        s = s_re.substitute("s|\u0001|*|gx", s);

        // Now we're onto the monospace stuff =monospace=
        s = s_re.substitute("s|==|\u0001|gx", s);
        //s = s_re.substitute("s|(?<!\\w)=([^=]+)=(?!\\w)|<code>$1</code>|gx", s);
        s = s_re.substitute("s|(\\W)=([^=]+)=(?!\\w)|$1<code>$2</code>|gx", s);
        s = s_re.substitute("s|\u0001|=|gx", s);

        if (s_log.isDebugEnabled()) {
            s_log.debug("After styles {" + s + "}");
        }


        // Links are next on the list @text(url)
        s = s_re.substitute("s|@@|\u0001|gx", s);
        s = s_re.substitute("s|@([^\\(@]+)\\(([^\\)]+)\\)|<a href=\"$2\">$1</a>|gx", s);
        s = s_re.substitute("s|\u0001|@|gx", s);
        if (s_log.isDebugEnabled()) {
            s_log.debug("After links pass two {" + s + "}");
        }

        // Finally we can unobscure the hyperlinks
        s = smartTextReplace(new UnobscureSubstitution(links),
                             "\u0002([^\u0002]+)\u0002", s);
        s = s_re.substitute("s|\u0001|@|gx", s);
        if (s_log.isDebugEnabled()) {
            s_log.debug("After links pass three {" + s + "}");
        }

        // And those entities
        s = smartTextReplace(new EntitySubstitution(),
                              "\u0003([^\u0003]+)\u0003", s);
        if (s_log.isDebugEnabled()) {
            s_log.debug("After entities (complete) {" + s + "}");
        }

        return s;
    }

    /**
     * 
     * @param subst
     * @param pattern
     * @param s
     * @return 
     */
    private static String smartTextReplace(Substitution subst,
                                           String pattern,
                                           String s) {
        Perl5Matcher matcher = new Perl5Matcher();
        Perl5Compiler compiler = new Perl5Compiler();
        StringBuffer result = new StringBuffer();
        PatternMatcherInput input = new PatternMatcherInput(s);

        try {
            Util.substitute(result,
                            matcher,
                            compiler.compile(pattern),
                            subst,
                            input,
                            Util.SUBSTITUTE_ALL);
        } catch (MalformedPatternException e) {
            throw new UncheckedWrapperException("cannot perform substitution", e);
        }
        return result.toString();
    }

    /**
     * 
     */
    private static class TitledLinkSubstitution implements Substitution {
        private Map m_hash;

        public TitledLinkSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String title = match.group(1);
            String link = match.group(2);
            s_log.debug("Link: " + link);

            Integer i = new Integer(m_hash.size());
            s_log.debug("Key: " + i);
            m_hash.put(i, link);
            String dst = "@" + title + "(\u0002" + i.toString() + "\u0002)";
            appendBuffer.append(dst);
            s_log.debug("Encoded Link: " + dst);
        }
    }

    /**
     * 
     */
    private static class UntitledLinkSubstitution implements Substitution {
        private Map m_hash;

        public UntitledLinkSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String link = match.group(1);
            s_log.debug("Link: " + link);

            Integer i = new Integer(m_hash.size());
            s_log.debug("Key: " + i);
            m_hash.put(i, link);
            String dst = "@\u0002" + i.toString() + "\u0002(\u0002" +
                          i.toString() + "\u0002)";
            appendBuffer.append(dst);
            s_log.debug("Encoded Link: " + dst);
        }
    }

    /**
     * 
     */
    private static class UnobscureSubstitution implements Substitution {
        private Map m_hash;

        public UnobscureSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String s = match.group(1);
            s_log.debug("Key: " + s);

            Integer i = new Integer(s);
            appendBuffer.append((String)m_hash.get(i));
            s_log.debug("Link: " + m_hash.get(i));
        }
    }

    /**
     * 
     */
    private static class EntitySubstitution implements Substitution {
        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String s = match.group(1);
            s_log.debug("Key: " + s);

            appendBuffer.append((String)s_entities.get(s));
            s_log.debug("Entity: " + s_entities.get(s));
        }
    }


    /**
     * Convert a string of items separated by a separator
     * character to an (string)array of the items.  sep is the separator
     * character.  Example: Input - s == "cat,house,dog" sep==','
     * Output - {"cat", "house", "dog"}
     * @param s string contains items separated by a separator character.
     * @param sep separator character.
     * @return Array of items.
     */
    public static String [] split(String s, char sep) {
        ArrayList al = new ArrayList();
        int start_pos, end_pos;
        start_pos = 0;
        while (start_pos < s.length()) {
            end_pos = s.indexOf(sep, start_pos);
            if (end_pos == -1) {
                end_pos = s.length();
            }
            String found_item = s.substring(start_pos, end_pos);
            al.add(found_item);
            start_pos = end_pos + 1;
        }
        if (s.length() > 0 && s.charAt(s.length()-1) == sep) {
            al.add("");  // In case last character is separator
        }
        String [] returned_array = new String[al.size()];
        al.toArray(returned_array);
        return returned_array;
    }

    /**
     * <p> Given a string, split it into substrings matching a regular
     * expression that you supply. Parts of the original string which
     * don't match the regular expression also appear as substrings. The
     * upshot of this is that the final substrings can be concatenated
     * to get the original string.  </p>
     *
     * <p> As an example, let's say the original string is: </p>
     *
     * <pre>
     * s = "/packages/foo/xsl/::vhost::/foo_::locale::.xsl";
     * </pre>
     *
     * <p> We call the function like this: </p>
     *
     * <pre>
     * output = splitUp (s, "/::\\w+::/");
     * </pre>
     *
     * <p> The result (<code>output</code>) will be the following list: </p>
     *
     * <pre>
     * ("/packages/foo/xsl/", "::vhost::", "/foo_", "::locale::", ".xsl")
     * </pre>
     *
     * <p> Notice the important property that concatenating all these
     * strings together will restore the original string. </p>
     *
     * <p> Here is another useful example. To split up HTML into elements
     * and content, do: </p>
     *
     * <pre>
     * output = splitUp (html, "/<.*?>/");
     * </pre>
     *
     * <p> You will end up with something like this: </p>
     *
     * <pre>
     * ("The following text will be ", "<b>", "bold", "</b>", ".")
     * </pre>
     *
     * @param s The original string to split.
     * @param re The regular expression in the format required by
     * {@link org.apache.oro.text.perl.Perl5Util#match(String, String)}.
     * @return List of substrings.
     *
     * @author Richard W.M. Jones
     *
     * <p> This is equivalent to the Perl "global match in array context",
     * specifically: <code>@a = /(RE)|(.+)/g;</code> </p>
     *
     */
    public static List splitUp (String s, String re)
    {
        Perl5Util p5 = new Perl5Util ();
        ArrayList list = new ArrayList ();

        while (s != null && s.length() > 0) {
            // Find the next match.
            if (p5.match (re, s)) {
                MatchResult result = p5.getMatch ();

                // String up to the start of the match.
                if (result.beginOffset (0) > 0)
                    list.add (s.substring (0, result.beginOffset (0)));

                // Matching part.
                list.add (result.toString ());

                // Update s to be the remainder of the string.
                s = s.substring (result.endOffset (0));
            }
            else {
                // Finished.
                list.add (s);

                s = null;
            }
        }

        return list;
    }

    /**
     * Converts an array of Strings into a single String separated by
     * a given character.
     * Example Input: {"cat", "house", "dog"}, ','
     * Output -  "cat,house,dog"
     *
     * @param strings The string array too join.
     * @param joinChar The character to join the array members together.
     *
     * @pre strings != null
     *
     * @return Joined String
     */
    public static String join(String[] strings, char joinChar) {
        StringBuffer result = new StringBuffer();
        final int lastIdx = strings.length - 1;
        for (int idx = 0; idx < strings.length; idx++) {
            result.append(strings[idx]);
            if (idx < lastIdx) {
                result.append(joinChar);
            }
        }

        return result.toString();
    }
    
    /**
     * Converts an array of Strings into a single String separated by
     * a given string.
     * Example Input: {"cat", "house", "dog"}, ", "
     * Output -  "cat, house, dog"
     *
     * @param strings The string array too join.
     * @param joinStr The string to join the array members together.
     *
     * @pre strings != null
     *
     * @return Joined String
     */
    public static String join(String[] strings, String joinStr) {
        StringBuffer result = new StringBuffer();
        final int lastIdx = strings.length - 1;
        for (int idx = 0; idx < strings.length; idx++) {
            result.append(strings[idx]);
            if (idx < lastIdx) {
                result.append(joinStr);
            }
        }

        return result.toString();
    }
    
    /**
     * Extract a parameter value from a packed list of parameter values.
     * Example: 
     *    input: key="age", sep=',',
     *    plist="cost=23,age=27,name=Thom"
     *    output = "27".
     * This is a simple implementation that is meant for controlled use in which
     * the key and values are known to be safe. 
     * Specifically, the equals character must be used to indicate
     * parameter assignments.  There is no escape character.  Thus the
     * parameter names and values cannot contain the equals character or the
     * separator character.
     *
     * @param key the key indicating which parameter value to extract.
     * @param plist packed list of key=value assignments.  The character '='
     *   must be used to indicate the assignment.
     * @param sep separator character.
     * @return the value corresponding to the key, or null if the key is not
     *         present.  If the key appears in the list more than once,
     *         the first value is returned.
     */
    public static String getParameter(String key, String plist, char sep) {
        int key_end;
        int key_start = 0;
        String found_value;
        while (key_start < plist.length()) {
            key_start = plist.indexOf(key, key_start);
            if (key_start == -1) {
                return null;   // Did not find key
            }
            key_end = key_start + key.length();
            if (plist.charAt(key_end) == '=' &&
                (key_start == 0 || plist.charAt(key_start - 1) == sep)) {
                // Found isolated parameter value, this is the match
                int value_end = plist.indexOf(sep, key_end);
                if (value_end == -1) {
                    // did not find another separator, return value
                    found_value = plist.substring(key_end + 1);
                } else {
                    // found another separator, return value
                    found_value = plist.substring(key_end + 1, value_end);
                }
                return found_value;
            } else {
                key_start++;   // did not find.  Advance past current position
            }
        }
        return null;
    }

    /**
     * Strip extra white space from a string.  This replaces any white space
     * character or consecutive white space characters with a single space.
     * It is useful when comparing strings that should be equal except for
     * possible differences in white space.  Example:  input = "I  \ndo\tsee".
     * Output = "I do see".
     * @param s string that may contain extra white space
     * @return string the same as the input, but with extra white space
     * removed and replaced by a single space.
     */
    static public String stripWhiteSpace(String s) {
        StringBuffer to = new StringBuffer();
        boolean inSpace = true;
        boolean isSpace;
        char c;
        for (int i=0; i<s.length(); i++) {
            c = s.charAt(i);
            isSpace = Character.isWhitespace(c);
            if (!isSpace) {
                to.append(c);
                inSpace = false;
            } else if (!inSpace) {
                to.append(' ');
                inSpace = true;
            }
        }
        return to.toString().trim();
    }

    /**
     * Get a String representation for an Object.  If it has an
     * asString method, use that; otherwise fall back on toString
     */
    public static String toString(Object o) {
        try {
            return (String) o.getClass().getMethod("asString", null)
                .invoke(o, new Object[0]);
        } catch (NoSuchMethodException e) {
            return o.toString();
        } catch (Exception e) {
            throw new UncheckedWrapperException
                ("Invoking asString() on an " + o.getClass(), e);
        }
    }

    /**
     * create a String representation of a map.  This method is not
     * too necessary, because Map.toString() does almost the same.
     */
    public static String toString(Map m) {
        StringBuffer to = new StringBuffer();
        if (m == null) {
            to.append("null");
        } else {
            to.append(m.getClass().getName());
            Set entrySet = m.entrySet();
            if (entrySet == null) {
                to.append("[null entrySet]");
            } else {
                Iterator entries = entrySet.iterator();
                if (entries == null) {
                    to.append("[null iterator]");
                } else {
                    to.append("{");
                    String comma = NEW_LINE;

                    while (entries.hasNext()) {
                        to.append(comma);
                        comma = "," + NEW_LINE;
                        Map.Entry e = (Map.Entry)entries.next();

                        to  .append(toString(e.getKey()))
                            .append(" => ")
                            .append(toString(e.getValue()));
                    }
                    to.append(NEW_LINE).append("}");
                }
            }
        }
        String result = to.toString();
        return result;
    }

    /**
     * Strips all new-line characters from the input string.
     * @param str a string to strip
     * @return the input string with all new-line characters
     * removed.
     * @post result.indexOf('\r') == 0
     * @post result.indexOf('\n') == 0
     */
    public static String stripNewLines(String str) {
        int len = str.length();
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch != '\r' && ch != '\n') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * <p>Add a possible newline for proper wrapping.</p>
     *
     * <p>Checks the given String to see if it ends with whitspace.  If so, it
     * assumes this whitespace is intentional formatting and returns a reference
     * to the original string.  If not, a new <code>String</code> object is
     * created containing the original plus a platform-dependent newline
     * character obtained from {@link System#getProperty(String)
     * System.getProperty("line.separator")}.</p>
     */
    public static String addNewline(String s) {
        int n = s.length()-1;
        if (n == -1) {
            return s;
        } else if (Character.isWhitespace(s.charAt(n))) {
            return s;
        } else {
            return s.concat(NEW_LINE);
        }
    }


    /**
     *  This takes the passed in string and truncates it.
     *  It cuts the string off at the length specified and then
     *  goes back to the most recent space and truncates any
     *  word that may have been cut off.  It also takes the
     *  string and converts it to plain text so that no HTML
     *  will be shown.
     */
    public static String truncateString(String s, int length) {
        return truncateString(s, length, true);
    }


    /**
     *  This takes the passed in string and truncates it.
     *  It cuts the string off at the length specified and then
     *  goes back to the most recent space and truncates any
     *  word that may have been cut off.  The htmlToText dictates
     *  whehter or not the string should be converted from HTML to
     *  text before being truncated
     *
     *  @param s The string to be truncated
     *  @param length The length which to truncate the string
     *  @param removeHTML Whether or not to convert the HTML to text
     */
    public static String truncateString(String s, int length,
                                        boolean removeHTML) {
        if (s == null) {
            return "";
        }

        String string = s;
        if (removeHTML) {
            string = htmlToText(string);
        }

        if (string.length() <= length) {
            return string;
        }

        return string.substring(0, string.lastIndexOf(" ", length));
    }


    /**
     * "join" a List of Strings into a single string, with each string
     * separated by a defined separator string.
     *
     * @param elements the strings to join together
     * @param sep the separator string
     * @return the strings joined together
     */
    public static String join(List elements, String sep) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator iter = elements.iterator();

        while (iter.hasNext()) {
            String element = (String)iter.next();

            if (!first) {
                sb.append(sep);
            } else {
                first = false;
            }

            sb.append(element);
        }

        return sb.toString();
    }

    /**
     * Removes whitespace from the beginning of a string. If the
     * string consists of nothing but whitespace characters, an empty
     * string is returned.
     */
    public final static String trimleft(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return s.substring(i);
            }
        }
        return "";
    }

    /**
     * Returns a String containing the specified repeat count of a
     * given pattern String.
     *
     * @param pattern the pattern String
     * @param repeatCount the number of time to repeat it
     */
    public static String repeat(String pattern, int repeatCount) {
        StringBuffer sb = new StringBuffer(repeatCount * pattern.length());
        for (int i = 0; i < repeatCount; i++) {
            sb.append(pattern);
        }
        return sb.toString();
    }

    /**
     * Returns a String containing the specified repeat count of a
     * given pattern character.
     *
     * @param pattern the pattern character
     * @param repeatCount the number of time to repeat it
     */
    public static String repeat(char pattern, int repeatCount) {
        return repeat(String.valueOf(pattern), repeatCount);
    }

    /**
     * Wrap a string to be no wider than 80 characters.  This is just
     * a convenience method for calling the more general method with a
     * default string width.
     *
     * @param input the String to wrap
     *
     * @since  5.1.2
     */
    public static String wrap(String input) {
        return wrap(input,80);
    }

    /**
     * Wrap a string to be no wider than a specified number of
     * characters by inserting line breaks.  If the input is null or
     * the empty string, a string consisting of only the newline
     * character will be returned. Otherwise the input string will be
     * wrapped to the specified line length.  In all cases the last
     * character of the return value will be a single newline.
     *
     * <p>Notes:
     *
     * <ol>
     * <li>line breaks in the input string are preserved
     * <li>wrapping is "soft" in that lines in the output string may
     *     be longer than maxLength if they consist of contiguous
     *     non-whitespace characters.
     * </ol>
     *
     * @param input the String to wrap
     * @param maxLength the maximum number of characters between line
     * breaks
     *
     * @since  5.1.2
     */
    public static String wrap(String input, int maxLength) {

        final char SPACE = ' ';
        final char ENDL  = '\n';

        // Make sure that we start with a string terminated by a
        // newline character.  Some of the index calculations below
        // depend on this.

        if (emptyString(input)) {
            return String.valueOf(ENDL);
        } else {
            input = input.trim() + String.valueOf(ENDL);
        }

        StringBuffer output = new StringBuffer();

        int startOfLine = 0;

        while (startOfLine < input.length()) {

            String line = input.substring
                (startOfLine, Math.min(input.length(),
                                       startOfLine + maxLength));

            if (line.equals("")) {
                break;
            }

            int firstNewLine = line.indexOf(ENDL);
            if (firstNewLine != -1) {

                // there is a newline
                output.append
                    (input.substring(startOfLine,
                                     startOfLine + firstNewLine));
                output.append(ENDL);
                startOfLine += firstNewLine + 1;
                continue;
            }

            if (startOfLine + maxLength > input.length()) {

                // we're on the last line and it is < maxLength so
                // just return it

                output.append(line);
                break;
            }

            int lastSpace = line.lastIndexOf(SPACE);
            if (lastSpace == -1) {

                // no space found!  Try the first space in the whole
                // rest of the string

                int nextSpace = input.indexOf
                    (SPACE, startOfLine);
                int nextNewLine = input.indexOf
                    (ENDL, startOfLine);

                if (nextSpace == -1) {
                    lastSpace = nextNewLine;
                } else {
                    lastSpace = Math.min
                        (nextSpace,nextNewLine);
                }

                if (lastSpace == -1) {
                    // didn't find any more whitespace, append the
                    // whole thing as a line
                    output.append(input.substring(startOfLine));
                    break;
                }

                // code below will add this to the start of the line

                lastSpace -= startOfLine;
            }

            // append up to the last space

            output.append(input.substring(startOfLine,
                                          startOfLine + lastSpace));
            output.append(ENDL);

            startOfLine += lastSpace + 1;
        }

        return output.toString();
    }

    /**
     * Returns true if the String is AlphaNumeric. Obviously, this is not at all 
     * globalized and should only be used with English text.
     *
     * @param value String to check
     * @return true if value is alphanumeric, false otherwise.
     */
    public static boolean isAlphaNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9'))) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method performs interpolation on multiple variables.
     * The keys in the hash table correspond directly to the placeholders
     * in the string. The values in the hash table can either be
     * plain strings, or an instance of the PlaceholderValueGenerator
     * interface
     *
     * Variable placeholders are indicated in text by surrounding
     * a key word with a pair of colons. The keys in the hash
     * table correspond to the names
     *
     * eg. "::forename:: has the email address ::email::"
     *
     * @see java.text.MessageFormat
     *
     * @param text the text to interpolate
     * @param vars a hash table containing key -> value mappings
     */
    public static String interpolate(String text, Map vars) {
        HashSubstitution subst = new HashSubstitution(vars);
        Perl5Matcher matcher = new Perl5Matcher();
        Perl5Compiler compiler = new Perl5Compiler();
        StringBuffer result = new StringBuffer();
        PatternMatcherInput input = new PatternMatcherInput(text);

        try {
            Util.substitute(result,
                            matcher,
                            compiler.compile("(::(?:\\w+(?:[.-]+\\w+)*)::)"),
                            subst,
                            input,
                            Util.SUBSTITUTE_ALL);
        } catch (MalformedPatternException e) {
            throw new UncheckedWrapperException("cannot perform substitution", e);
        }
        return result.toString();
    }


    /**
     * THis method performs a single variable substitution
     * on a string. The placeholder takes the form of
     * ::key:: within the sample text.
     *
     * @see java.text.MessageFormat
     *
     * @param text the text to process for substitutions
     * @param key the name of the placeholder
     * @param value the value to insert upon encountering a placeholder
     */
    public static String interpolate(String text, String key, String value) {
        String pattern = "s/::" + key + "::/" + value + "/";

        return s_re.substitute(pattern, text);
    }



    /**
     * Finds all occurrences of <code>find</code> in <code>str</code> and
     * replaces them with them with <code>replace</code>.
     *
     * @pre find != null
     * @pre replace != null
     */
    public static String replace(final String str,
                                 final String find,
                                 final String replace) {

        Assert.exists(find, String.class);
        Assert.exists(replace, String.class);

        if ( str == null ) return null;

        int cur = str.indexOf(find);
        if ( cur < 0 ) return str;

        final int findLength = find.length();
        // If replace is longer than find, assume the result is going to be
        // slightly longer than the original string.
        final int bufferLength =
            replace.length() > findLength ? (int) (str.length() * 1.1) : str.length();
        StringBuffer sb = new StringBuffer(bufferLength);
        int last = 0;

        if ( cur == 0 ) {
            sb.append(replace);
            cur = str.indexOf(find, cur+findLength);
            last = findLength;
        }

        while ( cur > 0 ) {
            sb.append(str.substring(last, cur));
            sb.append(replace);
            last = cur + findLength;
            cur = str.indexOf(find, cur+findLength);
        }
        if ( last < str.length()-1) {
            sb.append(str.substring(last));
        }

        return sb.toString();
    }


    /**
     * An interface allowing the value for a placeholder to be
     * dynamically generated.
     */
    public interface PlaceholderValueGenerator {
        /**
         * Returns the value corresponding to the supplied key
         * placeholder.
         *
         * @param key the key being substituted
         */
        public String generate(String key);
    }



    /**
     * 
     */
    private static class HashSubstitution implements Substitution {
        private Map m_hash;

        public HashSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String placeholder = match.toString();
            String key = placeholder.substring(2, placeholder.length()-2);

            Object value = (m_hash.containsKey(key) ?
                            m_hash.get(key) :
                            placeholder);

            if( s_log.isDebugEnabled() ) {
                Object hashValue = m_hash.get( key );

                s_log.debug( "Placeholder: " + placeholder );
                s_log.debug( "Key: " + key );
                if( null != value ) {
                    s_log.debug( "Value (" + value.getClass().getName() +
                                 "): " + value.toString() );
                }
                if( null != hashValue ) {
                    s_log.debug( "Hash Value (" +
                                 hashValue.getClass().getName() + "): " +
                                 hashValue.toString() );
                }
            }

            value = (m_hash.containsKey(key) ? m_hash.get(key) : "");

            String val;
            if( value instanceof PlaceholderValueGenerator ) {
                PlaceholderValueGenerator gen = (PlaceholderValueGenerator)value;
                val = gen.generate(key);
            } else if( value.getClass().isArray() ) {
                Object[] values = (Object[]) value;

                StringBuffer buf = new StringBuffer();
                for( int i = 0; i < values.length; i++ ) {
                    buf.append( values[i].toString() );
                    if( (values.length - 1) != i ) {
                        buf.append( ", " );
                    }
                }

                val = buf.toString();
            } else {
                val = value.toString();
            }

            appendBuffer.append(val);
        }
    }

    /**
     * @throws NullPointerException if <code>throwable</code> is null
     */
    public static String getStackTrace(Throwable throwable) {
        if (throwable==null) { throw new NullPointerException("throwable"); }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    /**
     * Returns a list of lines where each line represents one level
     * in the stack trace captured by <code>throwable</code>.
     *
     * <p>For a stack trace like this:</p>
     *
     * <pre>
     * java.lang.Throwable
     *         at Main.level3(Main.java:19)
     *         at Main.level2(Main.java:15)
     *         at Main.level1(Main.java:11)
     *         at Main.main(Main.java:7)
     * </pre>
     *
     * <p>the returned list looks like this: </p>
     *
     * <pre>
     * ["java.lang.Throwable",
     *  "Main.level3(Main.java:20)",
     *  "Main.level2(Main.java:15)",
     *  "Main.level1(Main.java:11)",
     *  "Main.main(Main.java:7)"]
     * </pre>
     *
     * @see #getStackTrace(Throwable)
     * @throws NullPointerException if <code>throwable</code> is null
     */
    public static List getStackList(Throwable throwable) {
        StringTokenizer tkn = new StringTokenizer
            (getStackTrace(throwable), System.getProperty("line.separator"));
        List list = new LinkedList();
        while ( tkn.hasMoreTokens() ) {
            String token = tkn.nextToken().trim();
            if ( "".equals(token) ) { continue; }
            if ( token.startsWith("at ") ) {
                list.add(token.substring(3));
            } else {
                list.add(token);
            }
        }

        return list;
    }

    /**
     * Convert a name into a URL form, the java equivalent of
     * "<code>manipulate-input.js</code>"
     *
     * For example, "<code>Business promotions!</code>" will be converted to
     *              "<code>business-promotions</code>".
     *
     * @param name
     *            the to be converted into a URL.
     * @return the converted name, possibly unchanged and null if the input is null.
     */
    public static String urlize(String name) {
        if (name == null) {
            return null;
        }
        StringBuffer urlizedName = new StringBuffer(name.length());

        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);

            if (Character.isLetter(ch)) {
                urlizedName.append(Character.toLowerCase(ch));
            }
            else if (Character.isDigit(ch) || ch == '_' || ch == '-') {
                urlizedName.append(ch);
            }
            else if (ch == ' ' || ch == '&' || ch == '/') {
                urlizedName.append('-');
            }
        }
        return urlizedName.toString();
    }
}


