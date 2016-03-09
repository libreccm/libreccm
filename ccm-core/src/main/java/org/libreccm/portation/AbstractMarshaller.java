/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.portation;

import org.apache.log4j.Logger;
import org.libreccm.core.Identifiable;

import java.io.File;
import java.util.List;

/**
 * Abstract class responsible for ex- and importing entity-objects to several
 * file-formats. Every entity-class (e.g. DocRepo.File) needs to have its own
 * extension of this class to override the abstract methods, making it
 * possible to ex- or import that extending entity-class (e.g. DocRepo
 * .FileMarshal).
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 2/10/16
 */
public abstract class AbstractMarshaller<I extends Identifiable> {

    private static final Logger log = Logger.getLogger(AbstractMarshaller.class);

    private Format format;

    private File exportFile;
    private List<File> importFiles;

    // CSV specifics

    // JSON specifics

    // XML specifics

    /**
     *
     * @param format
     */
    private void init(final Format format) {
        this.format = format;

        switch (this.format) {
            case CSV:
                break;

            case JSON:
                break;

            case XML:
                break;

            default:
                break;
        }
    }

    public void init(final Format format, final String filename) {
        exportFile = new File(filename);
        init(format);
    }

    public void init(final Format format, final List<String> filenames) {
        filenames.forEach(fname -> importFiles.add(new File(fname)));
        init(format);
    }


    /**
     *
     * @param exportObjects List of {@code T}-tpyed objects being exported
     */
    public void exportEntities(final List<I> exportObjects) {
        switch (format) {
            case CSV:
                break;

            case JSON:
                break;

            case XML:
                break;

            default:
                break;
        }

    }

    /**
     *
     * @return List of {@code T}-typed objects being imported
     */
    public List<I> importEntities() {
        switch (format) {
            case CSV:
                break;

            case JSON:
                break;

            case XML:
                break;

            default:
                break;
        }
        return null;
    }

}
