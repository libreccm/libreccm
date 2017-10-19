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
package org.librecms.pagemodel.contentitems;

import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.MultiPartArticleSection;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemRenderer(renders = MultiPartArticle.class)
public class MultiPartArticleRenderer
    extends AbstractContentItemRenderer<MultiPartArticle> {

    @Override
    public void renderItem(final MultiPartArticle article,
                           final Locale language,
                           final Map<String, Object> result) {

        result.put("summary", article.getSummary().getValue(language));
        result.put("sections",
                   article
                       .getSections()
                       .stream()
                       .map(section -> renderSection(section, language))
                       .collect(Collectors.toList()));

    }

    protected Map<String, Object> renderSection(
        final MultiPartArticleSection section, final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("sectionId", section.getSectionId());
        result.put("title", section.getTitle().getValue(language));
        result.put("rank", section.getRank());
        result.put("pageBreak", section.isPageBreak());
        result.put("text", section.getText().getValue(language));

        return result;
    }

}
