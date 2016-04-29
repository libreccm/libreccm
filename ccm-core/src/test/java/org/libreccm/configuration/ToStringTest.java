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
package org.libreccm.configuration;

import org.libreccm.configuration.StringSetting;
import org.libreccm.configuration.LocalizedStringSetting;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.EnumSetting;
import org.libreccm.configuration.DoubleSetting;
import org.libreccm.configuration.BigDecimalSetting;
import org.libreccm.configuration.LongSetting;
import org.libreccm.configuration.SettingInfo;
import org.libreccm.configuration.BooleanSetting;
import java.util.Arrays;
import java.util.Collection;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.ToStringVerifier;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class ToStringTest extends ToStringVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            BigDecimalSetting.class,
            BooleanSetting.class,
            ConfigurationInfo.class,
            DoubleSetting.class,
            EnumSetting.class,
            LocalizedStringSetting.class,
            LongSetting.class,
            SettingInfo.class,
            StringSetting.class
        });
    }

    public ToStringTest(final Class<?> entityClass) {
        super(entityClass);
    }

}
