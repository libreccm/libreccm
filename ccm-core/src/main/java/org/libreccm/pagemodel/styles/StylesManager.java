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
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class StylesManager implements Serializable {

    private static final long serialVersionUID = -2906584926633549611L;

    @Inject
    private StylesRepository stylesRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)

    public void addCssPropertyToRule(final CssProperty property,
                                     final Rule rule) {

        Objects.requireNonNull(property);
        Objects.requireNonNull(rule);

        rule.addProperty(property);
        stylesRepo.saveRule(rule);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void removeCssPropertyFromRule(final CssProperty property,
                                          final Rule rule) {

        Objects.requireNonNull(property);
        Objects.requireNonNull(rule);

        rule.removeProperties(property);
        stylesRepo.saveRule(rule);
        stylesRepo.deleteCssProperty(property);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void addMediaRuleToStyles(final MediaRule mediaRule,
                                     final Styles styles) {

        Objects.requireNonNull(styles);
        Objects.requireNonNull(mediaRule);

        styles.addMediaRule(mediaRule);
        stylesRepo.saveStyles(styles);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void removeMediaRuleFromStyles(final MediaRule mediaRule,
                                          final Styles styles) {

        Objects.requireNonNull(styles);
        Objects.requireNonNull(mediaRule);

        styles.removeMediaRule(mediaRule);
        stylesRepo.saveStyles(styles);
        stylesRepo.deleteMediaRule(mediaRule);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void addRuleToMediaRule(final Rule rule, final MediaRule mediaRule) {

        Objects.requireNonNull(rule);
        Objects.requireNonNull(mediaRule);
        
        mediaRule.addRule(rule);
        stylesRepo.saveMediaRule(mediaRule);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void removeRuleFromMediaRule(final Rule rule,
                                        final MediaRule mediaRule) {

        Objects.requireNonNull(rule);
        Objects.requireNonNull(mediaRule);
        
        mediaRule.removeRule(rule);
        stylesRepo.saveMediaRule(mediaRule);
        stylesRepo.deleteRule(rule);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void addRuleToStyles(final Rule rule, final Styles styles) {

        Objects.requireNonNull(rule);
        Objects.requireNonNull(styles);
        
        styles.addRule(rule);
        stylesRepo.saveStyles(styles);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void removeRuleFromStyles(final Rule rule, final Styles styles) {

        Objects.requireNonNull(rule);
        Objects.requireNonNull(styles);
        
        styles.removeRule(rule);
        stylesRepo.deleteRule(rule);
    }

}
