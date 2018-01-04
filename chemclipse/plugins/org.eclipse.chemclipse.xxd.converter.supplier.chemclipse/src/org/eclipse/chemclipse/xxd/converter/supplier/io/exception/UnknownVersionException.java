/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.io.exception;

import java.io.IOException;

public class UnknownVersionException extends IOException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7248123969997448857L;

	public UnknownVersionException() {
	}

	public UnknownVersionException(final String message) {
		super(message);
	}

	public UnknownVersionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnknownVersionException(final Throwable cause) {
		super(cause);
	}
}
