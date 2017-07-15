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
package org.librecms.contenttypes;

import org.librecms.contentsection.ContentItemRepository;

import java.util.List;
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
public class MultiPartArticleSectionManager {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private MultiPartArticleSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public void addSectionToMultiPartArticle(
        final MultiPartArticleSection section,
        final MultiPartArticle article) {

        article.addSection(section);
        section.setRank(article.getSections().size() - 1);
        itemRepo.save(article);
        sectionRepo.save(section);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeSectionFromMultiPartArticle(
        final MultiPartArticleSection section,
        final MultiPartArticle article) {

        article.removeSection(section);
        itemRepo.save(article);
        sectionRepo.delete(section);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void moveToFirst(final MultiPartArticle article,
                            final MultiPartArticleSection section) {

        final List<MultiPartArticleSection> sections = article
            .getSections()
            .stream()
            .sorted((section1, section2) -> Integer.compare(section1.getRank(),
                                                            section2.getRank()))
            .collect(Collectors.toList());

        final int oldRank = section.getRank();

        section.setRank(0);
        sections
            .stream()
            .filter(current -> !current.equals(section))
            .forEach(current -> current.setRank(current.getRank() + 1));

        sections
            .forEach(current -> sectionRepo.save(current));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void moveSectionAfter(final MultiPartArticle article,
                                 final MultiPartArticleSection section,
                                 final MultiPartArticleSection after) {

        Objects.requireNonNull(article);
        Objects.requireNonNull(section);
        Objects.requireNonNull(after);

        final List<MultiPartArticleSection> sections = article
            .getSections()
            .stream()
            .sorted((section1, section2) -> {
                return Integer.compare(section1.getRank(), section2.getRank());
            })
            .collect(Collectors.toList());

        if (!sections.contains(section)) {
            throw new IllegalArgumentException(String.format(
                "MultiPartArticleSection %d is not a section of multipart article %d.",
                section.getSectionId(),
                article.getObjectId()));
        }
        if (!sections.contains(after)) {
            throw new IllegalArgumentException(String.format(
                "MultiPartArticleSection %d is not a section of multipart article %d.",
                after.getSectionId(),
                article.getObjectId()));
        }

        final int afterIndex = sections.indexOf(after);
        for (int i = afterIndex + 1; i < sections.size(); i++) {
            final MultiPartArticleSection current = sections.get(i);
            current.setRank(current.getRank() + 1);
            sectionRepo.save(current);
        }
        
        section.setRank(afterIndex + 1);
        sectionRepo.save(section);
    }

}
