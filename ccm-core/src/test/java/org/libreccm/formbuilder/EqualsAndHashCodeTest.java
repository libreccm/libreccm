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
package org.libreccm.formbuilder;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class EqualsAndHashCodeTest {

    private final Class<?> entityClass;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Component.class,
            DataDrivenSelect.class,
            FormSection.class,
            Listener.class,
            MetaObject.class,
            ObjectType.class,
            Option.class,
            PersistentDataQuery.class,
            ProcessListener.class,
            Widget.class,
            WidgetLabel.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Test
    public void verifyEqualsAndHashCode() {
        final Component component1 = new Component();
        component1.setAdminName("Component One");

        final Component component2 = new Component();
        component2.setAdminName("Component Two");

        final WidgetLabel widgetLabel1 = new WidgetLabel();
        widgetLabel1.setAdminName("WidgetLabel One");

        final WidgetLabel widgetLabel2 = new WidgetLabel();
        widgetLabel2.setAdminName("WidgetLabel Two");

        final Widget widget1 = new Widget();
        widget1.setAdminName("Widget 1");

        final Widget widget2 = new Widget();
        widget2.setAdminName("Widget 2");

        final FormSection formSection1 = new FormSection();
        formSection1.setAdminName("FormSection One");

        final FormSection formSection2 = new FormSection();
        formSection2.setAdminName("FormSection Two");

        EqualsVerifier
                .forClass(entityClass)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .withRedefinedSuperclass()
                .withPrefabValues(Component.class, component1, component2)
                .withPrefabValues(WidgetLabel.class, widgetLabel1, widgetLabel2)
                .withPrefabValues(Widget.class, widget1, widget2)
                .withPrefabValues(FormSection.class, formSection1, formSection2)
                .verify();
    }

}
