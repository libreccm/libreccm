/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.core;

import java.util.Arrays;
import java.util.Collection;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.tests.categories.UnitTest;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class CategorizationEntitiesTest extends EntitiesTestCore {

    @Parameterized.Parameters
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Categorization.class,
            org.libreccm.categorization.Category.class,
            Domain.class,
            DomainOwnership.class});
    }

    public CategorizationEntitiesTest(final Class<?> entitiesClass) {
        super(entitiesClass);
    }

}
