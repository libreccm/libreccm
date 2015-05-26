/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class EntitiesTestCore {

    private final Class<?> entityClass;

    public EntitiesTestCore(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Test
    public void verifyEqualsAndHashCode() {
        EqualsVerifier
                .forClass(entityClass)
                .suppress(Warning.STRICT_INHERITANCE)
                .withRedefinedSuperclass()
                .verify();
    }
}
