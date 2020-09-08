package org.libreccm.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * CDI producer providing access to the JPA {@link EntityManager} using
 * {@code @Inject}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
@SuppressWarnings("PMD.UnusedPrivateField")
public class EntityManagerProducer {

    @Produces
    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;

}
