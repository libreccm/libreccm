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
package org.libreccm.formbuilder.actions;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.formbuilder.Component;
import org.libreccm.formbuilder.FormSection;
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
            ConfirmEmailListener.class,
            ConfirmRedirectListener.class,
            RemoteServerPostListener.class,
            SimpleEmailListener.class,
            TemplateEmailListener.class,
            XmlEmailListener.class
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
        
        final FormSection formSection1 = new FormSection();
        formSection1.setAdminName("FormSection One");
        
        final FormSection formSection2 = new FormSection();
        formSection2.setAdminName("FormSection Two");
        
        EqualsVerifier
            .forClass(entityClass)
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .withRedefinedSuperclass()
            .withPrefabValues(Component.class, component1, component2)
            .withPrefabValues(FormSection.class, formSection1, formSection2)
            .verify();
    }

}
