/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.bebop;

import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Text extends SimpleComponent {

    private String text;
    private boolean outputEscaped;
    private PrintListener printListener;

    public Text() {
        this("");
    }

    public Text(final String text) {
        this.text = text;
        outputEscaped = true;
    }

    public Text(final PrintListener printListener) {
        this();
        this.printListener = printListener;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public boolean isOutputEscaped() {
        return outputEscaped;
    }

    public void setOutputEscaped(final boolean outputEscaped) {
        this.outputEscaped = outputEscaped;
    }

    public void setPrintListener(final PrintListener printListener) {
        if (printListener == null) {
            throw new IllegalArgumentException("PrintListener can't be null");
        }

        this.printListener = printListener;
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {

        if (!isVisible(state)) {
            return;
        }

        final Text target = firePrintEvent(state);

        final Element textElem = parent.newChildElement("bebop:text",
                                                        BEBOP_XML_NS);

        target.exportAttributes(textElem);

        if (outputEscaped) {
            textElem.addAttribute("escape", "no");
        } else {
            textElem.addAttribute("escape", "yes");
        }

        textElem.setText(target.getText());
    }

    protected Text firePrintEvent(final PageState state) {
        final Text component;
        if (printListener == null) {
            component = this;
        } else {
            try {
                component = (Text) this.clone();
                printListener.prepare(new PrintEvent(this, state, component));
            } catch (CloneNotSupportedException ex) {
                throw new UncheckedWrapperException(
                    "Could not clone Text component for PrintListener. "
                        + "This propaby indicates a serious programming error.");
            }
        }

        return component;
    }

}
