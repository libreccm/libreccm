/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;

/**
 * DecisiontreeSection, which is part of a decisiontree.
 * 
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 22/11/2015
 */
@Entity
@Audited
public class DecisiontreeSection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "SECTION_ID")
    private Long section_id;

    @NotEmpty
    @Column(name = "PARAMETERNAME")
    private LocalizedString parameterName;
    
    
    @Column(name = "INSTRUCTIONS")
    private LocalizedString instructions;
    
    @ManyToOne
    @JoinColumn(name = "DECISIONTREE_ID")
    private Decisiontree decisiontree;


    @OneToMany(mappedBy = "decisiontreeSectionOption")
    private List<DecisiontreeSectionOption> optionlist;

    
    
    //Getter and setter:
    public Long getId() {
        return section_id;
    }

    public void setId(Long id) {
        this.section_id = id;
    }

    public LocalizedString getParameterName() {
        return parameterName;
    }

    public void setParameterName(LocalizedString parameterName) {
        this.parameterName = parameterName;
    }

    public List<DecisiontreeSectionOption> getOptionlist() {
        return optionlist;
    }

    public void setOptionlist(List<DecisiontreeSectionOption> sectionlist) {
        this.optionlist = sectionlist;
    }

    public void addOption(DecisiontreeSectionOption option) {
        optionlist.add(option);
    }


}
