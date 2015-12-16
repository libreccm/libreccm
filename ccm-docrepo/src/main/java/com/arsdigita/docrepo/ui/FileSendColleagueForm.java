/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.docrepo.File;
import com.arsdigita.mail.Mail;
import com.arsdigita.util.StringUtils;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import com.arsdigita.bebop.FormProcessException;

/**
 * This component allows the user to send the document to
 * someone by e-mail. The document is enclosed as an attachment
 * and send using the com.arsdigita.mail package services, this
 * package is built ontop of javax.mail.
 *
 * @see com.arsdigita.mail
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <mailto href="ddao@arsdigita.com">David Dao</a>
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 */
public class FileSendColleagueForm extends Form implements FormProcessListener,
        FormInitListener, Constants {

    private static final Logger log = Logger.getLogger(
            FileSendColleagueForm.class);

    private static final char EMAIL_SEPARATOR = ';';
    private static final String EMAIL_LIST = "emailList";
    private static final String EMAIL_SUBJECT = "subject";
    private static final String DESCRIPTION = "description";

    private RequestLocal m_fileData;
    private TextField m_emailList;
    private TextField m_subject;
    private TextArea m_message;

    private FileInfoPropertiesPane m_parent;

    /**
     * Constructor. Creates the necessary form.
     *
     * @param parent The fileInfoPropertiesPane
     */
    public FileSendColleagueForm(FileInfoPropertiesPane parent) {
        super("FileSendColleagueForm", new ColumnPanel(2));
        m_parent = parent;

        // initialize the file
        m_fileData = new DocRepoRequestLocal();

        add(SEND_FRIEND_FORM_EMAIL_SUBJECT);
        m_subject = new TextField(EMAIL_SUBJECT);
        m_subject.addValidationListener(new NotEmptyValidationListener());
        add(m_subject);

        add(SEND_FRIEND_FORM_EMAIL_LIST);
        m_emailList = new TextField(EMAIL_LIST);
        m_emailList.addValidationListener(new NotEmptyValidationListener());
        add(m_emailList);

        add(SEND_FRIEND_FORM_DESCRIPTION);
        m_message = new TextArea(DESCRIPTION);
        m_message.setRows(10);
        m_message.setCols(40);
        add(m_message);

        SimpleContainer sc = new SimpleContainer();

        sc.add(new Submit(SEND_FRIEND_FORM_SUBMIT));
        CancelButton cancel = new CancelButton(CANCEL);
        sc.add(cancel);

        add(new Label()); // spacer
        add(sc, ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
    }

    /**
     * Returns a file as the request-specific value for the current state
     *
     * @param s The current page state
     * @return A file
     */
    private File getFile(PageState s) {
        return (File) m_fileData.get(s);
    }

    /**
     * Pre-fill the e-mail subject field with file name.
     *
     * @param e the FormSectionEvent
     */
    public void init(FormSectionEvent e)
            throws FormProcessException {
        PageState state = e.getPageState();

        // Todo: fix the Kernel Context
//        if ( Kernel.getContext().getParty() == null ) {
//            Util.redirectToLoginPage(state);
//            return;
//        }

        FormData data = e.getFormData();

        data.put(EMAIL_SUBJECT, getFile(state).getName());
    }


    /**
     * No Form validation happens here, the email addresses are parsed
     * and the file is e-mailed as attachment.
     *
     * @param e The FormSectionEvent
     */
    public void process(FormSectionEvent e)
            throws FormProcessException {

        PageState state = e.getPageState();
        // Todo: not used
        //FormData data = e.getFormData();
        //HttpServletRequest req = state.getRequest();

        String emailRecpts = (String) m_emailList.getValue(state);
        String recipient[] = StringUtils.split(emailRecpts, EMAIL_SEPARATOR);
        String subject = (String) m_subject.getValue(state);

        // message to go with the doc attachment
        String message = (String) m_message.getValue(state);

        // Sender e-mail address
        String from = Utils.getUser(state).getPrimaryEmailAddress()
                .getAddress();

        File file = getFile(state);
        String filename = file.getName();
        String mimeType = file.getContentType();
        byte[] attachment = getBytes(file);

        for (String aRecipient : recipient) {
            // TODO validate email of recipient for format
            sendDocument(aRecipient, from, subject, message, filename,
                    mimeType, attachment);
        }

        m_parent.displayPropertiesAndActions(state);
    }

    /**
     * Send the document as attachment to one e-mail recipient
     *
     * @param recipient The recipient of the mail
     * @param sender The sender of the mail
     * @param subject The subject of the mail
     * @param message The actual message of the mail
     * @param filename The filename to be send
     * @param mimeType The mimetype
     * @param attchmnt The attachment
     */
    private static void sendDocument(String recipient,
                                     String sender,
                                     String subject,
                                     String message,
                                     String filename,
                                     String mimeType,
                                     byte[] attchmnt) {

        try {
            Mail mail =  new Mail(recipient, sender, subject);
            mail.setBody(message);
            mail.attach(attchmnt,  mimeType, filename);
            mail.send();
        } catch (MessagingException exc) {
            log.error("Couldn't send the mail.", exc);
            // Todo: log in some buffer, or schedule for re-try later
        }
    }

    /**
     * Get the bytes from the given document resource
     *
     * @param file The document resource
     * @return A byte array
     */
    private static byte[] getBytes(File file) {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            InputStream inputStream = file.getInputStream();
            byte[] buf = new byte[8192];
            int sz = 0;
            while ((sz = inputStream.read(buf, 0, 8192)) != -1) {
                outputStream.write(buf, 0, sz);
            }
        } catch(IOException iox) {
            iox.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
