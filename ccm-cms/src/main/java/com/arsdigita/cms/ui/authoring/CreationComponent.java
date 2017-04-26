/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormProcessListener;

/**
 * Interface which item creation components should implement. It's
 * currently an optional interface, but user defined content types
 * will not work unless they inherit from a type whose creation
 * component implements this. This interface currently only defines
 * methods which are essential to the operation of UDCT creation
 * components.
 *
 * @author Scott Seago (sseago@redhat.com)
 */
public interface CreationComponent extends Container, FormProcessListener {

    /**
     * Instantiate and add the save/cancel section for this CreationComponent.
     */
    void addSaveCancelSection();

    /**
     * Return the save/cancel section for this CreationComponent.
     *
     * @return the save/cancel section for this CreationComponent.
     */
    SaveCancelSection getSaveCancelSection();

    /**
     * Removes the specified process listener from the
     * list of process listeners (if it had previously been added).
     *
     * @param listener the process listener to remove
     */
    void removeProcessListener(FormProcessListener listener);

    /**
     * Return the ApplyWorkflowFormSection associated with this CreationComponent.
     *
     * @return the ApplyWorkflowFormSection associated with this CreationComponent.
     */
    ApplyWorkflowFormSection getWorkflowSection();

}
