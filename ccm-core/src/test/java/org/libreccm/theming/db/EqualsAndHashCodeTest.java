/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.db;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.EqualsVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class EqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Directory.class,
            DataFile.class,
            Theme.class,
            ThemeFile.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        super(entityClass);
    }

    @Override
    protected void addPrefabValues(final SingleTypeEqualsVerifierApi<?> verifier) {

        Objects.requireNonNull(verifier);
        
        final DataFile dataFile1 = new DataFile();
        dataFile1.setFileId(1);
        dataFile1.setName("file1");
        
        final DataFile dataFile2 = new DataFile();
        dataFile2.setFileId(2);
        dataFile2.setName("file2");
      
        final Directory directory1 = new Directory();
        directory1.setName("directory1");
        
        final Directory directory2 = new Directory();
        directory2.setName("directory2");
        
        verifier
            .withPrefabValues(DataFile.class, dataFile1, dataFile2)
            .withPrefabValues(Directory.class, directory1, directory2);
    }

}
