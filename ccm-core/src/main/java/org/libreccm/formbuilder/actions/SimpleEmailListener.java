/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.formbuilder.actions;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.CollectionId;
import org.libreccm.formbuilder.ProcessListener;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_simple_email_listeners")
public class SimpleEmailListener
        extends ProcessListener
        implements Serializable {
    
    @Column(name = "recipient")
    private String recipient;
    
    @Column(name = "subject")
    private String subject;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    
    
}
