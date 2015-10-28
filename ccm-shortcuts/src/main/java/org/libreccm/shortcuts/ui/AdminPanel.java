/*
 * Copyright (C) 2015 LibreCCM Foundation.
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

package org.libreccm.shortcuts.ui;


import org.apache.log4j.Category;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.LongParameter;

public class AdminPanel extends SimpleContainer {

	final private ParameterSingleSelectionModel m_shortcut = new ParameterSingleSelectionModel(new LongParameter("ShortcutID"));

	private static final Category log = Category.getInstance(AdminPanel.class
			.getName());

	public AdminPanel() {
		add(new ShortcutForm(m_shortcut));
		add(new ShortcutsTable(m_shortcut));
	}

    @Override
	public void register(Page p) {
		super.register(p);

		p.addGlobalStateParam(m_shortcut.getStateParameter());
	}

}

