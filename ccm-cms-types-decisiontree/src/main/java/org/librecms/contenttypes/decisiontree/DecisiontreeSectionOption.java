/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes.decisiontree;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.libreccm.l10n.LocalizedString;

/**
 *
 * @author koalamann
 */
@Entity
public class DecisiontreeSectionOption implements Serializable {

    @Id
    private Long option_id;

    private static final long serialVersionUID = 1L;

    @Column(name = "RANK")
    private int rank;

    @Column(name = "LABEL")
    private LocalizedString label;

    @Column(name = "VALUE")
    private LocalizedString value;
    
    @Embedded
    private DecisiontreeOptionTarget target;

    @ManyToOne
    private DecisiontreeSection section;
        
    
    //Getter and Setter
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getValue() {
        return value;
    }

    public void setValue(LocalizedString value) {
        this.value = value;
    }

    public DecisiontreeOptionTarget getTarget() {
        return target;
    }

    public void setTarget(DecisiontreeOptionTarget target) {
        this.target = target;
    }
    

    public Long getOption_id() {
        return option_id;
    }

    public void setOption_id(Long option_id) {
        this.option_id = option_id;
    }

}
