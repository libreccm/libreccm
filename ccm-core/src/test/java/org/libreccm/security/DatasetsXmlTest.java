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
package org.libreccm.security;

import static org.libreccm.testutils.DatasetType.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.testutils.DatasetType;
import org.libreccm.testutils.DatasetsVerifier;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
public class DatasetsXmlTest extends DatasetsVerifier {

    @Parameterized.Parameters(name = "Dataset {0}")
    public static Collection<String> data() {
        return Arrays.asList(new String[]{
            "/datasets/org/libreccm/security/ChallengeManagerTest/data.xml",
            
            "/datasets/org/libreccm/security/ChallengeManagerTest/after-create-account-activation.xml",
            "/datasets/org/libreccm/security/ChallengeManagerTest/after-create-email-verification.xml",
            "/datasets/org/libreccm/security/ChallengeManagerTest/after-create-password-recovery.xml",
            
            "/datasets/org/libreccm/security/ChallengeManagerTest/after-finish-account-activation.xml",
            "/datasets/org/libreccm/security/ChallengeManagerTest/after-finish-email-verification.xml",
            "/datasets/org/libreccm/security/ChallengeManagerTest/after-finish-password-recovery.xml",
            
            "/datasets/org/libreccm/security/OneTimeAuthManagerTest/data.xml",
            "/datasets/org/libreccm/security/OneTimeAuthManagerTest/after-create.xml",
            "/datasets/org/libreccm/security/OneTimeAuthManagerTest/after-invalidate.xml",});
    }

    public DatasetsXmlTest(final String datasetPath) {
        super(datasetPath);
    }

    @Override
    public DatasetType getDatasetType() {
        return FLAT_XML;
    }

    @Override
    public String[] getSchemas() {
        return new String[]{"ccm_core"};
    }

}
