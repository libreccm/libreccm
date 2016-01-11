/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.globalization;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.LegacyKernelConfig;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class GlobalizationHelper {

    public static final String LANG_INDEPENDENT = LegacyKernelConfig.getConfig().getLanguagesIndependentCode();
    private static final String LANG_PARAM = "lang";
    
    // Don't instantiate
    private GlobalizationHelper() {
    }

    /**
     * This method returns the best matching locate for the request. In contrast to
     * the other methods available this one will also respect the supported_languages
     * config entry.
     *
     * @return The negotiated locale
     */
    public static java.util.Locale getNegotiatedLocale() {
        LegacyKernelConfig kernelConfig = LegacyKernelConfig.getConfig();

        // Set the preferedLocale to the default locale (first entry in the config parameter list)
        java.util.Locale preferedLocale = getPrefferedLocale();

        // The ACCEPTED_LANGUAGES from the client
        Enumeration locales = null;

        // Try to get the RequestContext
        try {

            // Get the SerrvletRequest
            ServletRequest request = ((ServletRequest) DispatcherHelper.getRequest());

            // Get the selected locale from the request, if any
            java.util.Locale selectedLocale = getSelectedLocale(request);
            if (selectedLocale != null && kernelConfig.hasLanguage(selectedLocale.getLanguage())) {
                preferedLocale = selectedLocale;
            } else {

                locales = request.getLocales();

                // For everey element in the enumerator
                while (locales.hasMoreElements()) {

                    // Test if the current locale is listed in the supported locales list
                    java.util.Locale curLocale = (Locale) locales.nextElement();
                    if (kernelConfig.hasLanguage(curLocale.getLanguage())) {
                        preferedLocale = curLocale;
                        break;
                    }
                }
            }
        } catch (NullPointerException ex) {
            // Don't have to do anything because I want to fall back to default language anyway
            // This case should only appear during setup
        } finally {

            return preferedLocale;

        }
    }

//    public static java.util.Locale getSystemLocale() {
//        
//    }
    private static Locale getPrefferedLocale() {
        LegacyKernelConfig kernelConfig = LegacyKernelConfig.getConfig();
        java.util.Locale preferedLocale = new java.util.Locale(kernelConfig.getDefaultLanguage(), "", "");
        return preferedLocale;
    }

    /**
     * Get the selected (as in fixed) locale from the ServletRequest
     * 
     * @return the selected locale as java.util.Locale or null if not defined
     */
    public static Locale getSelectedLocale(ServletRequest request) {
        
        // Return value
        java.util.Locale selectedLocale = null;

        // Access current HttpSession or create a new one, if none exist
        HttpSession session = ((HttpServletRequest) request).getSession(true);
        // Get the session stored language string
        String selectedSessionLang = (String) session.getAttribute(LANG_PARAM);
        // Get the request langauge string
        String selectedRequestLang = request.getParameter(LANG_PARAM);

        // If there is a request language string, then this will have priority
        // because this will only be the case, if someone selected another
        // language with the language selector
        if(selectedRequestLang != null) {
            // Get the Locale object for the param
            if((selectedLocale = scanLocale(selectedRequestLang)) != null) {
                // Save the request parameter as session value
                session.setAttribute(LANG_PARAM, selectedRequestLang);
            }
        } else {
            // If there is a session stored language, use it
            if(selectedSessionLang != null) {
                selectedLocale = scanLocale(selectedSessionLang);
            }
        }
        
        return selectedLocale;
    }
    
    /**
     * Create a Locale from a browser provides language string
     * 
     * @param lang A string encoded locale, as provided by browsers
     * @return A java.util.Locale representation of the language string
     */
    private static java.util.Locale scanLocale(String lang) {
        
        // Protect against empty lang string
        if ((lang != null) && !(lang.isEmpty())) {
            // Split the string and create the Locale object
            StringTokenizer paramValues = new StringTokenizer(lang, "_");
            if (paramValues.countTokens() > 1) {
                return new java.util.Locale(paramValues.nextToken(), paramValues.nextToken());
            } else {
                return new java.util.Locale(paramValues.nextToken());
            }
        }
        
        return null;
    }
    
}
