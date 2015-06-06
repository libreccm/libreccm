package org.libreccm.jpautils;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class EntityManagerProducer {
    
    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;
    
}
