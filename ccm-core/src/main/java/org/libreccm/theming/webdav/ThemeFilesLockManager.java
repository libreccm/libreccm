/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.webdav;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

/**
 * Manages the locks on files for WebDAV.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
class ThemeFilesLockManager {

    /**
     * Mapping between file path and lock token
     */
    private final Map<String, String> lockedFiles = new HashMap<>();

    /**
     * Mapping between lock token and file.
     */
    private final Map<String, String> locks = new HashMap<>();

    /**
     * Lock a file
     *
     * @param file Path of the file to lock.
     *
     * @return The lock token.
     *
     * @throws AlreadyLockedException If the file is already locked.
     */
    protected String lockFile(final String file) throws AlreadyLockedException {

        if (lockedFiles.containsKey(file)) {
            throw new AlreadyLockedException(String.format(
                "File %s is already locked.", file));
        } else {

            final String lockToken = UUID.randomUUID().toString();
            lockedFiles.put(file, lockToken);
            locks.put(lockToken, file);

            return lockToken;
        }
    }

    /**
     * Check if a file is locked.
     *
     * @param file The file to check for a lock.
     *
     * @return An {@link Optional} with the lock token of the file if the file
     *         is locked, an empty {@code Optional} otherwise.
     */
    protected Optional<String> isLocked(final String file) {
        if (lockedFiles.containsKey(file)) {
            return Optional.of(lockedFiles.get(file));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Removes the lock from a file.
     * 
     * @param lockToken The token of the lock to remove.
     */
    protected void unlock(final String lockToken) {

        final String file = locks.get(lockToken);
        locks.remove(lockToken);
        lockedFiles.remove(file);
    }

}
