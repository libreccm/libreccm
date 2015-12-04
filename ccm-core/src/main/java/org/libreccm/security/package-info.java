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
/**
 * This package contains all classes dealing with authentication and
 * authorisation in LibreCCM.
 *
 * Most of this classes are only relevant for the developers of the core part
 * of LibreCCM and and core administration UI. For developers of modules the
 * primary interface is the Apache Shiro Library. Module developers usually have
 * the use these classes only in the <code>CcmModule#install(InstallEvent</code>
 * method to create roles and privileges for their module. Therefore most
 * methods of these classes can only be invoked by the System user.
 *
 * The check if the current user is logged in and/or has a certain permission
 * you have to obtain the current <code>Subject</code> from Shiro. In
 * LibreCCM the subject is provided using CDI. In classes eligible for injection
 * you simply inject the current subject. In other classes you can use the
 * <a href="../cdi/utils/CdiUtil.html">CdiUtil</a> class.
 *
 * Another option for method of CDI beans is to use the interceptors provided by
 * this package.
 *
 * @see CcmModule
 */
package org.libreccm.security;
