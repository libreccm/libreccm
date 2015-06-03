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

import org.libreccm.formbuilder.ProcessListener;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_simple_email_listeners")
public class SimpleEmailListener
    extends ProcessListener
    implements Serializable {

    private static final long serialVersionUID = -5004346250775992079L;

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
        hash = 67 * hash + Objects.hashCode(recipient);
        hash = 67 * hash + Objects.hashCode(subject);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof SimpleEmailListener)) {
            return false;
        }
        final SimpleEmailListener other = (SimpleEmailListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(recipient, other.getRecipient())) {
            return false;
        }
        return Objects.equals(subject, other.getSubject());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof SimpleEmailListener;
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
