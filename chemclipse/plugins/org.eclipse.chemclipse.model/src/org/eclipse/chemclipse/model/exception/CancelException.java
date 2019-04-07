/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 *
 * Contributors:
 * Alexander Kerner - initial API and implementation
 *******************************************************************************/

package org.eclipse.chemclipse.model.exception;

/**
 *
 * @author Alexander Kerner
 *
 */
public class CancelException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -903919422865740822L;

    public CancelException() {
    }

    public CancelException(final String message) {
	super(message);
    }

    public CancelException(final Throwable cause) {
	super(cause);
    }

    public CancelException(final String message, final Throwable cause) {
	super(message, cause);
    }

    public CancelException(final String message, final Throwable cause, final boolean enableSuppression,
	    final boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }
}
