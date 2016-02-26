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
package com.arsdigita.bebop;

import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.util.Assert;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

/**
 * A text label displayed to the user for information about and identification
 * of certain parts of the screen. Therefore the label has to use a 
 * GlobalizedMessage for the information presented. 
 * 
 * A Label is meant to provide semantically relevant information and may not be
 * used for fixed arbitrary Text. Use Embedded instead. 
 * 
 * (Previous usage: can be used to generate either some static, fixed
 * text or a new text string for every request.)
 * 
 * To modify the information with an already locked label use the {@link
 * #setLabel(String,PageState)} method which can adjust for each request.
 *
 * @author David Lutterkort
 * @version $Id: Label.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Label extends DescriptiveComponent implements Cloneable {

    public static final String BOLD = "b";
    public static final String ITALIC = "i";

    // the default label
    private GlobalizedMessage m_label;
    // a requestlocal set of labels (to avoid printlisteners)
    private final RequestLocal m_requestLabel = new RequestLocal();
    private String m_fontWeight;

    /** The setting for output escaping affects how markup in the 
     *  <code>content</code> is handled. 
     *  <UL><LI>If output escaping is in effect (true), &lt;b>example&lt;/b>
     *  will appear literally.</LI> 
     *  <LI>If output escaping is disabled, &lt;b>example&lt;/b> appears as the 
     *  String "example" in bold (i.e. retaining the markup.</LI></UL>
     *  Default is false.                                                     */ 
    private boolean m_escaping = false;  // default for a primitive anyway
    private PrintListener m_printListener;

    /**
     * Constructor creates a new <code>Label</code> with empty text.
     */
    public Label() {
        // A kind of fallback (or a hack) here. Parameter label is taken as 
        // a key for some (unknown) Resource bundle. Because GlobalizedMessage
        // will not find a corrresponding message it will display the key
        // itself, 'faking' a globalized message.
        m_label = new GlobalizedMessage(" ");
    }

    /**
     * Creates a new <code>Label</code> with the specified (fixed) text.
     *
     * @param label the text to display
     * @deprecated  refactor to use Label(GlobalizedMessage label) instead
     */
    public Label(String label) {
        this(label, true);
    }

    /**
     * Creates a new <code>Label</code> with the specified text and 
     * output escaping turned on if <code>escaping</code> is <code>true</code>. 
     * 
     * The setting for output escaping affects how markup in the
     * <code>label</code> is handled. For example: 
     * <UL><LI>If output escaping is in effect, &lt;b>text&lt;/b> will appear 
     * literally.</LI> 
     * <LI>If output escaping is disabled, &lt;b>text&lt;/b> appears as the 
     * word "text" in bold.</LI></UL>
     *
     * @param label the text to display
     * @param escaping <code>true</code> if output escaping will be in effect;
     * <code>false</code> if output escaping will be disabled
     * 
     * @deprecated  refactor to Label(GlobalizedMessage label, boolean escaping) 
     *              instead
     */
    public Label(String label, boolean escaping) {
        setLabel(label);
        setOutputEscaping(escaping);
    }

    /**
     * <p> Creates a new label with the specified text. </p>
     *
     * @param label the text to display
     */
    public Label(GlobalizedMessage label) {
        this(label, true);
    }

    /**
     * Creates a new label with the specified text as GlobalizedMessage 
     * and fontweight.
     *
     * @param label The text to display as GlobalizedMessage
     * @param fontWeight The fontWeight e.g., Label.BOLD. Whether it has any
     *                   effect depends on the theme! Take it just as a hint.
     */
    public Label(GlobalizedMessage label, String fontWeight) {
        this(label, true);
        m_fontWeight = fontWeight;
    }

    /**
     * <p> Creates a new label with the specified text as GlobalizedMessage 
     * and output escaping turned on if <code>escaping</code> is
     * <code>true</code>. </p>
     *
     * @param label the text to display as GlobalizedMessage
     * @param escaping Whether or not to perform output escaping
     */
    public Label(GlobalizedMessage label, boolean escaping) {
        setLabel(label);
        setOutputEscaping(escaping);
    }

    /**
     * Creates a new <code>Label</code> that uses the print listener to 
     * generate output.
     *
     * @param l the print listener used to produce output
     */
    public Label(PrintListener l) {
        this();
        addPrintListener(l);
    }

    /**
     * Creates a new label with the specified text and fontweight.
     *
     * @param label The text to display
     * @param fontWeight The fontWeight e.g., Label.BOLD
     * 
     * @deprecated  without direct replacement. Refactor to use
     *              Label(GlobalizedMEssage) instead and modify the theme to
     *              use proper text marking. (Or use setFontWeight separately.
     */
    public Label(String label, String fontWeight) {
        this(label, true);
        m_fontWeight = fontWeight;
    }

    /**
     * Provides the Label as Text, localized for the current request.
     * 
     * Although it is not recommended, this method may be overridden to
     * dynamically generate the text of the label. Overriding code may need 
     * the page state. 
     * <p>
     * If possible, derived classes should override {@link #getLabel()} instead,
     * which is called from this method. As long as we don't have a static 
     * method to obtain ApplicationContext, this is a way to get the 
     * RequestContext (to determine the locale). When ApplicationContext gets
     * available, that will become the suggested way for overriding code to get
     * context.
     *
     * @param state the current page state
     * @return the string produced for this label
     */
    public String getLabel(PageState state) {
        return (String) getGlobalizedMessage(state).localize(state.getRequest());
    }

 // /**
 //  * .
 //  *
 //  * This method may be overridden to dynamically generate the default text of
 //  * the label.
 //  *
 //  * @return the string produced for this label.
 //  *
 //  * @deprecated Use {@link #getGlobalizedMessage()}
 //  */
 // Conflicts with Super's getLabel message of type GlobalizedMessage. But isn't
 // needed anyway. Should deleted as soon as the refactoring of Label is
 // completed (i.e. any string Label ironed out).
 // public String getLabel() {
 //     return getGlobalizedMessage().getKey();
 // }

    /**
     * <p> This should really be getLabel(), but since it was marked STABLE I
     * can't change its return type. </p>
     *
     * @return the default label to display.
     */
    public GlobalizedMessage getGlobalizedMessage() {
        return getGlobalizedMessage(null);
    }

    /**
     * <p> This should really be getLabel(), but since it was marked STABLE I
     * can't change its return type. </p>
     *
     * @param state the current PageState
     * @return the label to display for this request, or if state is null, the
     * default label
     */
    public GlobalizedMessage getGlobalizedMessage(PageState state) {
        if (state != null) {
            GlobalizedMessage dynlabel =
                    (GlobalizedMessage) m_requestLabel.get(state);
            if (dynlabel != null) {
                return dynlabel;
            }
        }
        return m_label;
    }

    /**
     * Sets new default text for this Label.
     *
     * @param label The new label text; will be used as a key into the current
     *              ResourceBundle if possible, or displayed literally.
     * @deprecated refactor to use
     *             @see setLabel(GlobalizedMessage) instead!
     */
    public void setLabel(String label) {
        setLabel(label, null);
    }

    /**
     * Sets new request-specific text for this Label to use on this request. If
     * state is null, then sets the default text instead.
     *
     * @param label The new label text; will be used as a key into the current
     * ResourceBundle if possible, or displayed literally.
     * @param state the page state
     * @pre state == null implies !isLocked()
     * @deprecated refactor to use
     *             @see setLabel(GlobalizedMessage, PageState) instead!
     */
    public void setLabel(String label, PageState state) {
        if (label == null || label.length() == 0) {
            label = " ";
        }
        // A kind of fallback (or a hack) here. Parameter label is taken as 
        // a key for some (unknown) Resource bundle. Because GlobalizedMessage
        // will not find a corrresponding message it will display the key
        // itself, 'faking' a globalized message.
        setLabel(new GlobalizedMessage(label), state);
    }

    /**
     * Sets the text for this label using a GlobalizedMessage.
     *
     * @param label The GlobalizedMessage containing the label text or the
     * lookup key to use in the ResourceBundle
     * @param state the current page state; if null, sets the default text for
     * all requests.
     * @pre state == null implies !isLocked()
     */
    public void setLabel(GlobalizedMessage label, PageState state) {
        if (state == null) {
            Assert.isUnlocked(this);
            m_label = label;
        } else {
            m_requestLabel.set(state, label);
        }
    }

    /**
     * Sets the default text for this Label.
     * 
     * Overwrites parent's method an therefore prevents the usage of parent's
     * label methods (which are attributes, but here it is the content).
     *
     * @param label The GlobalizedMessage containing the label text or the
     * lookup key to use in the ResourceBundle
     */
    @Override
    public void setLabel(GlobalizedMessage label) {
        setLabel(label, null);
    }

    public final boolean getOutputEscaping() {
        return m_escaping;
    }

    /**
     * Controls whether output is escaped during transformation, by default
     * true. If true, it will be printed literally, and the user will see
     * &lt;b&gt;. When false, the browser will interpret as a bold tag.
     * 
     * @param escaping
     */
    public final void setOutputEscaping(boolean escaping) {
        m_escaping = escaping;
    }

    public final String getFontWeight() {
        return m_fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        Assert.isUnlocked(this);
        m_fontWeight = fontWeight;
    }

    /**
     * Adds a print listener. Only one print listener can be set for a label,
     * since the <code>PrintListener</code> is expected to modify the target 
     * of the <code>PrintEvent</code>.
     *
     * @param listener the print listener
     * @throws IllegalArgumentException if <code>listener</code> is null.
     * @throws IllegalStateException if a print listener has previously been
     *         added.
     * @pre listener != null
     */
    public void addPrintListener(PrintListener listener)
            throws IllegalStateException, IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Argument listener can not be null");
        }
        if (m_printListener != null) {
            throw new IllegalStateException("Too many listeners. Can only have one");
        }
        m_printListener = listener;
    }

    /**
     * Removes a previously added print listener. If <code>listener</code> is 
     * not the listener that was added with {@link #addPrintListener
     * addPrintListener}, an IllegalArgumentException will be thrown.
     *
     * @param listener the listener that was added with
     * <code>addPrintListener</code>
     * @throws IllegalArgumentException if <code>listener</code> is not the
     * currently registered print listener or is <code>null</code>.
     * @pre listener != null
     */
    public void removePrintListener(PrintListener listener)
            throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if (listener != m_printListener) {
            throw new IllegalArgumentException("listener is not registered with this widget");
        }
        m_printListener = null;
    }

    /**
     * Generates the (J)DOM fragment for a label.
     * <p><pre>
     * &lt;bebop:link href="..." type="..." %bebopAttr;/>
     * </pre>
     * 
     * @param state The current {@link PageState}.
     * @param parent The XML element to attach the XML to.
     */
    @Override
    public void generateXML(PageState state, Element parent) {

        if (!isVisible(state)) {
            return;
        }

        Label target = firePrintEvent(state);

        Element label = parent.newChildElement("bebop:label", BEBOP_XML_NS);

        target.exportAttributes(label);
        target.generateDescriptionXML(state, label);

        String weight = target.getFontWeight();
        if (weight != null && weight.length() > 0) {
            label.addAttribute("weight", weight);
        }

        if (!target.m_escaping) {
            label.addAttribute("escape", "yes");
        } else {
            label.addAttribute("escape", "no");
        }

        String key = getGlobalizedMessage()
                     .getKey()
                     .substring(getGlobalizedMessage()
                                .getKey().lastIndexOf(".") + 1);
        
        // This if clause is needed to prevent printing of keys if the 
        // GlobalizedMessage was created from a String by this class
        if(!key.equals(target.getLabel(state))) {
            label.addAttribute("key", key);
        }
        
        /*
         * This may break with normal JDOM.  We may need to have a node for
         * the case where there is no weight.  The problem comes in that
         * setText *may* kill the other content in the node.  It will kill
         * the other text, so it may be a good idea anyways.
         */
        label.setText(target.getLabel(state));
    }

    /**
     * 
     * @param state
     * @return 
     */
    protected Label firePrintEvent(PageState state) {
        Label l = this;

        if (m_printListener != null) {
            try {
                l = (Label) this.clone();
                m_printListener.prepare(new PrintEvent(this, state, l));
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(
                        "Couldn't clone Label for PrintListener. "
                        + "This probably indicates a serious programming error: "
                        + e.getMessage());
            }
        }

        return l;
    }
}
