/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesRepository;

import java.util.stream.Stream;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
public class PagesGridDataProvider
    extends AbstractBackEndDataProvider<Pages, String> {

    private static final long serialVersionUID = -2388748600960614488L;

    @Inject
    private PagesRepository pagesRepo;

    @Override
    protected Stream<Pages> fetchFromBackEnd(final Query<Pages, String> query) {
        
        return pagesRepo
            .findAll()
            .stream();
    }

    @Override
    protected int sizeInBackEnd(final Query<Pages, String> query) {

        return pagesRepo.findAll().size();
    }

}
