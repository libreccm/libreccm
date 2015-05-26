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
@Table(name = "formbuilder_confirm_redirect_listeners")
public class ConfirmRedirectListener
        extends ProcessListener
        implements Serializable {

    private static final long serialVersionUID = 7891034630202555922L;
    
    @Column(name = "url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfirmRedirectListener other = (ConfirmRedirectListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        return Objects.equals(this.url, other.url);
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ConfirmRedirectListener;
    }
    
    @Override
    public String toString(final String data) {
        return super.toString(String.format(", url = \"%s\"%s",
                                            url,
                                            data));
    }

}
