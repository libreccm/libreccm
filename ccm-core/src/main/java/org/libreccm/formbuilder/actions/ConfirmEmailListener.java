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
import javax.persistence.Lob;
import javax.persistence.Table;
import org.libreccm.formbuilder.ProcessListener;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_confirm_email_listener")
public class ConfirmEmailListener
    extends ProcessListener
    implements Serializable {

    private static final long serialVersionUID = -7009695795355273248L;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    @Lob
    private String body;

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(final String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 19 * hash + Objects.hashCode(this.fromEmail);
        hash = 19 * hash + Objects.hashCode(this.subject);
        hash = 19 * hash + Objects.hashCode(this.body);
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

        if (!(obj instanceof ConfirmEmailListener)) {
            return false;
        }
        final ConfirmEmailListener other = (ConfirmEmailListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.fromEmail, other.getFromEmail())) {
            return false;
        }
        if (!Objects.equals(this.subject, other.getSubject())) {
            return false;
        }
        return Objects.equals(this.body, other.getBody());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ConfirmEmailListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", fromEmail = \"%s\","
                                                + "subject = \"%s\"%s",
                                            fromEmail,
                                            subject,
                                            data));
    }

}
