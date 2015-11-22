/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.librecms.contenttypes.decisiontree;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;
import static org.librecms.contenttypes.decisiontree.DecisiontreeConstants.DB_SCHEMA; //TODO: 

/**
 * This class represents the content type decisiontree 
 * 
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 17/11/2015
 */
@Entity
@Audited
@Table(name = "DECISIONTREE", schema = DB_SCHEMA)
public class Decisiontree extends ContentItem implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Column(name = "CANCELURL")
    private LocalizedString cancelURL;
    

    @OneToMany(mappedBy = "decisiontreesection")
    private List<DecisiontreeSection> sectionList;

    
    //Getter and Setter
    public LocalizedString getCancelURL() {
        return cancelURL;
    }

    public void setCancelURL(LocalizedString cancelURL) {
        this.cancelURL = cancelURL;
    }

    public List<DecisiontreeSection> getSectionlist() {
        return sectionList;
    }

    public void setSectionlist(List<DecisiontreeSection> sectionlist) {
        this.sectionList = sectionlist;
    }
  
        public void addSection(DecisiontreeSection section) {
        sectionList.add(section);
    }
}
