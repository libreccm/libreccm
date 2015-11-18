/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes.agenda.decisiontree;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.libreccm.l10n.LocalizedString;

/**
 *
 * @author koalamann
 */
@Entity
public class DecisiontreeSection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "SECTION_ID")
    private Long section_id;

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
