/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.docrepo.portation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.workflow.Workflow;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.libreccm.testutils.DependenciesHelpers.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_docrepo_schema.sql"})
public class ArquillianExampleTest {

    @Inject
    private EntityManager entityManager;

    @Inject
    private CcmObjectRepository ccmObjectRepository;

    public ArquillianExampleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
//        final PomEquippedResolveStage pom = Maven
//                .resolver()
//                .loadPomFromFile("pom.xml");
//        final PomEquippedResolveStage dependencies = pom
//                .importCompileAndRuntimeDependencies();
//        final File[] libs = dependencies.resolve().withTransitivity().asFile();
//
//        for (File lib : libs) {
//            System.err.printf("Adding file '%s' to test archive...%n",
//                              lib.getName());
//        }
//
        return ShrinkWrap
                .create(WebArchive.class,
                        "LibreCCM-org.libreccm.docrepo.ArquillianExampleTest.war").
                addPackage(org.libreccm.core.CcmObject.class.getPackage())
                .addPackage(org.libreccm.security.Permission.class.getPackage())
                .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
                .addPackage(org.libreccm.categorization.Categorization.class.
                        getPackage())
                .addPackage(LocalizedString.class.getPackage())
                .addPackage(Workflow.class.getPackage())
                .addPackage(EntityManagerProducer.class.getPackage())
                .addPackage(MimeTypeConverter.class.getPackage())
                .addPackage(EqualsVerifier.class.getPackage())
                .addPackage(IntegrationTest.class.getPackage())
                .addAsLibraries(getModuleDependencies())
                .addAsLibraries(getCcmCoreDependencies())
                .addAsResource("test-persistence.xml",
                               "META-INF/persistence.xml")
                .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    @InSequence(1)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    @Test
    @InSequence(2)
    public void repoIsInjected() {
        assertThat(ccmObjectRepository, is(not(nullValue())));
    }
}
