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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import java.io.File;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.librecms.CmsConstants;

/**
 * A form section with two widgets: a mime-type selection widget and a file
 * upload widget. The section will attempt to automatically guess the mime type
 * from the filename (if necessary), and return the mime type.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileUploadSection extends FormSection {

    private SingleSelect mimeWidget;
    private FileUpload fileWidget;
    private String mimePrefix;
    private String defaultMimeType;
    private String parameterPrefix;

    /**
     * The mime type widget
     */
    public static final String MIME_TYPE = "mime_type";

    /**
     * The file upload widget
     */
    public static final String FILE_UPLOAD = "file_upload";

    /**
     * Automatically guess the mime type
     */
    public static final String GUESS_MIME = "-guess-";

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @param panel The panel that is to be used to lay out the components
     *
     */
    public FileUploadSection(final GlobalizedMessage mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType,
                             final Container panel) {
        this(mimeLabel, mimePrefix, defaultMimeType, "", panel);
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @param panel The panel that is to be used to lay out the components
     *
     * @deprecated use the same constructor but with the GlobalizedMessage for
     * the mimeLabel
     */
    public FileUploadSection(final String mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType,
                             final Container panel) {
        // This takes advantage of the fact that the "key" is returned
        // when it is not present in the message bundle
        this(new GlobalizedMessage(mimeLabel),
             mimePrefix,
             defaultMimeType,
             panel);
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @param parameterPrefix Prepended to MIME_TYPE and FILE_UPLOAD for
     * parameter names so that more than 1 file upload widgets may be used per
     * form
     *
     * @param panel The panel that is to be used to lay out the components
     *
     * @deprecated use the same constructor but with the GlobalizedMessage for
     * the mimeLabel
     */
    public FileUploadSection(final String mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType,
                             final String parameterPrefix,
                             final Container panel
    ) {
        // This takes advantage of the fact that the "key" is returned
        // when it is not present in the message bundle
        this(new GlobalizedMessage(mimeLabel, CmsConstants.CMS_BUNDLE),
             mimePrefix,
             defaultMimeType,
             parameterPrefix,
             panel);
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @param parameterPrefix Prepended to MIME_TYPE and FILE_UPLOAD for
     * parameter names so that more than 1 file upload widgets may be used per
     * form
     *
     * @param panel The panel that is to be used to lay out the components
     *
     */
    public FileUploadSection(final GlobalizedMessage mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType,
                             final String parameterPrefix,
                             final Container panel) {

        super(panel);

        this.mimePrefix = mimePrefix;
        this.defaultMimeType = defaultMimeType;
        if (parameterPrefix == null) {
            this.parameterPrefix = "";
        } else {
            this.parameterPrefix = parameterPrefix;
        }

        add(new Label(mimeLabel, false));
        mimeWidget = new SingleSelect(getMimeTypeWidgetName());
        addMimeOptions(mimeWidget, mimePrefix);
        mimeWidget
            .addOption(new Option(GUESS_MIME,
                                  new Label(new GlobalizedMessage(
                                      "cms.ui.authoring.file_upload.auto_detect",
                                      CmsConstants.CMS_BUNDLE))));

        mimeWidget.setDefaultValue(GUESS_MIME);
        add(mimeWidget);

        add(new Label(new GlobalizedMessage("cms.ui.upload_new_content",
                                            CmsConstants.CMS_BUNDLE)));
        fileWidget = new FileUpload(getFileUploadWidgetName());
        add(fileWidget);
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @param parameterPrefix Prepended to MIME_TYPE and FILE_UPLOAD for
     * parameter names so that more than 1 file upload widgets may be used per
     * form
     *
     */
    public FileUploadSection(final GlobalizedMessage mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType,
                             final String parameterPrefix) {
        this(mimeLabel,
             mimePrefix,
             defaultMimeType,
             parameterPrefix,
             new ColumnPanel(2, true));
        final ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @param parameterPrefix Prepended to MIME_TYPE and FILE_UPLOAD for
     * parameter names so that more than 1 file upload widgets may be used per
     * form
     *
     * @deprecated use the same constructor but with the GlobalizedMessage for
     * the mimeLabel
     */
    public FileUploadSection(final String mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType,
                             final String parameterPrefix) {
        // This takes advantage of the fact that the "key" is returned
        // when it is not present in the message bundle
        this(new GlobalizedMessage(mimeLabel, CmsConstants.CMS_BUNDLE),
             mimePrefix,
             defaultMimeType,
             parameterPrefix);
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The label for the mime type widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     * @deprecated use the same constructor but with the GlobalizedMessage for
     * the mimeLabel
     */
    public FileUploadSection(final String mimeLabel,
                             final String mimePrefix,
                             final String defaultMimeType) {

        // This takes advantage of the fact that the "key" is returned
        // when it is not present in the message bundle
        this(new GlobalizedMessage(mimeLabel, CmsConstants.CMS_BUNDLE),
             mimePrefix,
             defaultMimeType,
             "");
    }

    /**
     * Construct a new FileUploadSection
     *
     * @param mimeLabel The GlobalizedMessage for the label for the mime type
     * widget
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     * @param defaultMimeType The default mime type that should be assumed if
     * the guessing fails
     *
     */
    public FileUploadSection(GlobalizedMessage mimeLabel,
                             String mimePrefix,
                             String defaultMimeType) {
        this(mimeLabel, mimePrefix, defaultMimeType, "");
    }

    /**
     * Try to guess the mime type from the filename, and return it. The parent
     * form should call this method in its process listener. Note that this
     * method may return null if the mime type could not be guessed.
     *
     * @param event The form section event
     * @return The mime type of the file.
     */
    public MimeType getMimeType(final FormSectionEvent event) {

        final FormData data = event.getFormData();

        final String fileName = (String) data.get(getFileUploadWidgetName());
        final String mimeTypeName = (String) data.get(getMimeTypeWidgetName());

        // Guess the mime type from the filename
        MimeType mimeType = null;
        if (fileName != null) {
            try {
                if (GUESS_MIME.equals(mimeTypeName)) {
                    // Guess the mime type from the file extension
                    mimeType = new MimeType(MimetypesFileTypeMap
                        .getDefaultFileTypeMap()
                        .getContentType(fileName));
                } else {
                    mimeType = new MimeType(mimeTypeName);
                }
            } catch (MimeTypeParseException ex) {
                mimeType = null;
            }
        }

        // Failed to guess it, failed to load it, fall back on the default
        if (mimeType == null) {
            try {
                mimeType = new MimeType(defaultMimeType);
            } catch (MimeTypeParseException ex) {
                mimeType = null;
            }
        }

        return mimeType;
    }

    /**
     * Obtain a File object from the file upload widget. The containing form
     * should call this method in its process listener.
     *
     * @param event The form section event
     * @return
     */
    public File getFile(final FormSectionEvent event) {

        final String fileName = getFileName(event);

        if (fileName != null && fileName.length() > 0) {
            return ((MultipartHttpServletRequest) unwrapRequest(event
                    .getPageState()
                    .getRequest()))
                .getFile(getFileUploadWidgetName());
        }

        return null;
    }

    private ServletRequest unwrapRequest(final HttpServletRequest request) {

        ServletRequest current = request;
        while (current instanceof HttpServletRequestWrapper) {
            current = ((HttpServletRequestWrapper) current).getRequest();
        }

        return current;
    }

    /**
     * Obtain a filename from the file upload widget. The containing form should
     * call this method in its process listener.
     *
     * @param event The form section event
     * @return
     */
    public String getFileName(final FormSectionEvent event) {

        return event
            .getFormData()
            .getString(getFileUploadWidgetName());
    }

    /**
     * Set the value for the mime type widget. The containing form should call
     * this method in its init listener
     *
     * @param event The form section event
     * @param mimeType The mime type to set, such as "text/html" or "img/jpeg"
     *
     */
    public void setMimeType(final FormSectionEvent event,
                            final String mimeType) {
        event
            .getFormData()
            .put(getMimeTypeWidgetName(), mimeType);
    }

    /**
     * @return the mime type widget
     */
    public SingleSelect getMimeTypeWidget() {
        return mimeWidget;
    }

    /**
     * @return the file upload widget
     */
    public FileUpload getFileUploadWidget() {
        return fileWidget;
    }

    /**
     * @return the parameter name prefix
     */
    public String getParameterPrefix() {
        return parameterPrefix;
    }

    /**
     * @return the file upload parameter name
     */
    public String getFileUploadWidgetName() {
        return parameterPrefix + FILE_UPLOAD;
    }

    /**
     * @return the mime typeparameter name
     */
    public String getMimeTypeWidgetName() {
        return parameterPrefix + MIME_TYPE;
    }

    /**
     * Add mime-type options to the option group by loading all mime types which
     * match a certain prefix from the database
     *
     * @param mimeTypeOptions The mime type widget to which options should be
     * added
     *
     * @param mimePrefix Populate the mime type widget with all mime types that
     * match the prefix. Some of the possible prefixes are "text", "image",
     * "binary", etc.
     *
     */
    public static void addMimeOptions(final OptionGroup mimeTypeOptions,
                                      final String mimePrefix) {

//        MimeTypeCollection types;
//        if (mimePrefix == null || mimePrefix.equals("")) {
//            types = MimeType.getAllMimeTypes();
//        } else {
//            types = MimeType.searchMimeTypes(mimePrefix + "/");
//        }
//        while (types.next()) {
//            MimeType type = types.getMimeType();
//            mimeTypeOptions.addOption(new Option(type.getMimeType(), type.getLabel()));
//        }
    }

}
