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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.IntStream;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger(Mail.class);

    private static final InternetAddress[] EMPTY_ADDRESS_LIST
                                           = new InternetAddress[0];
    /**
     * Table of message headers.
     */
    //private Hashtable m_headers;
    private final Map<String, String> headers = new HashMap<>();
    /**
     * Email addresses the message is being sent to.
     */
    private InternetAddress[] toAddress;
    private InternetAddress[] filteredTo = EMPTY_ADDRESS_LIST;
    private InternetAddress[] invalidTo = EMPTY_ADDRESS_LIST;
    private final static Set<String> INVALID_DOMAINS = new HashSet<>();

    static {
        LOGGER.debug("Static initalizer starting...");
        INVALID_DOMAINS.add("example.com");
        LOGGER.debug("Static initalizer finished.");
    }

    /**
     * Email address the message is being sent from.
     */
    private InternetAddress fromAddress;
    /**
     * Email address used for replies to this message.
     */
    private InternetAddress[] replyTo;
    /**
     * Email addresses that the message is being carbon-copied to.
     */
    private InternetAddress[] carbonCopy;
    /**
     * Email addresses that the message is being blind carbon-copied to.
     */
    private InternetAddress[] blindCarbonCopy;
    /**
     * Message subject.
     */
    private String subject;
    /**
     * Message body (can be text or HTML).
     */
    private String body;
    /**
     * Message body alternate (if the body is HTML)
     */
    private String alternate;
    /**
     * Encoding specification for m_body and m_alternate (optional). Default
     * value (null) implies "us-ascii" encoding.
     */
    private String encoding;
    /**
     * Message attachments (optional)
     */
    private MimeMultipart attachments;
    /**
     * Unique identifier for each mail send out.
     */
    private String messageId;
    /**
     * Session object used to send mail.
     */
    private static Session session;
    /**
     * SMTP host to connect to. Only used to override the default for testing
     * purposes.
     */
    private static String host;
    /**
     * SMTP port to connect to. Only used to override the default for testing
     * purposes.
     */
    private static String port;
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
     * Default constructor. Must use the setToAddress, setSubject (and so on)
     * methods to create a valid mail message.
     */
    public Mail() {
        this(null, null, null, null);
    }

    /**
     * Constructor used to specify to, from, and subject.
     *
     * @param to one or more of addresses to send the message to
     * @param from the address the message is being sent from
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
     * @param toAddress one or more of addresses to send the message to
     * @param fromAddress the address the message is being sent from
     * @param subject the subject for the message
     * @param body the plain text body of the message
     */
    public Mail(final String toAddress,
                final String fromAddress,
                final String subject,
                final String body) {
        if (toAddress == null) {
            this.toAddress = EMPTY_ADDRESS_LIST;
        } else {
            this.toAddress = parseAddressField(toAddress);
        }
        filterRecipients();
        if (fromAddress == null) {
            this.fromAddress = null;
        } else {
            this.fromAddress = parseAddress(fromAddress);
        }
        this.subject = subject;
        setBody(body);
    }

    /**
     * Constructor used to specify to, from, subject, body, and encoding.
     *
     * @param toAddress one or more of addresses to send the message to
     * @param fromAddress the address the message is being sent from
     * @param subject the subject for the message
     * @param body is plain text body of the message
     * @param enc the encoding of the body
     */
    public Mail(final String toAddress,
                final String fromAddress,
                final String subject,
                final String body,
                final String enc) {
        this(toAddress, fromAddress, subject, body);
        setEncoding(enc);
    }

    /**
     * A convenience method to send a simple plain-text message.
     *
     * @param toAddress one or more of addresses to send the message to
     * @param fromAddress the address the message is being sent from
     * @param subject the subject for the message
     * @param body the plain text body of the message
     *
     * @throws MessagingException If there is a problem creating the mail.
     * @throws SendFailedException If sending the mail fails for some reason.
     */
    public static void send(final String toAddress,
                            final String fromAddress,
                            final String subject,
                            final String body) throws MessagingException,
                                                      SendFailedException {
        Mail msg = new Mail(toAddress, fromAddress, subject, body);
        msg.send();
    }

    /**
     * Sends the message.
     *
     * @throws MessagingException If there is a problem creating the mail.
     * @throws SendFailedException If sending the mail fails for some reason.
     */
    public void send() throws MessagingException, SendFailedException {
        final Properties properties = MailConfig.getConfig().
                getJavaMailProperties();
        final Transport transport = getSession().getTransport();
        if ("true".equals(properties.getProperty("mail.smtp.auth"))) {
            transport.connect(properties.getProperty("mail.smtp.user"),
                              properties.getProperty("mail.smtp.password"));
        } else {
            transport.connect();
        }
        send(transport);
        transport.close();
    }

    /**
     * Sends the message using a given Transport object (package-level access).
     * This method is used when sending multiple messages at once with a single
     * connection to the mail server.
     *
     * @throws SendFailedException on any kind of MessagingException, also such
     * returned from the server. Applications might try to catch this and
     * re-schedule sending the mail.
     */
    void send(final Transport transport) throws MessagingException,
                                                SendFailedException {
//        Message message = null;
        final Message message;
        if (filteredTo.length > 0) {
            message = getMessage();

            try {
                transport.sendMessage(message, message.getAllRecipients());
            } catch (MessagingException ex) {

                // Close the transport agent and rethrow error for
                // detailed message.
                transport.close();

                throw new SendFailedException("send failed: ", ex);
            }
        } else {
            message = null;
        }

        // Write a copy of the message into the log file
        if (MailConfig.getConfig().isDebug()) {
            if (message == null) {
                LOGGER.debug("no message sent. No valid recipients:\n");
            } else {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    message.writeTo(os);
                    LOGGER.debug("message sent:\n{}\n-- EOT --", os.toString());
                } catch (IOException ex) {
                    LOGGER.error("unable to log message", ex);
                }
            }
        } else {
            LOGGER.info("message sent <{}> from <{}> subject <{}>",
                        Arrays.asList(filteredTo), fromAddress, subject);
            LOGGER.info("messages filtered for <{}> from <{}> <{}>",
                        Arrays.asList(invalidTo), fromAddress, subject);
        }
    }

    /**
     * Sets the email address that the message is being sent to.
     *
     * @param toAddress one or more addresses to send the message to
     */
    public void setToAddress(final String toAddress) {
        this.toAddress = parseAddressField(toAddress);
        filterRecipients();
    }

    /**
     * Sets the email address that the message is being sent from.
     *
     * @param from the address the message is sent from
     */
    public void setFromAddress(final String from) {
        fromAddress = parseAddress(from);
    }

    /**
     * Sets the subject of the message.
     *
     * @param subject the subject of the message
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Sets the replyTo address.
     *
     * @param replyTo the address to use for replies
     */
    public void setReplyTo(final String replyTo) {
        this.replyTo = parseAddressField(replyTo);
    }

    /**
     * Sets the Message ID
     *
     * @param messageID unique identifier for each email.
     */
    public void setMessageID(final String messageID) {
        this.messageId = messageID;
    }

    /**
     * Sets the mail's MIME headers.
     *
     * @param mimeHeader a String containing MIME headers
     */
    public void setHeaders(final String mimeHeader) {
        parseHeaderField(mimeHeader, headers);
    }

    /**
     * Adds a header (name, value) pair.
     *
     * @param name the header element name
     * @param value the header element value
     */
    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    /**
     * Sets the email address that is being carbon-copied.
     *
     * @param carbonCopy the email address for a carbon copy
     */
    public void setCarbonCopy(final String carbonCopy) {
        this.carbonCopy = parseAddressField(carbonCopy);
    }

    /**
     * Sets the email address that is being blind carbon-copied.
     *
     * @param blindCarbonCopy the email address for a blind carbon copy
     */
    public void setBlindCarbonCopy(final String blindCarbonCopy) {
        this.blindCarbonCopy = parseAddressField(blindCarbonCopy);
    }

    /**
     * Sets the body of the email to a simple plain text message.
     *
     * @param body the body of the message in plain text
     */
    public final void setBody(final String body) {
        this.body = body;
    }

    /**
     * Sets the body of the email to an HTML encoded message with a plain text
     * alternative.
     *
     * @param body the body of the message in HTML
     * @param alternate the alternate message body in plain text
     */
    public final void setBody(final String body, final String alternate) {
        this.body = body;
        this.alternate = alternate;
    }

    /**
     * Sets the character encoding. Valid encodings include "us-ascii",
     * "iso-8859-1" for w-Europe, "iso-8859-2" for e-Europe, and so on.
     *
     * @param encoding the requested encoding
     */
    public final void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Returns the character encoding that is being used. The default is
     * "us-ascii".
     *
     * @return the string value of the character encoding being used
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Adds an attachment to a message. This method is private but is invoked by
     * all of the other attach methods once they've constructed an appropraite
     * MimeBodyPart to attach.
     *
     * @param part the message part to attach
     */
    private void attach(final MimeBodyPart part) throws MessagingException {
        if (attachments == null) {
            attachments = new MimeMultipart();
        }
        attachments.addBodyPart(part);
    }

    /**
     * Adds an attachment with a specified name and description to a message by
     * fetching its content from a URL. Sets the disposition to ATTACHMENT.
     *
     * @param url the URL to retreieve the content from
     * @param name the name of the attachment
     * @param description a description of the attachment
     *
     * @throws MessagingException If the attachment could not be added.
     */
    public void attach(final URL url,
                       final String name,
                       final String description) throws MessagingException {
        attach(url, name, description, Mail.ATTACHMENT);
    }

    /**
     * Adds an attachment with a specified name, description and disposition to
     * a message by fetching its content from a URL.
     *
     * @param url the URL to retreieve the content from
     * @param name the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final URL url,
                       final String name,
                       final String description,
                       final String disposition) throws MessagingException {
        final MimeBodyPart part = new MimeBodyPart();
        attach(part);

        final DataHandler dataHandler = new DataHandler(new URLDataSource(url));
        part.setDataHandler(dataHandler);
        part.setFileName(name);
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Adds an attachment with a specified name and description to a message by
     * fetching its content from a local file. Sets the disposition to
     * ATTACHMENT.
     *
     * @param path the file path to retreieve the content from
     * @param name the name of the attachment
     * @param description a description of the attachment
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final File path,
                       final String name,
                       final String description) throws MessagingException {
        attach(path, name, description, ATTACHMENT);
    }

    /**
     * Adds an attachment with a specified name, description and disposition to
     * a message by fetching its content from a local file.
     *
     * @param path the file path to retreieve the content from
     * @param name the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final File path,
                       final String name,
                       final String description,
                       final String disposition) throws MessagingException {
        final MimeBodyPart part = new MimeBodyPart();
        attach(part);

        final DataHandler dataHandler = new DataHandler(new FileDataSource(path));
        part.setDataHandler(dataHandler);
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
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final byte[] data,
                       final String type,
                       final String name) throws MessagingException {
        attach(data, type, name, null, ATTACHMENT);
    }

    /**
     * Attaches a byte array to a message. Sets the MIME type, name, description
     * and disposition of the attachment.
     *
     * @param data the content of the attachment
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final byte[] data,
                       final String type,
                       final String name,
                       final String description,
                       final String disposition) throws MessagingException {
        final ByteArrayDataSource dataSource = new ByteArrayDataSource(data,
                                                                       type,
                                                                       name);
        attach(dataSource, description, disposition);
    }

    /**
     * Attaches a String to a message. Sets the MIME type and name of the
     * attachment, and initializes the disposition to ATTACHMENT.
     *
     * @param data the content of the attachment
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final String data,
                       final String type,
                       final String name) throws MessagingException {
        attach(data, type, name, null, ATTACHMENT);
    }

    /**
     * Attaches a String to a message. Sets the MIME type, name, description and
     * disposition of the attachment.
     *
     * @param data the content of the attachment
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final String data,
                       final String type,
                       final String name,
                       final String description,
                       final String disposition) throws MessagingException {
        final ByteArrayDataSource dataSource = new ByteArrayDataSource(data,
                                                                       type,
                                                                       name);
        attach(dataSource, description, disposition);
    }

    /**
     * Attaches the content from a ByteArrayInputStream to a message. Sets the
     * MIME type and name of the attachment, and initializes the disposition to
     * ATTACHMENT.
     *
     * @param inputStream the input stream to read from.
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final ByteArrayInputStream inputStream,
                       final String type,
                       final String name) throws MessagingException {
        attach(inputStream, type, name, null, ATTACHMENT);
    }

    /**
     * Attaches the content from a ByteArrayInputStream to a message. Sets the
     * MIME type, name, description and disposition of the attachment.
     *
     * @param inputStream the input stream to read from.
     * @param type the MIME type of the attachment
     * @param name the name of the attachment
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final ByteArrayInputStream inputStream,
                       final String type,
                       final String name,
                       final String description,
                       final String disposition) throws MessagingException {
        final ByteArrayDataSource dataSource = new ByteArrayDataSource(
                inputStream, type, name);
        attach(dataSource, description, disposition);
    }

    /**
     * Attaches the content from a ByteArrayDataSource to a message. This is
     * used internally by various other methods that take higher-level object
     * types as input. The MIME type and name are determined directly from the
     * dataSource.
     *
     * @param dataSource the data source to read from
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    protected void attach(final ByteArrayDataSource dataSource,
                          final String description,
                          final String disposition) throws MessagingException {
        final MimeBodyPart part = new MimeBodyPart();
        attach(part);

        DataHandler dataHandler = new DataHandler(dataSource);
        part.setDataHandler(dataHandler);
        part.setFileName(dataSource.getName());
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Attaches content to a message by supplying a DataHandler. All relevant
     * parameters (MIME type, name, ...) are determined directly from the
     * DataHandler.
     *
     * @param dataHandler a DataHandler for some piece of content.
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final DataHandler dataHandler) throws MessagingException {
        attach(dataHandler, null, ATTACHMENT);
    }

    /**
     * Attaches content to a message by supplying a DataHandler. Sets the
     * description and disposition of the content.
     *
     * @param dataHandler the data source to read from
     * @param description a description of the attachment
     * @param disposition Mail.ATTACHMENT or Mail.INLINE
     *
     * @throws MessagingException If teh attachment could not be added.
     */
    public void attach(final DataHandler dataHandler,
                       final String description,
                       final String disposition) throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        attach(part);

        part.setDataHandler(dataHandler);
        part.setFileName(dataHandler.getName());
        part.setDescription(description);
        part.setDisposition(disposition);
    }

    /**
     * Utility function that returns an appropriate Session object for sending
     * mail. This uses the default properties from the Mail initializer and any
     * of properties that can be overridden at the package level.
     */
    static synchronized Session getSession() {

        if (session == null) {

            // Set up the properties
            Properties props = new Properties(
                    MailConfig.getConfig().getJavaMailProperties());

            // Check for overrides of the server information
            if (host != null) {
                props.put("mail.smtp.host", host);
            }
            if (port != null) {
                props.put("mail.smtp.port", port);
            }

            // Set up the session
            session = Session.getInstance(props, null);
//            s_session.setDebug(MailConfig.getConfig().isDebug());
            session.setDebug(true);
        }

        return session;
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
        final MimeMessage msg = new MimeMessage(getSession());

        msg.setFrom(fromAddress);
        msg.setRecipients(Message.RecipientType.TO, filteredTo);
        msg.setSentDate(new Date());

        /**
         * If no message ID is set then do not generate message-id header.
         */
        if (messageId != null) {
            msg.setMessageID("<" + messageId + ">");
        }

        if (carbonCopy != null) {
            msg.setRecipients(Message.RecipientType.CC, carbonCopy);
        }

        if (blindCarbonCopy != null) {
            msg.setRecipients(Message.RecipientType.BCC, blindCarbonCopy);
        }

        if (replyTo != null) {
            msg.setReplyTo(replyTo);
        }

        // Encode the subject
        String enc_subj;
        try {
            enc_subj = MimeUtility.encodeText(subject, encoding, null);
        } catch (UnsupportedEncodingException uee) {
            LOGGER.warn("unable to encode subject: " + uee);
            enc_subj = subject;
        }
        msg.setSubject(enc_subj);

        // Encode the MIME headers
//        if (m_headers != null) {
        if (!headers.isEmpty()) {
            final Set<String> keys = headers.keySet();

            for (String key : headers.keySet()) {
                final String value = headers.get(key);
                String encodedValue;
                try {
                    encodedValue = MimeUtility.encodeText(
                            value, encoding, null);
                } catch (UnsupportedEncodingException ex) {
                    LOGGER.warn("unable to encode header element.", ex);
                    encodedValue = value;
                }

                msg.addHeader(key, encodedValue);
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
    synchronized static void setSmtpServer(final String host,
                                           final String port) {
        Mail.host = host;
        Mail.port = port;

        // invalidate the current session object
        session = null;
    }

    /**
     * Returns the SMTP mail host for debugging and account information.
     *
     * @return the SMTP mail host for debugging and account information.
     */
    public synchronized static String getSmtpServer() {
        return host;
    }

    /**
     * Writes the content of the message to the given output stream. Useful for
     * debugging.
     *
     * @param outputStream the output stream to write the message to
     *
     * @throws MessagingException If something goes wrong when writing the
     * message.
     */
    public void writeTo(final OutputStream outputStream)
            throws MessagingException {

        try {
            getMessage().writeTo(outputStream);
        } catch (IOException ex) {
            LOGGER.error("writeTo output error", ex);
        }
    }

    /**
     * Parses an address field.
     */
    private static InternetAddress[] parseAddressField(final String str) {
        final String[] addrList;

        if (str.contains(",")) {
            addrList = str.split(",");
        } else {
            addrList = new String[1];
            addrList[0] = str;
        }

        return parseAddressList(addrList);
    }

    /**
     * Parses an address list.
     */
    private static InternetAddress[] parseAddressList(
            final String[] addressList) {

        final InternetAddress[] addresses = new InternetAddress[addressList.length];

        IntStream.range(0, addressList.length).forEach(i -> {
            addresses[i] = parseAddress(addressList[i]);
        });

        return addresses;
    }

    /**
     * Parses an address.
     */
    private static InternetAddress parseAddress(final String str) {
        final String trimmed = str.trim();
        
        final String address;
        final String personal;
        if (trimmed.contains(" ")) {
            final String[] tokens = trimmed.split(" ");
            personal = tokens[0];
            address = tokens[1];
        } else {
            address = str;
            personal = null;
        }
        
        try {
            return new InternetAddress(address, personal);
        } catch(UnsupportedEncodingException ex) {
            LOGGER.error("Unable to parse address: {}", trimmed);
            LOGGER.error(ex);
            
            return null;
        }
    }

    /**
     * Parses a header field.
     */
    private static Map<String, String> parseHeaderField(
            final String str, final Map<String, String> headers) {
        return parseHeaderList(str.split(","), headers);
    }

    /**
     * Parses a header list.
     */
    private static Map<String, String> parseHeaderList(
            final String[] headerList,
            final Map<String, String> headers) {

        for (String header : headerList) {
            parseHeader(header, headers);
        }

        return headers;
    }

    /**
     * Parses a header.
     */
    private static void parseHeader(final String str,
                                    final Map<String, String> headers) {
        final String trimmed = str.trim();

        final String[] tokens = trimmed.split(":");
        if (tokens.length >= 2) {
            headers.put(tokens[0].trim(), tokens[1].trim());
        }
    }

    /**
     * Utility function to prepare the content of the message.
     */
    private Message prepareMessageContent(final MimeMessage msg)
            throws MessagingException {
        
        if (alternate == null && attachments == null) {

            // We have a plain-text message with no attachments.  Use
            // the Message.setText() method to initialize the content
            // and leave the default MIME type alone.
            msg.setText(body, encoding);

        } else {

            // For anything else the message will be a MIME multipart,
            // with a subtype of of either "mixed" or "alternative"
            // depending on whether we have attachments.
            final String subtype;
            if (attachments == null) {
                subtype = ALTERNATIVE;
            } else {
                subtype = MIXED;
            }

            // Create a MIME multipart for the content.
            final MimeMultipart mimeMultipart = new MimeMultipart(subtype);
            msg.setContent(mimeMultipart);

            // Next we need to look at whether the message part of the
            // content is going to be text/plain or text/html.
            final MimeBodyPart mimeBodyPart = new MimeBodyPart();

            if (alternate == null) {

                // No alternate, so it must be text/plain with
                // attachments.
                mimeBodyPart.setText(body, encoding);
                mimeBodyPart.setHeader(CONTENT_TYPE, MessageType.TEXT_PLAIN);
                mimeMultipart.addBodyPart(mimeBodyPart);

            } else {

                // We have an alternate form, so we supply the body as
                // the first part and the alternate as the second.
                // The overall MIME subtype is probably ALTERNATE
                // (depending on whether we have attachments).
                mimeBodyPart.setText(body, encoding);
                mimeBodyPart.setHeader(CONTENT_TYPE, MessageType.TEXT_HTML);
                mimeMultipart.addBodyPart(mimeBodyPart);

                final MimeBodyPart alternateMimeBodyPart = new MimeBodyPart();
                alternateMimeBodyPart.setText(alternate, encoding);
                alternateMimeBodyPart.setHeader(CONTENT_TYPE, MessageType.TEXT_PLAIN);
                mimeMultipart.addBodyPart(alternateMimeBodyPart);
            }

            // Do we have attachments?  If so then the MIME subtype
            // must be MIXED and and the attachments need to be
            // transferred to the Message.
            if (attachments != null) {
                // Add attachments to the Message content.
                for (int i = 0; i < attachments.getCount(); i++) {
                    mimeMultipart.addBodyPart(attachments.getBodyPart(i));
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
        final List<InternetAddress> filtered = new ArrayList<>();
        final List<InternetAddress> invalid = new ArrayList<>();
        for (InternetAddress toAddres : toAddress) {
            Iterator it = INVALID_DOMAINS.iterator();
            boolean isValid = true;
            while (it.hasNext()) {
                if (toAddres.getAddress().endsWith((String) it.next())) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                filtered.add(toAddres);
            } else {
                invalid.add(toAddres);
                LOGGER.debug("filtering message to non-existent email address " +
                             toAddres);
            }
        }
        filteredTo = (InternetAddress[]) filtered.toArray(
                new InternetAddress[filtered.
                size()]);
        invalidTo = (InternetAddress[]) invalid.toArray(
                new InternetAddress[invalid.
                size()]);
        return filteredTo;
    }

}
