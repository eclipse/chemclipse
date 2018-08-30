/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.exceptions;

public class ValueMustNotBeNullException extends RuntimeException {

	public ValueMustNotBeNullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ValueMustNotBeNullException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValueMustNotBeNullException(Throwable cause) {
		super(cause);
	}

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 6845549285214123241L;

	public ValueMustNotBeNullException() {
		super();
	}

	public ValueMustNotBeNullException(String message) {
		super(message);
	}
}
