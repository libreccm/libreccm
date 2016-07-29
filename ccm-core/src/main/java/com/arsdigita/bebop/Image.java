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

import com.arsdigita.xml.Element;

import com.arsdigita.util.Assert;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;

/**
 * A simple wrapper class for images.
 *
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 *
 * @version $Id$
 */

public class Image extends DescriptiveComponent  {

    private final String IMAGE_URL = "src";
    private final String ALT       = "alt";
    private final String HEIGHT    = "height";
    private final String WIDTH     = "width";
    private final String BORDER    = "border";

    private PrintListener m_printListener;

    public Image(String imageURL, String alt) {
        super();
        setImageURL(imageURL);
        setAlt(alt);
    }

    public Image(String imageURL) {
        this(imageURL, "");
    }

    /**
     * Creates a new <code>Image</code> that uses the print listener
     * to generate output.
     *
     * @param l the print listener used to produce output
     */
    public Image(PrintListener l) {
        this("");
        addPrintListener(l);
    }

    public void setImageURL(String imageURL) {
        Assert.isUnlocked(this);
        setAttribute(IMAGE_URL, imageURL);
    }

    public void setAlt(String alt) {
        Assert.isUnlocked(this);
        setAttribute(ALT, alt);
    }
    /**
     * 
     *
     * @param height
     */
    public void setHeight(String height) {
        Assert.isUnlocked(this);
        setAttribute(HEIGHT, height);
    }

    /**
     * 
     *
     * @param width
     */
    public void setWidth(String width) {
        Assert.isUnlocked(this);
        setAttribute(WIDTH, width);
    }

    /**
     * 
     *
     * @param border
     */
    public void setBorder(String border) {
        Assert.isUnlocked(this);
        setAttribute(BORDER, border);
    }

    /**
     * Adds a print listener. Only one print listener can be set for an
     * image, since the <code>PrintListener</code> is expected to modify the
     * target of the <code>PrintEvent</code>.
     * @param listener the print listener
     * @throws IllegalArgumentException if <code>listener</code> is null.
     * @throws IllegalStateException if a print listener has previously been
     *         added.
     * @pre listener != null */
    public void addPrintListener(PrintListener listener)
        throws IllegalStateException, IllegalArgumentException
    {
        if ( listener == null ) {
            throw new IllegalArgumentException
                ("Argument listener can not be null");
        }
        if ( m_printListener != null ) {
            throw new IllegalStateException
                ("Too many listeners. Can only have one");
        }
        m_printListener = listener;
    }

    /**
     * Removes a previously added print listener. If <code>listener</code> is
     * not the listener that was added with {@link #addPrintListener
     * addPrintListener}, an IllegalArgumentException will be thrown.
     * @param listener the listener that was previously added with
     *      <code>addPrintListener</code>
     * @throws IllegalArgumentException if <code>listener</code> is not the
     *      currently registered print listener or is <code>null</code>.
     * @pre listener != null
     */
    public void removePrintListener(PrintListener listener)
        throws IllegalArgumentException
    {
        if ( listener == null ) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if ( listener != m_printListener ) {
            throw new IllegalArgumentException
                ("listener is not registered with this widget");
        }
        m_printListener = null;
    }

    /**
     * Writes the output to a DOM to be used with the XSLT template
     * to produce the appropriate output.
     *
     * <p>Generates DOM fragment:
     * <p><code><pre>
     * &lt;bebop:image [src=...] [alt=...] [height=...]
     *       [width=...] [border=...]/>
     * </pre></code>
     *
     * @param parent the XML element to which the form adds its XML representation
     * */
    @Override
    public void generateXML(PageState state, Element parent) {

        if ( ! isVisible(state) ) {
            return;
        }

        Image target = firePrintEvent(state);
        Element image = parent.newChildElement ("bebop:image", BEBOP_XML_NS);
        target.exportAttributes(image);
    }

    protected Image firePrintEvent(PageState state) {
        Image i = this;
        if ( m_printListener != null ) {
            try {
                i = (Image) this.clone();
                m_printListener.prepare(new PrintEvent(this, state, i));
            } catch ( CloneNotSupportedException e ) {
                // FIXME: Failing silently here isn't so great
                //   It probably indicates a serious programming error
                i = this;
            }
        }
        return i;
    }

}
