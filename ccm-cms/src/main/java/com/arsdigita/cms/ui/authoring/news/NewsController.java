/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.authoring.news;

import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.News;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class NewsController {

    @Inject
    private ContentItemRepository itemRepo;

    @Transactional
    protected String getDescription(
        final News fromNews, final Locale forLocale
    ) {
        Objects.requireNonNull(fromNews);
        final News news = itemRepo
            .findById(fromNews.getObjectId(), News.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No News with ID %d available.", fromNews.getObjectId()
                    )
                )
            );

        return news.getDescription().getValue(forLocale);
    }
    
    @Transactional
    protected String getText(final News fromNews, final Locale forLocale) {
        Objects.requireNonNull(fromNews);
        final News news = itemRepo
            .findById(fromNews.getObjectId(), News.class)
        .orElseThrow(
            () -> new IllegalArgumentException(
                String.format(
                    "No News with ID %d available",
                    fromNews.getObjectId()
                )
            )
        );
        
        return news.getText().getValue(forLocale);
    }
    
    @Transactional
    protected void updateText(
        final News ofNews, 
        final Locale forLocale,
        final String text
    ) {
        Objects.requireNonNull(ofNews);
        final News news = itemRepo
            .findById(ofNews.getObjectId(), News.class)
        .orElseThrow(
            () -> new IllegalArgumentException(
                String.format(
                    "No News with ID %d available",
                    ofNews.getObjectId()
                )
            )
        );
        
        news.getText().addValue(forLocale, text);
        itemRepo.save(news);
    }

    @Transactional
    protected void update(
        final News news,
        final Date releaseDate,
        final Locale locale,
        final String description
        ) {
        Objects.requireNonNull(news);
        final News update = itemRepo
            .findById(news.getObjectId(), News.class)
        .orElseThrow(
            () -> new IllegalArgumentException(
                String.format(
                    "No News with ID %d available",
                    news.getObjectId()
                )
            )
        );
        
        update.setReleaseDate(releaseDate);
        update.getDescription().addValue(locale, description);
        itemRepo.save(update);
    }

}
