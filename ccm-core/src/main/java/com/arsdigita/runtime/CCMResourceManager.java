/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 * Copyright (C) 2009 Peter Boy (pb@zes.uni-bremen.de) All Rights Reserved.
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
package com.arsdigita.runtime;

import com.arsdigita.util.UncheckedWrapperException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <p>CCMResourceManager Runtime environment repository object, stores essential
 * properties of the runtime environment and provides them on request.</p>
 * 
 * <p>Currently, is is limited to the </p>
 * <ul>
 * <li>base directory of the running webapp</li>
 * </ul>
 * <p>It provides public methods to make the properties available.</p>
 *
 * <p>The singleton is initialised</p>
 * <ul>
 * <li>either during startup of the container (called by 
 * @see com.arsdigita.web.CCMApplicationContextListener (must be configured in
 * web.xml)</li>
 * <li>or by the command line system at the beginning of the processing (esp.
 * package @see com.arsdigita.packaging.Mastertool).</li>
 * </ul>
 * <p>Currently as a fall back mechanism the environmant Variable CCM_HOME is
 * evaluated and used a last resort, if no initialisation has been done when
 * a getter is first called.</p>
 *
 * <p>It is essential for the proper working of CCM that CCMResourceManager is
 * initialised before any operation starts, as it is the case with the Startup
 * class of the runtime package, which is responsible for organising database
 * access.</p>
 *
 *
 * <p><b>Subject to change!</b></p>
 * 
 * A similiar task is performed by com.arsdigita.util.ResourceManager
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * rewritten by
 * @author pboy &lt;pboy@barkhof.uni-bremen.de&gt;
 */
public final class CCMResourceManager {

    private static final Logger LOGGER = LogManager.getLogger(CCMResourceManager.class);

    private static CCMResourceManager s_ccm;

    /**
     * Full Pathname of the application base directory
     * (document root in apache terminology)
     */
    private static File m_baseDir;


    /**
     * Location of the registry in the applications directory tree
     * as offset from the base directory
     */
    // currently not used, should be refactored as File object for the sake of
    // operating system independency!
    // public static final String registryPath = "/WEB-INF/conf/registry";
    // public static final File registryPath = null;  // currently not used, work in progress

    /* ************     Section  singleton handlers   ***************** */

    /**
     * Sets the singleton configuration property for the runtime
     * environment.
     *
     */
    public static final synchronized void setBaseDirectory(String baseDirName) {
        if (s_ccm == null) {
            s_ccm = new CCMResourceManager();
            s_ccm.storeBaseDir(baseDirName);
        }
        else {
            // baseDir already set, silently discard
            LOGGER.info("baseDir already set as " + m_baseDir + ". Discarded.");
        }
    }

    /**
     * Returns the singleton configuration property for the runtime
     * environment.
     *
     * @return The <code>RuntimeConfig</code> record; it cannot be null
     */
    public static final synchronized File getBaseDirectory() {
        if (s_ccm == null) {
            // should never happen, setBaseDirectory has to be executed first
            // we try to resolve the problem in fetchBaseDir by search for
            // a runtime environment variable (the old way).
            s_ccm = new CCMResourceManager();
        }

        return s_ccm.fetchBaseDir();
    }

    /* ************     Section  singleton handlers   END   ************** */


    /* ************          Constructors Section           ************** */

    /**
     * Following the singleton pattern: Private constructor to prevent other
     * clients from instantiating the class (and the compiler from generating
     * a default public constructor).
     */
    private CCMResourceManager() { }

    /* ************      Constructors Section  END          ************** */


    /* ************     Public getter/setter  Section      *************** */

    /**
     * Retrieve the homeDir as URL.
     *
     * Note! API changed. @see getHomeDirectory()
     * <b>May be removed in the future!</b>
     *
     * @return Directory location in the servers file system as URL object.
     */
    public static final URL getHomeURL() {
        try {
            return CCMResourceManager.getHomeDirectory().toURL();
        } catch (MalformedURLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    /**
     * Retrieve the homeDir, which is the location of the servlet server's
     * servlet container directory in the file system of the server machine,
     * as File object.
     *
     * <b>Note! API changed!</b>
     *
     * Originally it is used to determine all file object locations of a
     * CCM installation, during the installation step as well as 
     * while running the application inside a servlet container. The CCM installation
     * of a servlet container used to use a non-standard layout. It is based upon a
     * system wide environment variable CCM_HOME to determine the home directory.
     *
     * The dependency from a system wide environment variable prevents a servlet
     * container to run multiple instances of CCM. In addition to it CCM will
     * be migrated to be installable in a standard way to a standard container.
     * Therefore all file locations will be given relative to the applications
     * directory (the baseDirectory @see m_baseDir).
     *
     *
     * Method getHomeDirectory() is preserved during the transition phase.
     * <b>It may be removed in the future!</b> Or it may be moved to
     * c.ad.packaging for assistence of the installation step only.
     *
     * MODIFIED:
     * CCM_HOME is now interpreted as the path to the applications base
     * directory (web application context).
     * 
     * @return Directory location in the servers file system as File object.
     */
    static final File getHomeDirectory() {

        String home = System.getProperty("ccm.home");

        if (home == null) {
            throw new IllegalStateException
                ("The ccm.home system property is null or not defined");
        }

        // make a guess, wether it is old style (i.e. referring to the containers
        // base directory and therefor does not contain the webapps part) or
        // new style referring to the apps base directory (and therefor containing
        // the webapps part)
        if (home.indexOf("webapps") > 0 ){
            // should be new style
        }
        else {
            // presumably old style, add path to standard context name
            home += "/webapps/ROOT";
        }

        File file = new File(home);

//      No need to require that home exists (indeed, during install it will not).
//      Should be created by invoking method if not.
//      if (!file.exists()) {
//          throw new IllegalStateException
//              ("The file given in the ccm.home system property " +
//               "does not exist");
//      }

        if (!file.isDirectory()) {
            throw new IllegalStateException
                ("The file: " + home + " given in the ccm.home system property"
                                     + " is not a directory");
        }

        return file;
    }

    /**
     * Provide the configDirectory as URL.
     *
     * @see getConfigDirectory() for details.
     *
     * <b>Note! API changed!</b>
     * 
     * @return Directory location in the servers file system as URL object.
     */
    public static final URL getConfigURL() {
        try {
            return CCMResourceManager.getConfigDirectory().toURL();
        } catch (MalformedURLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    /**
     * Retrieve the configDir, which is the location of the configuration
     * database root (registry) in the file system tree of the server machine,
     * as File object.

     * 
     * @return Directory location in the servers file system as File object.
     */
    public static final File getConfigDirectory() {

        // Keep this in sync with informational attribut @see registryPath !
        File confdir = new File(new File(new File(CCMResourceManager.getBaseDirectory(),
                                             "WEB-INF"),"conf"), "registry");

        if (!confdir.exists()) {
            if (!confdir.mkdirs()) {
                throw new IllegalStateException
                    ("Could not create configuration directory: " + confdir);
            }
        }
        if (!confdir.isDirectory()) {
            throw new IllegalStateException
                ("Configuration directory value is not a directory: " + confdir);
        }

        return confdir;
    }

    /**
     * getWorkDirectory retrieves and eventually creates an internal directory
     * in the servlet container for temporary files (work files), where subsystems
     * may create subdirectories for internal use (e.g. Lucene search enginge or
     * the PublishToFile machinery).
     *
     * The containers work file directory could be used as well, but may be
     * inappropriate in case of confidential data.
     *
     * @return Directory location in the servers file system as File object.
     */
    public static final File getWorkDirectory() {
        File file = new File(new File(CCMResourceManager.getBaseDirectory(),
                             "WEB-INF"),"work");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IllegalStateException
                    ("Could not create work directory: " + file);
            }
        }
        if (!file.isDirectory()) {
            throw new IllegalStateException
                ("Work directory value is not a directory: " + file);
        }
        return file;
    }

    /* ************   Public getter/setter  Section END    *************** */


    /* ************        Private Worker  Section         *************** */

    /**
     * Stores the passed in String as File object.
     *
     * @param baseDirName String containing the path, must not be null
     */
    private final void storeBaseDir(String baseDirName) {

        LOGGER.debug("storeBaseDir: BaseDir name is given as " + baseDirName );
        m_baseDir = new File(baseDirName);

        // eventually: check if dir exists, create it if not.
        if (!m_baseDir.exists()) {
            if (!m_baseDir.mkdirs()) {
                throw new IllegalStateException
                    ("Could not create base directory: " + m_baseDir);
            }
        }
        if (!m_baseDir.isDirectory()) {
            throw new IllegalStateException
                ("Base directory value is not a directory: " + m_baseDir);
        }

    }

    /**
     * Retrieves the stored BaseDir File object.
     *
     * @return Base directory location in the servers file system as File object.
     */
    private final File fetchBaseDir() {

        if (m_baseDir == null) {
            // should never happen, but we try to cope with it anyway by
            // falling back to getHomeDirectory() and the system wide
            // environment variable.
            // During transition phase only! Must be removed when the new
            // standard compliant installation method is fully in place
            // MODIFIED
            // HomeDirectory now specifies the applications context dir.
            m_baseDir = CCMResourceManager.getHomeDirectory();

            // eventually: check if dir exists, create it if not.
            if (!m_baseDir.exists()) {
                if (!m_baseDir.mkdirs()) {
                    throw new IllegalStateException
                        ("Could not create base directory: " + m_baseDir);
                }
            }
            if (!m_baseDir.isDirectory()) {
                throw new IllegalStateException
                    ("Base directory value is not a directory: " + m_baseDir);
            }
        }

        return  m_baseDir;

    }

    /* ************     Private  Worker  Section  END      *************** */


}
