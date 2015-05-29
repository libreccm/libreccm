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
import org.libreccm.tests.categories.UnitTest;

/**
 * The tests in this class are used to verify the implementations of the
 * {@code equals}, {@code hashCode} and {@code toString} methods of the entities
 * in the {@code org.libreccm.core} package.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class CcmCoreEntitiesTest extends EntitiesTestCore {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            CcmObject.class,
            EmailAddress.class,
            GroupMembership.class,
            Party.class,
            Permission.class,
            PersonName.class,
            Privilege.class,
            Resource.class,
            Role.class,
            User.class,
            UserGroup.class});
    }

    public CcmCoreEntitiesTest(final Class<?> entityClass) {
        super(entityClass);
    }
}
