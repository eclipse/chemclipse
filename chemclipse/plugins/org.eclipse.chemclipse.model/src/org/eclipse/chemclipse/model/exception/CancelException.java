/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Kerner - initial API and implementation
 * Philip Wenig - wrong license, it has been adjusted to EPL
 *******************************************************************************/
package org.eclipse.chemclipse.model.exception;

/**
 *
 * @author Alexander Kerner
 *
 */
public class CancelException extends RuntimeException {

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

	public CancelException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {

		super(message, cause, enableSuppression, writableStackTrace);
	}
}