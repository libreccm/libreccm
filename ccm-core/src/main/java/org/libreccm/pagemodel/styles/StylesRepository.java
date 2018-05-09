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
package org.libreccm.pagemodel.styles;

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class StylesRepository implements Serializable {

    private static final long serialVersionUID = 8350984709496516542L;

    @Inject
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void saveCssProperty(final CssProperty cssProperty) {

        Objects.requireNonNull(cssProperty);

        if (cssProperty.getPropertyId() == 0) {
            entityManager.persist(cssProperty);
        } else {
            entityManager.merge(cssProperty);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteCssProperty(final CssProperty cssProperty) {

        Objects.requireNonNull(cssProperty);

        entityManager.remove(cssProperty);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void saveMediaQuery(final MediaQuery mediaQuery) {
        
        Objects.requireNonNull(mediaQuery);
        
        if (mediaQuery.getMediaQueryId() == 0) {
            entityManager.persist(mediaQuery);
        } else {
            entityManager.merge(mediaQuery);
        }
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteMediaQuery(final MediaQuery mediaQuery){
        
        Objects.requireNonNull(mediaQuery);
        
        entityManager.remove(mediaQuery);
        
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void saveMediaRule(final MediaRule mediaRule) {
        
        Objects.requireNonNull(mediaRule);
        
        if (mediaRule.getMediaRuleId() == 0) {
            entityManager.persist(mediaRule);
        } else {
            entityManager.merge(mediaRule);
        }
        
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteMediaRule(final MediaRule mediaRule) {
        
        Objects.requireNonNull(mediaRule);
        
        entityManager.remove(mediaRule);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void saveRule(final Rule rule) {
        
        Objects.requireNonNull(rule);
        
        if (rule.getRuleId() == 0) {
            entityManager.persist(rule);
        } else {
            entityManager.merge(rule);
        }
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteRule(final Rule rule) {
        
        Objects.requireNonNull(rule);
        
        entityManager.remove(rule);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void saveStyles(final Styles styles) {

        Objects.requireNonNull(styles);

        if (styles.getStyleId() == 0) {
            entityManager.persist(styles);
        } else {
            entityManager.merge(styles);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteStyles(final Styles styles) {

        Objects.requireNonNull(styles);

        entityManager.remove(styles);

    }

}
