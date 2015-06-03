/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.formbuilder.actions;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.libreccm.formbuilder.ProcessListener;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_remote_server_post_listener")
public class RemoteServerPostListener
        extends ProcessListener
        implements Serializable {

    private static final long serialVersionUID = 7095242410811956838L;

    private String remoteUrl;

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(final String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(this.remoteUrl);
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
        
        if (!(obj instanceof RemoteServerPostListener)) {
            return false;
        }
        final RemoteServerPostListener other = (RemoteServerPostListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(this.remoteUrl, other.getRemoteUrl());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof RemoteServerPostListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", remoteUrl = \"%s\"%s",
                                            remoteUrl,
                                            data));
    }

}
