/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.exceptions;

public class NoIdentifiedScansAvailableException extends Exception {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -6185021897801072977L;

	public NoIdentifiedScansAvailableException() {
		super();
	}

	public NoIdentifiedScansAvailableException(String message) {
		super(message);
	}
}
