/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;


import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

/**
 * A Bebop Confirmation Page which should be mounted at ConfirmPage.CONFIRM_URL by the BebopMapDispatcher.
 * This page takes three URL parameters:
 * <ul>
 * <li>A confirmation message with variable name ConfirmPage.CONFIRM_MSG_VAR
 * <li>An OK URL with variable name ConfirmPage.OK_URL_VAR
 * <li>A Cancel URL with variable name ConfirmPage.CANCEL_URL_VAR
 * </ul>
 * The page displays a form asking the confirmation message passed in.  If the user hits <i>OK</i>,
 * Then the page redirects to the OK URL.  Otherwise, if the user hits <i>Cancel<i/>,
 * The page redirects to the Cancel URL.
 * @author Bryan Che 
 */

public class ConfirmPage extends Page {
    private StringParameter m_ConfirmMsgParam;
    private StringParameter m_OkUrlParam;
    private StringParameter m_CancelUrlParam;

    private RequestLocal m_ConfirmMsgRL;
    private RequestLocal m_OkUrlRL;
    private RequestLocal m_CancelUrlRL;

    //URL at which to mount this page
    public static final String CONFIRM_URL = "BEBOP-confirmation-page";

    //URL variable names
    private static final String CONFIRM_MSG_VAR = "confirm-msg";
    private static final String OK_URL_VAR = "ok-url";
    private static final String CANCEL_URL_VAR = "cancel-url";

    public ConfirmPage() {
        super();

        m_ConfirmMsgParam = new StringParameter(CONFIRM_MSG_VAR);
        m_OkUrlParam = new StringParameter(OK_URL_VAR);
        m_CancelUrlParam = new StringParameter(CANCEL_URL_VAR);

        //add global state params
        addGlobalStateParam(m_ConfirmMsgParam);
        addGlobalStateParam(m_OkUrlParam);
        addGlobalStateParam(m_CancelUrlParam);

        //initialize RequestLocals for the URL params
        m_ConfirmMsgRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return ps.getValue(m_ConfirmMsgParam);
                }
            };
        m_OkUrlRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return ps.getValue(m_OkUrlParam);
                }
            };
        m_CancelUrlRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return ps.getValue(m_CancelUrlParam);
                }
            };

        //set the title
        buildTitle();

        //add the form
        ConfirmForm cf = new ConfirmForm(m_ConfirmMsgRL, m_OkUrlRL, m_CancelUrlRL);
        add(cf);

        lock();
    }

    /**
     * Returns a URL (minus "http://" string and server name) at which to access the ConfirmPage
     * with the given Confirmation Message, OK URL, and Cancel URL.
     * @param sConfirmMsg the Confirmation message to display on the page
     * @param sOkUrl the URL to which to redirect if the user hits <i>OK</i>
     * @param sCancelUrl the URL to which to redirect if the user hits <i>Cancel</i>
     * @return URL at which to access the ConfirmPage
     */
    public static String getConfirmUrl(String sConfirmMsg, String sOkUrl, String sCancelUrl) {
        final ParameterMap params = new ParameterMap();

        params.setParameter(CONFIRM_MSG_VAR, sConfirmMsg);
        params.setParameter(OK_URL_VAR, sOkUrl);
        params.setParameter(CANCEL_URL_VAR, sCancelUrl);

        return URL.there("/" + CONFIRM_URL, params).toString();
    }

    protected void buildTitle() {
        class ConfirmPagePrintListener implements PrintListener {
            public void prepare(PrintEvent e) {
                Label label = (Label) e.getTarget();
                PageState ps = e.getPageState();

                label.setLabel((String)m_ConfirmMsgRL.get(ps));
            }
        }

        setTitle(new Label(new ConfirmPagePrintListener()));
    }

    private class ConfirmFormPrintListener implements PrintListener {
        private RequestLocal m_RL;

        ConfirmFormPrintListener(RequestLocal ConfirmMsgRL) {
            m_RL = ConfirmMsgRL;
        }

        public void prepare(PrintEvent e) {
            Label label = (Label) e.getTarget();
            PageState ps = e.getPageState();

            label.setLabel((String)m_RL.get(ps) );
        }
    }

    private class ConfirmForm extends Form implements FormInitListener, FormProcessListener {
        private Label m_ConfirmMsgLabel;
        private Submit m_OkButton;
        private Submit m_CancelButton;

        private RequestLocal m_OkRL;
        private RequestLocal m_CancelRL;

        private String m_sOkUrl = null;
        private String m_sCancelUrl = null;

        public ConfirmForm(RequestLocal ConfirmMsgRL, RequestLocal OkUrlRL, RequestLocal CancelUrlRL) {
            super("ConfirmForm");
            m_ConfirmMsgLabel = new Label(new ConfirmFormPrintListener(ConfirmMsgRL));

            this.add(m_ConfirmMsgLabel);

            m_OkButton = new Submit("OK");
            m_OkButton.setButtonLabel("OK");
            this.add(m_OkButton);
            m_OkRL = OkUrlRL;

            m_CancelButton = new Submit("Cancel");
            m_CancelButton.setButtonLabel("Cancel");
            this.add(m_CancelButton);
            m_CancelRL = CancelUrlRL;

            this.addInitListener(this);
            this.addProcessListener(this);
        }

        public void init(FormSectionEvent e) {
            PageState ps = e.getPageState();

            //initialize the OK and Cancel URL's
            m_sOkUrl = (String) m_OkRL.get(ps);
            m_sCancelUrl = (String) m_CancelRL.get(ps);
        }

        public void process(FormSectionEvent e) {
            PageState ps = e.getPageState();

            if (m_OkButton.isSelected(ps)) {
                throw new RedirectSignal(m_sOkUrl, true);
            } else {
                throw new RedirectSignal(m_sCancelUrl, false);
            }
        }
    }
}
