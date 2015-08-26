/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.formbuilder.actions;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.libreccm.formbuilder.ProcessListener;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_xml_email_listeners", schema = "ccm_core")
public class XmlEmailListener extends ProcessListener implements Serializable {

    private static final long serialVersionUID = -4607965414018004925L;

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

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(this.recipient);
        hash = 59 * hash + Objects.hashCode(this.subject);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof XmlEmailListener)) {
            return false;
        }
        final XmlEmailListener other = (XmlEmailListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.recipient, other.recipient)) {
            return false;
        }
        return Objects.equals(this.subject, other.subject);
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof XmlEmailListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", recipient = \"%s\", "
                                                + "subject = \"%s\"%s",
                                            recipient,
                                            subject,
                                            data));
    }

}
