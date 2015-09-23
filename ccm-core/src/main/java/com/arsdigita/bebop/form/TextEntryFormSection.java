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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.util.MessageType;

/**
 * Form section that takes a text entry component (e.g., TextField)
 * and displays it along with a drop-down box that enables the user to
 * select the input type.
 *
 */
public class TextEntryFormSection extends FormSection
    implements MessageType {

    private Widget m_widget;
    private OptionGroup m_textType = null;

    public TextEntryFormSection(Widget w) {
        super(new GridPanel(2));
        m_widget = w;
        add(m_widget);

        if (w.getParameterModel().getValueClass().equals(String.class)) {
            m_textType = new SingleSelect(w.getName() + ".textType");
            m_textType.addOption(new Option(MessageType.TEXT_PLAIN, "Plain text"));
            m_textType.addOption(new Option(MessageType.TEXT_HTML, "HTML"));
            m_textType.addOption(new Option(MessageType.TEXT_PREFORMATTED, "Preformatted text"));
            add(m_textType);
        } else {
            m_textType = new SingleSelect(w.getName() + ".textType") {
                    public boolean isVisible(PageState ps) {
                        return false;
                    }};
            add(m_textType);
        }
    }

    public void setWidgetValue(PageState ps, Object value) {
        m_widget.setValue(ps, value);
    }

    public Object getWidgetValue(PageState ps) {
        return m_widget.getValue(ps);
    }

    public String getTextType(PageState ps) {
        // Null if it's a Deditor
        if ( m_textType == null ) {
            return MessageType.TEXT_HTML;
        } else {
            return (String) m_textType.getValue(ps);
        }
    }

    public void setTextType(PageState ps, String type) {
        m_textType.setValue(ps, type);
    }

}
