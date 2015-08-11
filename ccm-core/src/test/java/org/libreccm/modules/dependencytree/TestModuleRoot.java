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
package org.libreccm.modules.dependencytree;

import org.libreccm.modules.Module;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.libreccm.modules.annotations.Module(
    name = "org.libreccm.core.ccm-testmodule-a", version = "1.0.0")
public class TestModuleRoot implements Module {

    @Override
    public void prepare() {
        //Nothing
    }

    @Override
    public void uninstall() {
        //Nothing
    }

    @Override
    public void init() {
        //Nothing
    }

    @Override
    public void shutdown() {
        //Nothing
    }

}
