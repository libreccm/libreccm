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
package com.arsdigita.cms.ui.contenttypes;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.PageCreateForm;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemInitializer;
import org.librecms.contenttypes.News;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class NewsCreateForm extends PageCreateForm {

    private static final String NEWS_DATE = "newsDate";

    private Date newsDate;

    public NewsCreateForm(final ItemSelectionModel itemSelectionModel,
                          final CreationSelector creationSelector,
                          final StringParameter selectedLanguageParam) {

        super(itemSelectionModel, creationSelector, selectedLanguageParam);
    }

    @Override
    protected void addWidgets() {

        super.addWidgets();

        add(new Label(new GlobalizedMessage("cms.contenttypes.ui.newsitem.date",
                                            CmsConstants.CMS_BUNDLE)));
        newsDate = new Date(NEWS_DATE);
        newsDate.addValidationListener(new NotEmptyValidationListener());
        add(newsDate);
    }

    @Override
    protected ContentItemInitializer<?> getItemInitializer(
        final FormData data, final PageState state) {

        return item -> ((News) item)
            .setReleaseDate((java.util.Date) newsDate.getValue(state));
    }

}
