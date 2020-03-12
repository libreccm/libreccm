/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
