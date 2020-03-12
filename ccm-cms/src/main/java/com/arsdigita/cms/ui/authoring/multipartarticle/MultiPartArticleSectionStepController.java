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

import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.MultiPartArticleSection;
import org.librecms.contenttypes.MultiPartArticleSectionManager;
import org.librecms.contenttypes.MultiPartArticleSectionRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class MultiPartArticleSectionStepController {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private MultiPartArticleSectionRepository sectionRepo;

    @Inject
    private MultiPartArticleSectionManager sectionManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<MultiPartArticleSection> retrieveSections(
        final MultiPartArticle forArticle) {

        final MultiPartArticle article = itemRepo
            .findById(forArticle.getObjectId(),
                      MultiPartArticle.class)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No MultiPartArticle with ID %d in the database.",
                    forArticle.getObjectId())));

        //Ensure that the sections are loaded
        return article
            .getSections()
            .stream()
            .sorted((section1, section2) -> {
                return Integer.compare(section1.getRank(), section2.getRank());
            })
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addSection(final MultiPartArticle article,
                              final MultiPartArticleSection section) {

        final MultiPartArticle theArticle = itemRepo
            .findById(article.getObjectId(),
                      MultiPartArticle.class)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No MultiPartArticle with ID %d in the database.",
            article.getObjectId())));

        sectionManager.addSectionToMultiPartArticle(section, theArticle);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeSection(final MultiPartArticle article,
                                 final MultiPartArticleSection section) {

        final MultiPartArticle theArticle = itemRepo
            .findById(article.getObjectId(),
                      MultiPartArticle.class)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No MultiPartArticle with ID %d in the database.",
            article.getObjectId())));

        final MultiPartArticleSection theSection = sectionRepo
            .findById(section.getSectionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No MultiPartArticleSection with ID %d in the database.",
            section.getSectionId())));

        sectionManager.removeSectionFromMultiPartArticle(theSection,
                                                         theArticle);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void moveToFirst(final MultiPartArticle article,
                               final MultiPartArticleSection section) {

        final MultiPartArticle theArticle = itemRepo
            .findById(article.getObjectId(),
                      MultiPartArticle.class)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No MultiPartArticle with ID %d in the database.",
            article.getObjectId())));

        final MultiPartArticleSection theSection = sectionRepo
            .findById(section.getSectionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No MultiPartArticleSection with ID %d in the database.",
            section.getSectionId())));

        sectionManager.moveToFirst(theArticle, theSection);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void moveAfter(final MultiPartArticle article,
                             final MultiPartArticleSection section,
                             final MultiPartArticleSection after) {

        final MultiPartArticle theArticle = itemRepo
            .findById(article.getObjectId(),
                      MultiPartArticle.class)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No MultiPartArticle with ID %d in the database.",
            article.getObjectId())));

        sectionManager.moveSectionAfter(theArticle, section, after);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getSectionTitle(
        final MultiPartArticleSection ofSection, final Locale forLocale
    ) {
        Objects.requireNonNull(ofSection);
        Objects.requireNonNull(forLocale);

        final MultiPartArticleSection section = sectionRepo
            .findById(ofSection.getSectionId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No section with ID %d available.", ofSection
                            .getSectionId()
                    )
                )
            );
        return section.getTitle().getValue(forLocale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getSectionText(
        final MultiPartArticleSection ofSection, final Locale forLocale
    ) {
        Objects.requireNonNull(ofSection);
        Objects.requireNonNull(forLocale);

        final MultiPartArticleSection section = sectionRepo
            .findById(ofSection.getSectionId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No section with ID %d available.", ofSection
                            .getSectionId()
                    )
                )
            );
        return section.getText().getValue(forLocale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void updateSection(
        final MultiPartArticleSection section,
        final String title,
        final String text,
        final boolean pageBreak,
        final Locale locale
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(locale);

        final MultiPartArticleSection update = sectionRepo
            .findById(section.getSectionId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No section with ID %d available.",
                        section.getSectionId()
                    )
                )
            );
        update.getTitle().addValue(locale, title);
        update.getText().addValue(locale, title);
        update.setPageBreak(pageBreak);
    }

}
