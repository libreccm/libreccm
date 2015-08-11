package org.libreccm.jpa;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
@SuppressWarnings("PMD.UnusedPrivateField")
public class EntityManagerProducer {
    
    @Produces
    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;
    
    @Produces
    private AuditReader auditReader = AuditReaderFactory.get(entityManager);
    
}
