/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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
 */
package com.arsdigita.cms.ui.cse;

import com.arsdigita.bebop.SimpleContainer;

/**
 * A pane that contains details to soon to be expired content.
 *
 * @author Unknown
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSoonExpiredPane extends SimpleContainer {

    public ContentSoonExpiredPane() {
        super();
        super.add(new ContentSoonExpiredTable());
    }

}
