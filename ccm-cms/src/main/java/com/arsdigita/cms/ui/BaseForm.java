/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.Assert;

import org.librecms.CmsConstants;

/**
 * A convenience class for CMS forms.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
public abstract class BaseForm extends Form implements Cancellable {

    private final BodySection m_body;
    private final BoxPanel m_actions;
    private Cancel m_cancel;

    protected BaseForm(final String name, final Label heading) {
        super(name, new GridPanel(1));

        setRedirecting(true);

        m_body = new BodySection(heading);
        m_actions = new BoxPanel(BoxPanel.HORIZONTAL);

        add(m_body);
        add(m_actions);

        addComponent(new FormErrorDisplay(this));
    }

    protected BaseForm(final String name,
                       final GlobalizedMessage heading) {
        this(name, new Label(heading));
    }

    private class BodySection extends Section {

        final SimpleContainer m_container;

        BodySection(final Label heading) {
            setHeading(heading);

            m_container = new GridPanel(2);
            setBody(m_container);
        }

        final void add(final Component component) {
            m_container.add(component);
        }

        final void add(final Component component, int hints) {
            m_container.add(component, hints);
        }

    }

    protected final void addComponent(final Component component) {
        m_body.add(component, GridPanel.FULL_WIDTH);
    }

    protected final void addField(final Label name, final Component widget) {
        m_body.add(name);
        m_body.add(widget);
    }

    protected final void addField(final GlobalizedMessage name,
                                  final Component widget) {
        addField(new Label(name), widget);
    }

    protected final void addAction(final Submit button) {
        m_actions.add(button);
    }

    protected final void addAction(final Cancel button) {
        m_cancel = button;
        m_actions.add(button);
    }

    protected final void addSecurityListener(final String action) {
        addSubmissionListener(new FormSecurityListener(action));
    }

    protected final void addSecurityListener(final String action,
                                             final ContentItemRequestLocal item) {
        addSubmissionListener(new FormSecurityListener(action, item));
    }

    @Override
    public boolean isCancelled(final PageState state) {
        return m_cancel != null && m_cancel.isSelected(state);
    }

    protected final class Name extends TextField {

        public Name(final String key, final int max, final boolean required) {
            super(new TrimmedStringParameter(key));

            if (required) {
                addValidationListener(new NotEmptyValidationListener());
            }

            setSize(40);
            setMaxLength(max);
        }

    }

    protected final class Description extends TextArea {

        public Description(final String key,
                           final int maxLength,
                           final boolean isRequired) {
            super(new TrimmedStringParameter(key));
            Assert.isTrue(maxLength > 0, "Max length cannot be negative");

            if (isRequired) {
                addValidationListener(NotNullValidationListener.DEFAULT);
            }
            addValidationListener(new StringLengthValidationListener(maxLength));
            setCols(40);
            setRows(5);
            setWrap(TextArea.SOFT);
        }

    }

    protected final class Finish extends Submit {

        public Finish() {
            super("finish", gz("cms.ui.finish"));
        }

    }

    protected final class Cancel extends Submit {

        public Cancel() {
            super("cancel", gz("cms.ui.cancel"));
        }

    }

    protected static final GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected static final String lz(final String key) {
        return (String) gz(key).localize();
    }

}
