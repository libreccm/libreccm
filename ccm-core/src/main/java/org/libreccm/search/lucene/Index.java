/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.search.lucene;

import org.libreccm.web.Host;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "LUCENE_INDEXES", schema = DB_SCHEMA)
public class Index implements Serializable {

    private static final long serialVersionUID = 3197625173477366719L;

    @Id
    @Column(name = "INDEX_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long indexId;

    @OneToOne
    @JoinColumn(name = "HOST_ID")
    private Host host;

    @Column(name = "LUCENE_INDEX_ID")
    private long luceneIndexId;

    public Index() {
        super();
    }

    public long getIndexId() {
        return indexId;
    }

    public void setIndexId(final long indexId) {
        this.indexId = indexId;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(final Host host) {
        this.host = host;
    }

    public long getLuceneIndexId() {
        return luceneIndexId;
    }

    public void setLuceneIndexId(final long luceneIndexId) {
        this.luceneIndexId = luceneIndexId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (indexId ^ (indexId >>> 32));
        hash = 89 * hash + Objects.hashCode(host);
        hash
            = 89 * hash + (int) (luceneIndexId ^ (luceneIndexId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Index)) {
            return false;
        }
        final Index other = (Index) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        if (indexId != other.getIndexId()) {
            return false;
        }
        if (!Objects.equals(host, other.getHost())) {
            return false;
        }
        return luceneIndexId == other.getLuceneIndexId();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Index;
    }
    
    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "indexId = %d, "
                                 + "host = %s, "
                                 + "luceneIndexId = %d"
                                 + " }",
                             super.toString(),
                             indexId,
                             Objects.toString(host),
                             luceneIndexId);
    }

}
