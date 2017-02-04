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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A (static) class of generally-useful string utilities.
 *
 * @author Bill Schneider
 */
@SuppressWarnings({"PMD.GodClass",
    "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.CyclomaticComplexity"})
public final class StringUtils {

    private static final Logger LOGGER = LogManager.getLogger(StringUtils.class);

    public static final String NEW_LINE = System.getProperty("line.separator");
    //html line break:
    public static final String LINE_BREAK = "<br>";

    private StringUtils() {
        // can't instantiate me!
    }

    /**
     * Tests if a string is empty.
     *
     * @param str A string to test
     * @return <code>true</code> if <code>str</code> is null or empty; otherwise
     * <code>false</code>
     *
     */
    public static boolean emptyString(final String str) {
        if (str != null) {
            return str.isEmpty();
        }
        return true;
    }

    /**
     * Tests if a string is empty.
     *
     * @param obj A string to test
     * @return <code>true</code> if <code>o</code> is null or empty; otherwise
     * <code>false</code>
     */
    public static boolean emptyString(final Object obj) {
        final boolean expr
                = obj == null || obj instanceof String && ((String) obj).length() == 0;
        return expr;
    }

    /**
     * If the String is null, returns an empty string. Otherwise, returns the
     * string unaltered
     *
     * @param str
     * @return empty or unalterd string
     */
    public static String nullToEmptyString(final String str) {
        return (str == null) ? "" : str;
    }

    /**
     * Escapes some "special" characters in HTML text (ampersand, angle
     * brackets, quote).
     *
     * @param str The plain-text string to quote
     * @return The string with special characters escaped.
     */
    public static String quoteHtml(final String str) {
        if (str == null) {
            return "";
        } else {
            String result;
            result = str.replaceAll("&", "&amp;");
            result = result.replaceAll("\"", "&quot;");
            result = result.replaceAll("<", "&lt;");
            result = result.replaceAll(">", "&gt;");
            return result;
        }
    }

    /**
     * Takes a plaintext string, and returns an HTML string that, when rendered
     * by a web browser, will appear as the original input string
     *
     * @param str The input plaintext string
     * @return A HTML string with blank lines coverted to <pre>&lt;p></pre> and
     * ampersands/angle brackets escaped.
     */
    public static String textToHtml(final String str) {
        String result;
        result = quoteHtml(str);
        result = result.replaceAll("\r\n\r\n", "<p>");
        result = result.replaceAll("\n\n", "<p>");
        result = result.replaceAll("\r\r", "<p>");
        result = result.replaceAll("\r\n", LINE_BREAK);
        result = result.replaceAll("\n", LINE_BREAK);
        result = result.replaceAll("\r", LINE_BREAK);
        return result;
    }

    /**
     * Removes tags and substitutes P tags with newlines. For much more
     * extensive conversion of HTML fragments to plain text equivalents, see
     * {@link HtmlToText}.
     */
    public static String htmlToText(final String str) {
        if (str == null) {
            return "";
        } else {
            // first take out new-lines
            String result = str.replaceAll("\n", "");
            result = result.replaceAll("\r", "");
            result = result.replaceAll("<[pP]>", "\n\n");
            result = result.replaceAll(LINE_BREAK, "\n");
            // take out other tags
            result = result.replaceAll("<[^>]*>", " ");
            return result;
        }
    }

    /**
     * Converts plain text with simple inline markup into HTML. The following
     * constructs are recognised:
     *
     * <ul>
     * <li>*text* generates bold <strong>text</strong>
     * <li>/text/ generates italic <em>text</em>
     * <li>=text= generates fixed <code>text</code>
     * <li>@text(http://www.google.com) generates a titled link
     * <a href="http://www.google.com">text</a>
     * <li>http://www.google.com generates an untitled link
     * <a href="http://www.google.com">http://www.google.com</a>
     * <li>--- <br/>generates a horizontal line<br/> <hr/>
     * <li>___ <br/>generates a horizontal line<br/> <hr/>
     * <li><p>
     * my item <br>
     * * my next item<br>
     * * my final item </p>
     * generates an bulleted list
     * <ul>
     * <li>my item
     * <li>my next item
     * <li>my final item
     * </ul>
     * <li><p>
     * + my item <br>
     * + my next item<br>
     * + my final item </p>
     * generates an enumerated list
     * <ol>
     * <li>my item
     * <li>my next item
     * <li>my final item
     * </ol>
     * <li>1/2, 1/4, 3/4, (C), (TM), (R) generate entities &frac12;, &frac14,
     * &frac34;, &copy; <sup>TM</sup>
     * &reg;
     * </ul>
     */
    public static String smartTextToHtml(final String str) {

        //first splits the string at every new line
//        String blocks2[] = str.split( "\r?\n(\r?(?<=\n))"); 
//          Arrays.asList(str.split( "?<=\r?\n\r?\n")
//        ArrayList blocks = new ArrayList();
        final List<String> blocks = splitUpAtNewLine(str);

//        s_re.split(blocks, "/\\r?\\n(\\r?\\n)+/", s);
        final Pattern hrpattern = Pattern.compile("-{3,}|_{3,}");
        final Pattern asterisk = Pattern.compile("^\\*");
        final Pattern plus = Pattern.compile("^\\+");
        final Pattern word = Pattern.compile(".*[a-zA-Z_0-9\\*\\+].*"); // \\w

        final StringBuffer html = new StringBuffer(20);
        final Iterator iter = blocks.iterator();
        while (iter.hasNext()) {
            final String block = (String) iter.next();

            final Matcher hrmatcher = hrpattern.matcher(block);
            final Matcher asteriskmatcher = asterisk.matcher(block);
            final Matcher plusmatcher = plus.matcher(block);
            final Matcher wordmatcher = word.matcher(block);

            if (hrmatcher.find()) { // horizontal line 
                html.append("<hr/>");
            } else if (asteriskmatcher.find()) {
                html.append(smartTextList("(?m)^\\*+\\s", "ul", block)); //bulleted list
            } else if (plusmatcher.find()) {
                html.append(smartTextList("(?m)^\\++\\s", "ol", block)); //numerated list
            } else if (wordmatcher.find()) {
                html.append("<div>\n" + smartTextInline(block) + "\n</div>");
            }
            html.append('\n');
        }
        return html.toString();
    }

    /**
     *
     * @param match
     * @param type
     * @param str
     * @return
     */
    private static String smartTextList(final String match,
            final String type,
            final String str) {

        final String[] blocks = str.split(match);

        final StringBuffer list = new StringBuffer("<" + type + ">\n");

        for (int j = 0; j < blocks.length; j++) {
            final String block = blocks[j];
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

    /**
     * dont use directly, use smartTextToHtml instead
     *
     * @param str
     * @return
     */
    private static String smartTextInline(final String str) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Input {" + str + "}");
        }

        // We're going to use the octal characters \u0001 and \u0002 for
        // escaping stuff, so we'd better make sure there aren't any
        // in the text.
        String result;
        result = str.replaceAll("[\u0001|\u0002|\u0003]", "");

        // We transform a few common symbols
        result = result.replaceAll("1/4", "&frac14;");
        result = result.replaceAll("1/2", "&frac12;");
        result = result.replaceAll("3/4", "&frac34;");
        result = result.replaceAll("\\([Cc]\\)", "&copy;");
        result = result.replaceAll("\\([Rr]\\)", "&reg;");
        result = result.replaceAll("\\(TM\\)|\\(tm\\)", "<sup>TM</sup>");
        result = result.replaceAll("^\\+", "");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("After entities {" + result + "}");
        }

        // Next lets process italics /italic/
        result = result.replaceAll("/+([a-zA-Z_0-9]+)+/", "<em>$1</em>");
        // Lets process bold text *bold*
        result = result.replaceAll("\\*+([a-zA-Z_0-9]+)+\\*", "<strong>$1</strong>");
        // Now we're onto the monospace stuff =monospace=
        result = result.replaceAll("\\=+([a-zA-Z_0-9]+)+\\=", "<code>$1</code>");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("After styles {" + result + "}");
        }

        // untitled mailto    
        //"mailto:dan@berrange.com" to
        //"<a href=\"mailto:dan@berrange.com\">mailto:dan@berrange.com</a>"
        result = result.replaceAll("mailto:([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[A-Za-z]{2,4})\\b",
                "<a href=\\\"mailto:$1\\\">mailto:$1</a>");

        //adding a '@' before every untitled link so it does not interfere with 
        // titled links
        result = result.replaceAll("(http[s]*://www\\..+\\.[A-Za-z]{2,4})",
                "@$1");

        // titled Links
        //"@google(http://www.google.com) to
        //<a href=\"http://www.google.com\">google</a>"
        result = result.replaceAll("@(\\w+)\\(@(http*[s]*:*/*/*www\\..+\\.[A-Za-z]{2,4})\\)",
                "<a href=\\\"$2\\\">$1</a>");
        //titled mailto
        //"@Dan B(mailto:dan@@berrange.com)" to 
        //"<a href=\"mailto:dan@berrange.com\">Dan B</a>"
        result = result.replaceAll("@([\\w\\s]+)\\(mailto:([a-zA-Z0-9._%+-]+@@[a-zA-Z0-9.-]+\\.[A-Za-z]{2,4})\\)",
                "<a href=\\\"mailto:$2\\\">$1</a>");
        //remove one of the two @'s
        result = result.replaceAll("mailto:([a-zA-Z0-9._%+-]+)+@@([a-zA-Z0-9.-]+\\.[A-Za-z]{2,4})",
                "mailto:$1@$2");

        //untitled links (which got an '@' in front now)
        result = result.replaceAll("@(http[s]*://www\\..+\\.[A-Za-z]{2,4})",
                "<a href=\\\"$1\\\">$1</a>");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("After links {" + result + "}");
        }

        return result;
    }

    /**
     * Convert a string of items separated by a separator character to an
     * (string)array of the items. sep is the separator character. Example:
     * Input - str == "cat,house,dog,," sep==',' Output - {"cat", "house",
     * "dog", "" ,""} different to java.lang.String.split(): Input - str ==
     * "cat,house,dog,," sep==',' Output - {"cat", "house", "dog"}
     *
     * @param str string contains items separated by a separator character.
     * @param sep separator character.
     * @return Array of items.
     *
     */
    public static String[] split(final String str, final char sep) {
        final ArrayList arrl = new ArrayList();
        int startpos, endpos;
        startpos = 0;
        while (startpos < str.length()) {
            endpos = str.indexOf(sep, startpos);
            if (endpos == -1) {
                endpos = str.length();
            }
            final String found_item = str.substring(startpos, endpos);
            arrl.add(found_item);
            startpos = endpos + 1;
        }
        if (str.length() > 0 && str.charAt(str.length() - 1) == sep) {
            arrl.add("");  // In case last character is separator
        }
        final String[] returnedArray = new String[arrl.size()];
        arrl.toArray(returnedArray);
        return returnedArray;
    }

    /**
     * <p>
     * Given a string, split it into substrings matching a regular expression
     * that you supply. Parts of the original string which don't match the
     * regular expression also appear as substrings. The upshot of this is that
     * the final substrings can be concatenated to get the original string.  </p>
     *
     * <p>
     * As an example, let's say the original string is: </p>
     *
     * <pre>
     * str = "/packages/foo/xsl/::vhost::/foo_::locale::.xsl";
     * </pre>
     *
     * <p>
     * We call the function like this: </p>
     *
     * <pre>
     * output = splitUp (str, "::\\w+::");
     * </pre>
     *
     * <p>
     * The result (<code>output</code>) will be the following list: </p>
     *
     * <pre>
     * ("/packages/foo/xsl/", "::vhost::", "/foo_", "::locale::", ".xsl")
     * </pre>
     *
     * <p>
     * Notice the important property that concatenating all these strings
     * together will restore the original string. </p>
     *
     * <p>
     * Here is another useful example. To split up HTML into elements and
     * content, do: </p>
     *
     * <pre>
     * output = splitUp (html, "/<.*?>/");
     * </pre>
     *
     * <p>
     * You will end up with something like this: </p>
     *
     * <pre>
     * ("The following text will be ", "<b>", "bold", "</b>", ".")
     * </pre>
     *
     * @param str The original string to split.
     * @param regex The regular expression.
     * @return List of substrings.
     *
     * @author Richard W.M. Jones
     *
     * <p>
     * This is equivalent to the Perl "global match in array context",
     * specifically: <code>@a = /(RE)|(.+)/g;</code> </p>
     *
     */
    public static List splitUp(final String str, final String regex) {
        final ArrayList list = new ArrayList();
        final Pattern pattern = Pattern.compile(regex);
        String string = str;
        while (string != null && string.length() > 0) {
            // Find the next match.
            final Matcher matcher = pattern.matcher(string);
            if (matcher.find()) {
                final int start = matcher.start();
                final int end = matcher.end();

                // String up to the start of the match.
                list.add(string.substring(0, start));

                // Matching part.
                list.add(string.substring(start, end));

                // Update str to be the remainder of the string.
                string = string.substring(end);
            } else {
                // Finished.
                list.add(string);

                string = null;
            }
        }

        return list;
    }

    /**
     * Splits a String into substrings if there is a linebreak followed by
     * anoter linebreak. Keeps the empty line as an own block. needed for
     * smartTextToHtml.
     *
     * @param str The string to split
     * @return List
     */
    public static List splitUpAtNewLine(final String str) {
        final ArrayList list = new ArrayList();
        final Pattern pattern = Pattern.compile("\r?\n\r?\n");
        String string = str;
        while (string != null && string.length() > 0) {
            // Find the next match.
            final Matcher matcher = pattern.matcher(string);
            if (matcher.find()) {
                final int start = matcher.start();
                final int end = matcher.end();

                // String up to the start of the match. first block
                list.add(string.substring(0, start));

                // second block, an empty block.
                list.add("\n");

                // Update str to be the remainder of the string.
                string = string.substring(end);
            } else {
                // Finished.
                list.add(string);

                string = null;
            }
        }
        return list;
    }

    /**
     * Converts an array of Strings into a single String separated by a given
     * character. Example Input: {"cat", "house", "dog"}, ',' Output -
     * "cat,house,dog"
     *
     * @param strings The string array too join.
     * @param joinChar The character to join the array members together.
     *
     * @pre strings != null
     *
     * @return Joined String
     */
    public static String join(final String[] strings, final char joinChar) {
        final StringBuffer result = new StringBuffer();
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
     * Converts an array of Strings into a single String separated by a given
     * string. Example Input: {"cat", "house", "dog"}, ", " Output - "cat,
     * house, dog"
     *
     * @param strings The string array too join.
     * @param joinStr The string to join the array members together.
     *
     * @pre strings != null
     *
     * @return Joined String
     */
    public static String join(final String[] strings, final String joinStr) {
        final StringBuffer result = new StringBuffer();
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
     * Example: input: key="age", sep=',', plist="cost=23,age=27,name=Thom"
     * output = "27". This is a simple implementation that is meant for
     * controlled use in which the key and values are known to be safe.
     * Specifically, the equals character must be used to indicate parameter
     * assignments. There is no escape character. Thus the parameter names and
     * values cannot contain the equals character or the separator character.
     *
     * @param key the key indicating which parameter value to extract.
     * @param plist packed list of key=value assignments. The character '=' must
     * be used to indicate the assignment.
     * @param sep separator character.
     * @return the value corresponding to the key, or null if the key is not
     * present. If the key appears in the list more than once, the first value
     * is returned.
     */
    public static String getParameter(final String key, final String plist, final char sep) {
        int keyEnd;
        int keyStart = 0;
        String foundValue;
        while (keyStart < plist.length()) {
            keyStart = plist.indexOf(key, keyStart);
            if (keyStart == -1) {
                return null;   // Did not find key
            }
            keyEnd = keyStart + key.length();
            if (plist.charAt(keyEnd) == '='
                    && (keyStart == 0 || plist.charAt(keyStart - 1) == sep)) {
                // Found isolated parameter value, this is the match
                final int valueEnd = plist.indexOf(sep, keyEnd);
                if (valueEnd == -1) {
                    // did not find another separator, return value
                    foundValue = plist.substring(keyEnd + 1);
                } else {
                    // found another separator, return value
                    foundValue = plist.substring(keyEnd + 1, valueEnd);
                }
                return foundValue;
            } else {
                keyStart++;   // did not find.  Advance past current position
            }
        }
        return null;
    }

    /**
     * Strip extra white space from a string. This replaces any white space
     * character or consecutive white space characters with a single space. It
     * is useful when comparing strings that should be equal except for possible
     * differences in white space. Example: input = "I \ndo\tsee". Output = "I
     * do see".
     *
     * @param str string that may contain extra white space
     * @return string the same as the input, but with extra white space removed
     * and replaced by a single space.
     */
    static public String stripWhiteSpace(final String str) {
        final StringBuffer stringbuf = new StringBuffer();
        boolean inSpace = true;
        boolean isSpace;
        char chr;
        for (int i = 0; i < str.length(); i++) {
            chr = str.charAt(i);
            isSpace = Character.isWhitespace(chr);
            if (isSpace) {
                if (!inSpace) {
                    stringbuf.append(' ');
                    inSpace = true;
                }
            } else {
                stringbuf.append(chr);
                inSpace = false;
            }
        }
        return stringbuf.toString().trim();
    }

    /**
     * Get a String representation for an Object. If it has an asString method,
     * use that; otherwise fall back on toString
     */
    public static String toString(final Object obj) {
        try {
            return (String) obj.getClass().getMethod("asString", null)
                    .invoke(obj, new Object[0]);
        } catch (NoSuchMethodException e) {
            return obj.toString();
            
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UncheckedWrapperException("Invoking asString() on an " + obj.getClass(), e);
        }
    }

    /**
     * create a String representation of a map. This method is not too
     * necessary, because Map.toString() does almost the same.
     */
    public static String toString(final Map map) {
        final StringBuffer tostr = new StringBuffer();
        if (map == null) {
            tostr.append("null");
        } else {
            tostr.append(map.getClass().getName());
            final Set entrySet = map.entrySet();
            if (entrySet == null) {
                tostr.append("[null entrySet]");
            } else {
                final Iterator entries = entrySet.iterator();
                if (entries == null) {
                    tostr.append("[null iterator]");
                } else {
                    tostr.append('{');
                    String comma = NEW_LINE;

                    while (entries.hasNext()) {
                        tostr.append(comma);
                        comma = "," + NEW_LINE;
                        final Map.Entry entry = (Map.Entry) entries.next();

                        tostr.append(toString(entry.getKey()))
                                .append(" => ")
                                .append(toString(entry.getValue()));
                    }
                    tostr.append(NEW_LINE).append('}');
                }
            }
        }
        return tostr.toString();
    }

    /**
     * Strips all new-line characters from the input string.
     *
     * @param str a string to strip
     * @return the input string with all new-line characters removed.
     * @post result.indexOf('\r') == 0
     * @post result.indexOf('\n') == 0
     */
    public static String stripNewLines(final String str) {

        return str.replaceAll("[\\n\\r]", "");
    }

    /**
     * <p>
     * Add a possible newline for proper wrapping.</p>
     *
     * <p>
     * Checks the given String to see if it ends with whitspace. If so, it
     * assumes this whitespace is intentional formatting and returns a reference
     * to the original string. If not, a new <code>String</code> object is
     * created containing the original plus a platform-dependent newline
     * character obtained from {@link System#getProperty(String)
     * System.getProperty("line.separator")}.</p>
     */
    public static String addNewline(final String str) {
        final int length = str.length() - 1;
        if (length == -1) {
            return str;
        } else if (Character.isWhitespace(str.charAt(length))) {
            return str;
        } else {
            return str.concat(NEW_LINE);
        }
    }

    /**
     * This takes the passed in string and truncates it. It cuts the string off
     * at the length specified and then goes back to the most recent space and
     * truncates any word that may have been cut off. It also takes the string
     * and converts it to plain text so that no HTML will be shown.
     */
    public static String truncateString(final String str, final int length) {
        return truncateString(str, length, true);
    }

    /**
     * This takes the passed in string and truncates it. It cuts the string off
     * at the length specified and then goes back to the most recent space and
     * truncates any word that may have been cut off. The htmlToText dictates
     * whehter or not the string should be converted from HTML to text before
     * being truncated
     *
     * @param str The string to be truncated
     * @param length The length which to truncate the string
     * @param removeHTML Whether or not to convert the HTML to text
     * @return the truncated String
     */
    public static String truncateString(final String str, final int length,
            final boolean removeHTML) {
        if (str == null) {
            return "";
        }

        String string = str;
        if (removeHTML) {
            string = htmlToText(string);
        }

        if (string.length() <= length) {
            return string;
        }

        if (string.lastIndexOf(' ', length) == -1) {
            //no whitespace found, so truncate at the specified length even if
            // it is in the middle of a word
            return string.substring(0, length);
        }

        return string.substring(0, string.lastIndexOf(' ', length)).trim();
    }

    /**
     * "join" a List of Strings into a single string, with each string separated
     * by a defined separator string.
     *
     * @param elements the strings to join together
     * @param sep the separator string
     * @return the strings joined together
     */
    public static String join(final List elements, final String sep) {
        final StringBuffer stringbuf = new StringBuffer();
        boolean first = true;
        final Iterator iter = elements.iterator();

        while (iter.hasNext()) {
            final String element = (String) iter.next();

            if (first) {
                first = false;
            } else {
                stringbuf.append(sep);
            }

            stringbuf.append(element);
        }

        return stringbuf.toString();
    }

    /**
     * Removes whitespace from the beginning of a string. If the string consists
     * of nothing but whitespace characters, an empty string is returned.
     *
     * @param str the String
     * @return the String without whitespaces
     */
    public static String trimleft(final String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(i);
            }
        }
        return "";
    }

    /**
     * Returns a String containing the specified repeat count of a given pattern
     * String.
     *
     * @param pattern the pattern String
     * @param repeatCount the number of time to repeat it
     * @return
     */
    public static String repeat(final String pattern, final int repeatCount) {
        final StringBuffer strb = new StringBuffer(repeatCount * pattern.length());
        for (int i = 0; i < repeatCount; i++) {
            strb.append(pattern);
        }
        return strb.toString();
    }

    /**
     * Returns a String containing the specified repeat count of a given pattern
     * character.
     *
     * @param pattern the pattern character
     * @param repeatCount the number of time to repeat it
     */
    public static String repeat(final char pattern, final int repeatCount) {
        return repeat(String.valueOf(pattern), repeatCount);
    }

    /**
     * Wrap a string to be no wider than 80 characters. This is just a
     * convenience method for calling the more general method with a default
     * string width.
     *
     * @param input the String to wrap
     *
     * @since 5.1.2
     */
    public static String wrap(final String input) {
        return wrap(input, 80);
    }

    /**
     * Wrap a string to be no wider than a specified number of characters by
     * inserting line breaks. If the input is null or the empty string, a string
     * consisting of only the newline character will be returned. Otherwise the
     * input string will be wrapped to the specified line length. In all cases
     * the last character of the return value will be a single newline.
     *
     * <p>
     * Notes:
     *
     * <ol>
     * <li>line breaks in the input string are preserved
     * <li>wrapping is "soft" in that lines in the output string may be longer
     * than maxLength if they consist of contiguous non-whitespace characters.
     * </ol>
     *
     * @param input the String to wrap
     * @param maxLength the maximum number of characters between line breaks
     *
     * @since 5.1.2
     */
    public static String wrap(final String input, final int maxLength) {

        final char ENDL = '\n';
        String formattedInput;

        // Make sure that we start with a string terminated by a
        // newline character.  Some of the index calculations below
        // depend on this.
        if (emptyString(input)) {
            return String.valueOf(ENDL);
        } else {
            formattedInput = input.trim() + ENDL;
        }

        final StringBuffer output = new StringBuffer();

        int startOfLine = 0;

        while (startOfLine < formattedInput.length()) {

            final String line = formattedInput.substring(startOfLine, Math.min(formattedInput.length(),
                    startOfLine + maxLength));

            if ("".equals(line)) {
                break;
            }

            final int firstNewLine = line.indexOf(ENDL);
            if (firstNewLine != -1) {

                // there is a newline
                output.append(formattedInput.substring(startOfLine,
                        startOfLine + firstNewLine));
                output.append(ENDL);
                startOfLine += firstNewLine + 1;
                continue;
            }

            if (startOfLine + maxLength > formattedInput.length()) {

                // we're on the last line and it is < maxLength so
                // just return it
                output.append(line);
                break;
            }

            final char SPACE = ' ';
            int lastSpace = line.lastIndexOf(SPACE);
            if (lastSpace == -1) {

                // no space found!  Try the first space in the whole
                // rest of the string
                final int nextSpace = formattedInput.indexOf(SPACE, startOfLine);
                final int nextNewLine = formattedInput.indexOf(ENDL, startOfLine);

                if (nextSpace == -1) {
                    lastSpace = nextNewLine;
                } else {
                    lastSpace = Math.min(nextSpace, nextNewLine);
                }

                if (lastSpace == -1) {
                    // didn't find any more whitespace, append the
                    // whole thing as a line
                    output.append(formattedInput.substring(startOfLine));
                    break;
                }

                // code below will add this to the start of the line
                lastSpace -= startOfLine;
            }

            // append up to the last space
            output.append(formattedInput.substring(startOfLine,
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
    public static boolean isAlphaNumeric(final String value) {
        return !value.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    /**
     * This method performs interpolation on multiple variables. The keys in the
     * hash table correspond directly to the placeholders in the string. The
     * values in the hash table can either be plain strings, or an instance of
     * the PlaceholderValueGenerator interface
     *
     * Variable placeholders are indicated in text by surrounding a key word
     * with a pair of colons. The keys in the hash table correspond to the names
     *
     * eg. "::forename:: has the email address ::email::"
     *
     * @see java.text.MessageFormat
     *
     * @param text the text to interpolate
     * @param vars a hash table containing key -> value mappings ////
     */
    public static String interpolate(final String text, final Map vars) {
        //old perl regex= "(::(?:\\w+(?:[.-]+\\w+)*)::)"
        String str = text;
        final Pattern pattern = Pattern.compile("::([\\w\\.\\-]+)::");
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            final String key = matcher.group(1);
            final String value = (String) vars.get(key);
            if (value != null) {
                final String regex = "::" + key + "::";
                str = str.replaceAll(regex, value);
            }
        }

        return str;
    }

    /**
     * THis method performs a single variable substitution on a string. The
     * placeholder takes the form of ::key:: within the sample text.
     *
     * @see java.text.MessageFormat
     *
     * @param text the text to process for substitutions
     * @param key the name of the placeholder
     * @param value the value to insert upon encountering a placeholder
     */
    public static String interpolate(final String text, final String key, final String value) {
        final String regex = "::" + key + "::";

        return text.replaceAll(regex, value);
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
        if (str == null) {
            return null;
        }
        return str.replace(find, replace);
    }

    /**
     * @throws IllegalArgumentException if <code>throwable</code> is null
     */
    public static String getStackTrace(final Throwable throwable) {
        if (throwable == null) {
            throw new IllegalArgumentException();
        }

        final StringWriter strw = new StringWriter();
        final PrintWriter prntw = new PrintWriter(strw);
        throwable.printStackTrace(prntw);
        prntw.close();
        return strw.toString();
    }

    /**
     * Returns a list of lines where each line represents one level in the stack
     * trace captured by <code>throwable</code>.
     *
     * <p>
     * For a stack trace like this:</p>
     *
     * <pre>
     * java.lang.Throwable
     *         at Main.level3(Main.java:19)
     *         at Main.level2(Main.java:15)
     *         at Main.level1(Main.java:11)
     *         at Main.main(Main.java:7)
     * </pre>
     *
     * <p>
     * the returned list looks like this: </p>
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
    public static List getStackList(final Throwable throwable) {
        final StringTokenizer tkn = new StringTokenizer(getStackTrace(throwable), System.getProperty("line.separator"));
        final List list = new LinkedList();
        while (tkn.hasMoreTokens()) {
            final String token = tkn.nextToken().trim();
            if ("".equals(token)) {
                continue;
            }
            if (token.startsWith("at ")) {
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
     * "<code>business-promotions</code>".
     *
     * @param name the to be converted into a URL.
     * @return the converted name, possibly unchanged and null if the input is
     * null.
     */
    public static String urlize(final String name) {
        if (name == null) {
            return null;
        }
        final StringBuffer urlizedName = new StringBuffer(name.length());

        for (int i = 0; i < name.length(); i++) {
            final char chr = name.charAt(i);

            if (Character.isLetter(chr)) {
                urlizedName.append(Character.toLowerCase(chr));
            } else if (Character.isDigit(chr) || chr == '_' || chr == '-') {
                urlizedName.append(chr);
            } else if (chr == ' ' || chr == '&' || chr == '/') {
                urlizedName.append('-');
            }
        }
        return urlizedName.toString();
    }
}
