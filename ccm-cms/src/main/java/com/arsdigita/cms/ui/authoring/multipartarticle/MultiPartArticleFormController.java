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
package com.arsdigita.cms.ui.authoring.multipartarticle;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contenttypes.MultiPartArticle;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class MultiPartArticleFormController {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected Optional<Folder> getArticleFolder(final MultiPartArticle article) {

        final Optional<ContentItem> mpa = itemRepo.findById(
            article.getObjectId()
        );

        if (mpa.isPresent()) {
            return itemManager.getItemFolder(mpa.get());
        } else {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getName(
        final MultiPartArticle fromMpa, final Locale forLocale
    ) {
        Objects.requireNonNull(fromMpa);
        Objects.requireNonNull(forLocale);
        final MultiPartArticle mpa = itemRepo
            .findById(fromMpa.getObjectId(), MultiPartArticle.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No MultiPartArticle with ID %d available",
                        fromMpa.getObjectId()
                    )
                )
            );
        return mpa.getName().getValue(forLocale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getTitle(
        final MultiPartArticle fromMpa, final Locale forLocale
    ) {
        Objects.requireNonNull(fromMpa);
        Objects.requireNonNull(forLocale);
        final MultiPartArticle mpa = itemRepo
            .findById(fromMpa.getObjectId(), MultiPartArticle.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No MultiPartArticle with ID %d available",
                        fromMpa.getObjectId()
                    )
                )
            );

        return mpa.getTitle().getValue(forLocale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getSummary(
        final MultiPartArticle fromMpa, final Locale forLocale
    ) {
        Objects.requireNonNull(fromMpa);
        Objects.requireNonNull(forLocale);
        final MultiPartArticle mpa = itemRepo
            .findById(fromMpa.getObjectId(), MultiPartArticle.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No MultiPartArticle with ID %d available",
                        fromMpa.getObjectId()
                    )
                )
            );
        return mpa.getSummary().getValue(forLocale);
    }

}
