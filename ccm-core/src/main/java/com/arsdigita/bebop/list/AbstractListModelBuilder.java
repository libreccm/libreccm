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
package com.arsdigita.bebop.list;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.util.LockableImpl;

/**
 * An abstract class that implements ListModelBuilder by concretely
 * implementing the Lockable interface and leaving only the makeModel method
 * abstract. This allows for anonymous inner classes that implement the
 * ListModelBuilder interface without having to implement Lockable as well.
 *
 * @author Archit Shah 
 **/
public abstract class AbstractListModelBuilder
    extends LockableImpl implements ListModelBuilder {

    public abstract ListModel makeModel(List l, PageState state);
}
