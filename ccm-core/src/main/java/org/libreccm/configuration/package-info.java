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
 * This package contains classes which provide a database driven configuration
 * system. Prior to version 7.0.0 the configuration was stored in properties
 * files and managed using some classes and XML files.
 *
 * The configuration classes are still used, but they do work differently.
 *
 * A configuration class is a simple Java bean with several fields which are
 * supported by the configuration system, getters and setters for easy
 * access and which is annotated with the
 * <a href="Configuration.html"><code>Configuration</code></a>
 * annotation. The settings fields must annotated with the
 * <a href="Setting.html"><code>Setting</code></a>
 * annotation.
 *
 * The supported value types are:
 * <ul>
 * <li><code>BigDecimal</code></li>
 * <li><code>Boolean</code></li>
 * <li><code>Double</code></li>
 * <li>List of Strings (<code>EnumSetting</code></li>
 * <li><code>LocalizedString</code></li>
 * <li><code>Long</code></li>
 * <li><code>String</code></li>
 * </ul>
 *
 * The
 * <a href="ConfigurationManager.html"><code>ConfigurationManager</code></a>
 * provides methods for loading and saving the configurations.
 *
 * For most use cases it should not be necessary to use the classes which
 * extends
 * <a href="AbstractSetting.html"><code>AbstractSetting</code></a>
 * outside of this package. But there are may use cases where this is necessary.
 * Therefore these classes are publicly visible.
 */
package org.libreccm.configuration;
