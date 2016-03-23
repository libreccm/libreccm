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
package com.arsdigita.mail;

import com.arsdigita.util.MessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a email message with optional attachments. This class is a wrapper
 * for the JavaMail API that makes it easier for application developers to
 * create and send email. For simple plain text message, there is a static
 * convenience method that does not require the construction of an explicit Mail
 * object:
 *
 * <pre>
 * Mail.send(to, from, subject, body)
 * </pre>
 *
 * <p>
 * For more complex messages, the API provides methods to set all standard mail
 * headers, attach other pieces of content, and finally invoke the transport
 * process to deliver the message.
 *
 * @author Ron Henderson
 * @version $Id$
 */
public class Mail implements MessageType {

    /**
     * Used for logging.
     */
    private static final Logger s_log = Logger.getLogger(Mail.class);

    private static final InternetAddress[] EMPTY_ADDRESS_LIST
                                           = new InternetAddress[0];
    /**
     * Table of message headers.
     */
    private Hashtable m_headers;
    /**
     * Email addresses the message is being sent to.
     */
    private InternetAddress[] m_to;
    private InternetAddress[] m_filteredTo = EMPTY_ADDRESS_LIST;
    private InternetAddress[] m_invalidTo = EMPTY_ADDRESS_LIST;
    private static Set s_invalidDomains = new HashSet();

    static {
        s_log.debug("Static initalizer starting...");
        s_invalidDomains.add("example.com");
        s_log.debug("Static initalizer finished.");
    }

    /**
     * Email address the message is being sent from.
     */
    private InternetAddress m_from;
    /**
     * Email address used for replies to this message.
     */
    private InternetAddress[] m_replyTo;
    /**
     * Email addresses that the message is being carbon-copied to.
     */
    private InternetAddress[] m_cc;
    /**
     * Email addresses that the message is being blind carbon-copied to.
     */
    private InternetAddress[] m_bcc;
    /**
     * Message subject.
     */
    private String m_subject;
    /**
     * Message body (can be text or HTML).
     */
    private String m_body;
    /**
     * Message body alternate (if the body is HTML)
     */
    private String m_alternate;
    /**
     * Encoding specification for m_body and m_alternate (optional). Default
     * value (null) implies "us-ascii" encoding.
     */
    private String m_encoding;
    /**
     * Message attachments (optional)
     */
    private MimeMultipart m_attachments;
    /**
     * Unique identifier for each mail send out.
     */
    private String m_messageID;
    /**
     * Session object used to send mail.
     */
    private static Session s_session;
    /**
     * SMTP host to connect to. Only used to override the default for testing
     * purposes.
     */
    private static String s_host;
    /**
     * SMTP port to connect to. Only used to override the default for testing
     * purposes.
     */
    private static String s_port;
    // Constants used by Mail
    final static String CONTENT_TYPE = "Content-Type";
    final static String CONTENT_ID = "Content-ID";
    final static String MIXED = "mixed";
    final static String ALTERNATIVE = "alternative";
    /**
     * Disposition of "inline"
     */
    public final static String INLINE = javax.mail.Part.INLINE;
    /**
     * Disposition of "attachment"
     */
    public final static String ATTACHMENT = javax.mail.Part.ATTACHMENT;

    /**
     * Default constructor. Must use the setTo, setSubject (and so on) methods
     * to create a valid mail message.
     */
    public Mail() {
        this(null, null, null, null);
    }

    /**
     * Constructor used to specify to, from, and subject.
     *
     * @param to      one or more of addresses to send the message to
     * @param from    the address the message is being sent from
     * @param subject the subject for the message
     */
    public Mail(String to,
                String from,
                String subject) {
        this(to, from, subject, null);
    }

    /**
     * Constructor used to specify to, from, subject, and body.
     *
     * @param to      one or more of addresses to send the message to
     * @param from    the address the message is being sent from
     * @param subject the subject for the message
     * @param body    the plain text body of the message
     */
    public Mail(String to,
                String from,
                String subject,
                String body) {
        m_to = (to == null ? EMPTY_ADDRESS_LIST : parseAddressField(to));
        filterRecipients();
        m_from = (from == null ? null : parseAddress(from));
        m_subject = subject;
        setBody(body);
    }

    /**
     * Constructor used to specify to, from, subject, body, and encoding.
     *
     * @param to      one or more of addresses to send the message to
     * @param from    the address the message is being sent from
     * @param subject the subject for the message
     * @param body    is plain text body of the message
     * @param enc     the encoding of the body
     */
    public Mail(String to,
                String from,
                String subject,
                String body,
                String enc) {
        this(to, from, subject, body);
        setEncoding(enc);
    }

    /**
     * A convenience method to send a simple plain-text message.
     *
     * @param to      one or more of addresses to send the message to
     * @param from    the address the message is being sent from
     * @param subject the subject for the message
     * @param body    the plain text body of the message
     */
    public static void send(String to,
                            String from,
                            String subject,
                            String body)
        throws MessagingException,
               SendFailedException {
        Mail msg = new Mail(to, from, subject, body);
        msg.send();
    }

    /**
     * Sends the message.
     */
    public void send()
        throws MessagingException,
               SendFailedException {
        final Properties properties = MailConfig.getConfig().getJavaMailProperties();
        Transport transport = getSession().getTransport();
        transport.connect(properties.getProperty("mail.smtp.user"), 
                          properties.getProperty("mail.smtp.password"));
        send(transport);
        transport.close();
    }

    /**
     * Sends the message using a given Transport object (package-level access).
     * This method is used when sending multiple messages at once with a single
     * connection to the mail server.
     *
     * @throws SendFailedException on any kind of MessagingException, also such
     *                             returned from the server. Applications might
     *                             try to catch this and re-schedule sending the
     *                             mail.
     */
    void send(Transport transport)
        throws MessagingException,
               SendFailedException {
        Message msg = null;
        if (m_filteredTo.length > 0) {
            msg = getMessage();

            try {
                transport.sendMessage(msg, msg.getAllRecipients());
            } catch (MessagingException mex) {

                // Close the transport agent and rethrow error for
                // detailed message.
                transport.close();

                throw new SendFailedException("send failed: ", mex);
            }
        }

        // Write a copy of the message into the log file
        if (MailConfig.getConfig().isDebug()) {
            if (msg != null) {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    msg.writeTo(os);
                    s_log.debug("message sent:\n" + os.toString()
                                    + "\n-- EOT --");
                } catch (IOException ex) {
                    s_log.error("unable to log message");
                }
            } else {
                s_log.debug("no message sent. No valid recipients:\n");
            }
        } else {
            s_log.info("message sent to <" + Arrays.asList(m_filteredTo)
                           + "> from <" + m_from + "> subject <" + m_subject
                       + ">");
            s_log.info("messages filtered for <" + Arrays.asList(m_invalidTo)
                           + "> from <" + m_from + "> subject <" + m_subject
                       + ">");
        }
    }

    /**
     * Sets the email address that the message is being sent to.
     *
     * @param to one or more addresses to send the message to
     */
    public void setTo(String to) {
        m_to = parseAddressField(to);
        filterRecipients();
    }

    /**
     * Sets the email address that the message is being sent from.
     *
     * @param from the address the message is sent from
     */
    public void setFrom(String from) {
        m_from = parseAddress(from);
    }

    /**
     * Sets the subject of the message.
     *
     * @param subject the subject of the message
     */
    public void setSubject(String subject) {
        m_subject = subject;
    }

    /**
     * Sets the replyTo address.
     *
     * @param replyTo the address to use for replies
     */
    public void setReplyTo(String replyTo) {
        m_replyTo = parseAddressField(replyTo);
    }

    /**
     * Sets the Message ID
     *
     * @param messageID unique identifier for each email.
     */
    public void setMessageID(String messageID) {
        m_messageID = messageID;
    }

    /**
     * Sets the mail's MIME headers.
     *
     * @param headers a String containing MIME headers
     */
    public void setHeaders(String headers) {
        m_headers = parseHeaderField(headers);
    }

    /**
     * Adds a header (name, value) pair.
     *
     * @param name  the header element name
     * @param value the header element value
     */
    public void addHeader(String name, String value) {
        if (m_headers == null) {
            m_headers = new Hashtable();
        }

        m_headers.put(name, value);
    }

    /**
     * Sets the email address that is being carbon-copied.
     *
     * @param cc the email address for a carbon copy
     */
    public void setCc(String cc) {
        m_cc = parseAddressField(cc);
    }

    /**
     * Sets the email address that is being blind carbon-copied.
     *
     * @param bcc the email address for a blind carbon copy
     */
    public void setBcc(String bcc) {
        m_bcc = parseAddressField(bcc);
    }

    /**
     * Sets the body of the email to a simple plain text message.
     *
     * @param body the body of the message in plain text
     */
    public void setBody(String body) {
        m_body = body;
    }

    /**
     * Sets the body of the email to an HTML encoded message with a plain text
     * alternative.
     *
     * @param body the body of the message in HTML
     * @param alt  the alternate message body in plain text
     */
    public void setBody(String body, String alt) {
        m_body = body;
        m_alternate = alt;
    }

    /**
     * Sets the character encoding. Valid encodings include "us-ascii",
     * "iso-8859-1" for w-Europe, "iso-8859-2" for e-Europe, and so on.
     *
     * @param enc the requested encoding
     */
    public void setEncoding(String enc) {
        m_encoding = enc;
    }

    /**
     * Returns the character encoding that is being used. The default is
     * "us-ascii".
     *
     * @return the string value of the character encoding being used
     */
    public String getEncoding() {
        return m_encoding;
    }

    /**
     * Adds an attachment to a message. This method is private but is invoked by
     * all of the other attach methods once they've constructed an appropraite
     * MimeBodyPart to attach.
     *
     * @param part the message part to attach
     */
    private void attach(MimeBodyPart part)
        throws MessagingException {
        if (m_attachments == null) {
            m_attachments = new MimeMultipart();
        }
        m_attachments.addBodyPart(part);
    }

    /**
     * Adds an attachment with a specified name and description to a message by
     * fetching its content from a URL. Sets the disposition to ATTACHMENT.
     *
     * @param url         the URL to retreieve the content from
     * @param name        the name of the attachment
     * @param description a description of the attachment
     */
    public void attach(URL url,
                       String name,
                       String description)
        throws MessagingException {
        attach(url, name, description, Mail.ATTACHMENT);
    }

    /**
     * Adds an attachment with a specified name, description and disposition to
     * a message by fetching its content from a URL.
     *
     * @param url         the URL to retreieve the content from
     * @param name        the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     */
    public void attach(URL url,
                       String name,
                       String description,
                       String disposition)
        throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        attach(part);

        DataHandler dh = new DataHandler(new URLDataSource(url));

        part.setDataHandler(dh);
        part.setFileName(name);
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Adds an attachment with a specified name and description to a message by
     * fetching its content from a local file. Sets the disposition to
     * ATTACHMENT.
     *
     * @param path        the file path to retreieve the content from
     * @param name        the name of the attachment
     * @param description a description of the attachment
     */
    public void attach(File path,
                       String name,
                       String description)
        throws MessagingException {
        attach(path, name, description, ATTACHMENT);
    }

    /**
     * Adds an attachment with a specified name, description and disposition to
     * a message by fetching its content from a local file.
     *
     * @param path        the file path to retreieve the content from
     * @param name        the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     */
    public void attach(File path,
                       String name,
                       String description,
                       String disposition)
        throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        attach(part);

        DataHandler dh = new DataHandler(new FileDataSource(path));

        part.setDataHandler(dh);
        part.setFileName(name);
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Attaches a byte array to a message. Sets the MIME type and name of the
     * attachment, and initializes its disposition to ATTACHMENT.
     *
     * @param data the content of the attachment
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     */
    public void attach(byte[] data,
                       String type,
                       String name)
        throws MessagingException {
        attach(data, type, name, null, ATTACHMENT);
    }

    /**
     * Attaches a byte array to a message. Sets the MIME type, name, description
     * and disposition of the attachment.
     *
     * @param data        the content of the attachment
     * @param type        the MIME type of the attachment
     * @param name        the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws javax.mail.MessagingException
     */
    public void attach(byte[] data,
                       String type,
                       String name,
                       String description,
                       String disposition)
        throws MessagingException {
        ByteArrayDataSource ds = new ByteArrayDataSource(data, type, name);
        attach(ds, description, disposition);
    }

    /**
     * Attaches a String to a message. Sets the MIME type and name of the
     * attachment, and initializes the disposition to ATTACHMENT.
     *
     * @param data the content of the attachment
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     */
    public void attach(String data,
                       String type,
                       String name)
        throws MessagingException {
        attach(data, type, name, null, ATTACHMENT);
    }

    /**
     * Attaches a String to a message. Sets the MIME type, name, description and
     * disposition of the attachment.
     *
     * @param data        the content of the attachment
     * @param type        the MIME type of the attachment
     * @param name        the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     */
    public void attach(String data,
                       String type,
                       String name,
                       String description,
                       String disposition)
        throws MessagingException {
        ByteArrayDataSource ds = new ByteArrayDataSource(data, type, name);
        attach(ds, description, disposition);
    }

    /**
     * Attaches the content from a ByteArrayInputStream to a message. Sets the
     * MIME type and name of the attachment, and initializes the disposition to
     * ATTACHMENT.
     *
     * @param is   the input stream to read from.
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     */
    public void attach(ByteArrayInputStream is,
                       String type,
                       String name)
        throws MessagingException {
        attach(is, type, name, null, ATTACHMENT);
    }

    /**
     * Attaches the content from a ByteArrayInputStream to a message. Sets the
     * MIME type, name, description and disposition of the attachment.
     *
     * @param is          the input stream to read from.
     * @param type        the MIME type of the attachment
     * @param name        the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     */
    public void attach(ByteArrayInputStream is,
                       String type,
                       String name,
                       String description,
                       String disposition)
        throws MessagingException {
        ByteArrayDataSource ds = new ByteArrayDataSource(is, type, name);
        attach(ds, description, disposition);
    }

    /**
     * Attaches the content from a ByteArrayDataSource to a message. This is
     * used internally by various other methods that take higher-level object
     * types as input. The MIME type and name are determined directly from the
     * dataSource.
     *
     * @param dataSource  the data source to read from
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     */
    protected void attach(ByteArrayDataSource dataSource,
                          String description,
                          String disposition)
        throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        attach(part);

        DataHandler dh = new DataHandler(dataSource);

        part.setDataHandler(dh);
        part.setFileName(dataSource.getName());
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Attaches content to a message by supplying a DataHandler. All relevant
     * parameters (MIME type, name, ...) are determined directly from the
     * DataHandler.
     *
     * @param dh a DataHandler for some piece of content.
     */
    public void attach(DataHandler dh)
        throws MessagingException {
        attach(dh, null, ATTACHMENT);
    }

    /**
     * Attaches content to a message by supplying a DataHandler. Sets the
     * description and disposition of the content.
     *
     * @param dh          the data source to read from
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     */
    public void attach(DataHandler dh,
                       String description,
                       String disposition)
        throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        attach(part);

        part.setDataHandler(dh);
        part.setFileName(dh.getName());
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Utility function that returns an appropriate Session object for sending
     * mail. This uses the default properties from the Mail initializer and any
     * of properties that can be overridden at the package level.
     */
    static synchronized Session getSession() {

        if (s_session == null) {

            // Set up the properties
            Properties props = new Properties(
                MailConfig.getConfig().getJavaMailProperties());

            // Check for overrides of the server information
            if (s_host != null) {
                props.put("mail.smtp.host", s_host);
            }
            if (s_port != null) {
                props.put("mail.smtp.port", s_port);
            }

            // Set up the session
            s_session = Session.getInstance(props, null);
//            s_session.setDebug(MailConfig.getConfig().isDebug());
            s_session.setDebug(true);
        }

        return s_session;
    }

    /**
     * Utility function that returns the message part of the mail. Useful if you
     * want to build a separate Transport process (for example, to queue a
     * number of messages to send all at once rather than invoke the Mail.send()
     * method for each instance.)
     */
    private Message getMessage()
        throws MessagingException {
        // Create the message
        MimeMessage msg = new MimeMessage(getSession());

        msg.setFrom(m_from);
        msg.setRecipients(Message.RecipientType.TO, m_filteredTo);
        msg.setSentDate(new Date());

        /**
         * If no message ID is set then do not generate message-id header.
         */
        if (m_messageID != null) {
            msg.setMessageID("<" + m_messageID + ">");
        }

        if (m_cc != null) {
            msg.setRecipients(Message.RecipientType.CC, m_cc);
        }

        if (m_bcc != null) {
            msg.setRecipients(Message.RecipientType.BCC, m_bcc);
        }

        if (m_replyTo != null) {
            msg.setReplyTo(m_replyTo);
        }

        // Encode the subject
        String enc_subj;
        try {
            enc_subj = MimeUtility.encodeText(m_subject, m_encoding, null);
        } catch (UnsupportedEncodingException uee) {
            s_log.warn("unable to encode subject: " + uee);
            enc_subj = m_subject;
        }
        msg.setSubject(enc_subj);

        // Encode the MIME headers
        if (m_headers != null) {
            Enumeration e = m_headers.keys();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                String value = (String) m_headers.get(name);
                String enc_v;
                try {
                    enc_v = MimeUtility.encodeText(value, m_encoding, null);
                } catch (UnsupportedEncodingException uee) {
                    s_log.warn("unable to encode header element: " + uee);
                    enc_v = value;
                }
                msg.addHeader(name, enc_v);
            }
        }

        // Return the Message object with it's content ready to go.
        return prepareMessageContent(msg);

    }

    /**
     * Sets the host and port to connect to when sending mail. Package-level
     * access (should only be used for testing).
     *
     * @param host the SMTP host to connect to
     * @param port the port number on that host
     */
    synchronized static void setSmtpServer(String host, String port) {
        s_host = host;
        s_port = port;

        // invalidate the current session object
        s_session = null;
    }

    /**
     * Returns the SMTP mail host for debugging and account information.
     *
     * @return the SMTP mail host for debugging and account information.
     */
    public synchronized static String getSmtpServer() {
        return s_host;
    }

    /**
     * Writes the content of the message to the given output stream. Useful for
     * debugging.
     *
     * @param os the output stream to write the message to
     */
    public void writeTo(OutputStream os)
        throws MessagingException {
        try {
            getMessage().writeTo(os);
        } catch (IOException ex) {
            s_log.error("writeTo output error", ex);
        }
    }

    /**
     * Parses an address field.
     */
    private static InternetAddress[] parseAddressField(String str) {
        String[] addrList;

        if (str.indexOf(",") != -1) {
            ArrayList a = new ArrayList();
            StringTokenizer st = new StringTokenizer(str, ",", false);
            while (st.hasMoreTokens()) {
                a.add(st.nextToken());
            }

            addrList = new String[a.size()];
            a.toArray(addrList);
        } else {
            addrList = new String[1];
            addrList[0] = str;
        }

        return parseAddressList(addrList);
    }

    /**
     * Parses an address list.
     */
    private static InternetAddress[] parseAddressList(String[] addrList) {
        InternetAddress[] addrs = new InternetAddress[addrList.length];

        for (int i = 0; i < addrList.length; i++) {
            addrs[i] = parseAddress(addrList[i]);
        }
        return addrs;
    }

    /**
     * Parses an address.
     */
    private static InternetAddress parseAddress(String str) {
        String address = null;
        String personal = null;

        InternetAddress addr = null;

        str = str.trim();

        if (str.indexOf(" ") == -1) {
            address = str;
        } else {
            int sp = str.lastIndexOf(" ");
            personal = str.substring(0, sp);
            address = str.substring(sp + 1);
        }

        try {
            addr = new InternetAddress(address, personal);
        } catch (UnsupportedEncodingException e) {
            s_log.error("unable to parse address: " + str);
        }

        return addr;
    }

    /**
     * Parses a header field.
     */
    private static Hashtable parseHeaderField(String str) {
        String[] headerList;

        if (str.indexOf(",") != -1) {
            ArrayList a = new ArrayList();
            StringTokenizer st = new StringTokenizer(str, ",", false);
            while (st.hasMoreTokens()) {
                a.add(st.nextToken());
            }
            headerList = new String[a.size()];
            a.toArray(headerList);
        } else {
            headerList = new String[1];
            headerList[0] = str;
        }

        return parseHeaderList(headerList);
    }

    /**
     * Parses a header list.
     */
    private static Hashtable parseHeaderList(String[] headerList) {
        Hashtable headers = new Hashtable();

        for (int i = 0; i < headerList.length; i++) {
            parseHeader(headerList[i], headers);
        }
        return headers;
    }

    /**
     * Parses a header.
     */
    private static void parseHeader(String str, Hashtable headers) {
        str = str.trim();

        int sp = str.lastIndexOf(":");
        String name = str.substring(0, sp);
        String value = (str.substring(sp + 1)).trim();

        headers.put(name, value);
    }

    /**
     * Utility function to prepare the content of the message.
     */
    private Message prepareMessageContent(MimeMessage msg)
        throws MessagingException {
        if (m_alternate == null && m_attachments == null) {

            // We have a plain-text message with no attachments.  Use
            // the Message.setText() method to initialize the content
            // and leave the default MIME type alone.
            msg.setText(m_body, m_encoding);

        } else {

            // For anything else the message will be a MIME multipart,
            // with a subtype of of either "mixed" or "alternative"
            // depending on whether we have attachments.
            String subtype = (m_attachments == null) ? ALTERNATIVE : MIXED;

            // Create a MIME multipart for the content.
            MimeMultipart mp = new MimeMultipart(subtype);
            msg.setContent(mp);

            // Next we need to look at whether the message part of the
            // content is going to be text/plain or text/html.
            MimeBodyPart part = new MimeBodyPart();

            if (m_alternate == null) {

                // No alternate, so it must be text/plain with
                // attachments.
                part.setText(m_body, m_encoding);
                part.setHeader(CONTENT_TYPE, MessageType.TEXT_PLAIN);
                mp.addBodyPart(part);

            } else {

                // We have an alternate form, so we supply the body as
                // the first part and the alternate as the second.
                // The overall MIME subtype is probably ALTERNATE
                // (depending on whether we have attachments).
                part.setText(m_body, m_encoding);
                part.setHeader(CONTENT_TYPE, MessageType.TEXT_HTML);
                mp.addBodyPart(part);

                part = new MimeBodyPart();
                part.setText(m_alternate, m_encoding);
                part.setHeader(CONTENT_TYPE, MessageType.TEXT_PLAIN);
                mp.addBodyPart(part);

            }

            // Do we have attachments?  If so then the MIME subtype
            // must be MIXED and and the attachments need to be
            // transferred to the Message.
            if (m_attachments != null) {

                // Add attachments to the Message content.
                for (int i = 0; i < m_attachments.getCount(); i++) {
                    mp.addBodyPart(m_attachments.getBodyPart(i));
                }
            }
        }

        // Save changes to the message.  This will update the MIME
        // headers so the message is ready to send.
        msg.saveChanges();

        return msg;
    }

    /**
     *
     */
    private InternetAddress[] filterRecipients() {
        ArrayList filtered = new ArrayList();
        ArrayList invalid = new ArrayList();
        for (int i = 0; i < m_to.length; i++) {
            Iterator it = s_invalidDomains.iterator();
            boolean isValid = true;
            while (it.hasNext()) {
                if (m_to[i].getAddress().endsWith((String) it.next())) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                filtered.add(m_to[i]);
            } else {
                invalid.add(m_to[i]);
                s_log.debug("filtering message to non-existent email address "
                                + m_to[i]);
            }
        }
        m_filteredTo = (InternetAddress[]) filtered.toArray(
            new InternetAddress[filtered.
            size()]);
        m_invalidTo = (InternetAddress[]) invalid.toArray(
            new InternetAddress[invalid.
            size()]);
        return m_filteredTo;
    }

}
