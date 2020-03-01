/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.ui.admin.applications;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ApplicationInstanceTreeCdiUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ApplicationRepository applicationRepo;
    
    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    public String getTitle(final CcmApplication ofApplication) {
        final CcmApplication application = applicationRepo
            .findById(ofApplication.getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No CcmApplication with ID %d available.",
                        ofApplication.getObjectId()
                    )
                )
            );
        
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        
        final String title;
        if (application.getTitle().hasValue(globalizationHelper
            .getNegotiatedLocale())) {
            title = application.getTitle().getValue(globalizationHelper
                .getNegotiatedLocale());
        } else if (application.getTitle().hasValue(defaultLocale)) {
            title = application.getTitle().getValue(defaultLocale);
        } else if (application.getTitle().hasValue(Locale.getDefault())) {
            title = application.getTitle().getValue(Locale.getDefault());
        } else {
            title = application.getPrimaryUrl();
        }

        return title;
    }

}
