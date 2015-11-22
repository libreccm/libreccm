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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;

/**
 * The options of a section.
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 22/11/2015
 */
@Entity
@Audited
public class DecisiontreeSectionOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long option_id;

    @NotEmpty
    @Column(name = "RANK")
    private int rank;

    @NotEmpty
    @Column(name = "LABEL")
    private LocalizedString label;

    @NotEmpty
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
