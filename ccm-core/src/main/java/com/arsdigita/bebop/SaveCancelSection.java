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

import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.util.GlobalizationUtil;

/**
 * A form section with two buttons (Save and Cancel) aligned to
 * the right.
 *
 * @author Stanislav Freidin
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id: SaveCancelSection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SaveCancelSection extends FormSection {

    private Submit m_saveWidget, m_cancelWidget;

    /**
     * Constructs a new SaveCancelSection.
     */
    public SaveCancelSection() {
        super(new BoxPanel(BoxPanel.HORIZONTAL, false));

        BoxPanel panel = (BoxPanel)getPanel();
        panel.setWidth("2%");
        
        createWidgets();

        add(m_saveWidget, BoxPanel.RIGHT);
        add(m_cancelWidget, BoxPanel.RIGHT);
    }

    public SaveCancelSection(Container c) {
        super(c);
        
        createWidgets();
        add(m_saveWidget);
        add(m_cancelWidget);
    }

    private void createWidgets() {
        m_saveWidget = new Submit("save");
        m_saveWidget.setButtonLabel(GlobalizationUtil.globalize("bebop.save"));

        m_cancelWidget = new Submit("cancel");
        m_cancelWidget.setButtonLabel(GlobalizationUtil.globalize("bebop.cancel"));
    }

    /**
     * Gets the Save button.
     * @return the Save button.
     */
    public Submit getSaveButton() {
        return m_saveWidget;
    }

    /**
     * Gets the Cancel button.
     * @return the Cancel button.
     */
    public Submit getCancelButton() {
        return m_cancelWidget;
    }
}
