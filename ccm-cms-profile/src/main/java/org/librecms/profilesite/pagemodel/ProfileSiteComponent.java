/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.profilesite.pagemodel;

import org.libreccm.pagemodel.ComponentModel;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.profilesite.ProfileSiteConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PROFILE_SITE_COMPONENTS", schema = DB_SCHEMA)
public class ProfileSiteComponent extends ComponentModel {
    
    private static final long serialVersionUID = 1L;
    
    
}
