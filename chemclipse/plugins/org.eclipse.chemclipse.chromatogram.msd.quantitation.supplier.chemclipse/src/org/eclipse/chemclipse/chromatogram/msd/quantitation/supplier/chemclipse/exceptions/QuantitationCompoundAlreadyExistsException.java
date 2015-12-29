/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions;

public class QuantitationCompoundAlreadyExistsException extends Exception {

	/**
	 * Renew this uid on each change.
	 */
	private static final long serialVersionUID = -3414105831762922439L;

	public QuantitationCompoundAlreadyExistsException() {
		super();
	}

	public QuantitationCompoundAlreadyExistsException(String message) {
		super(message);
	}
}
