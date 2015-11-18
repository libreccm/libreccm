/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes.decisiontree;

import java.io.Serializable;
import java.util.ArrayList;
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
