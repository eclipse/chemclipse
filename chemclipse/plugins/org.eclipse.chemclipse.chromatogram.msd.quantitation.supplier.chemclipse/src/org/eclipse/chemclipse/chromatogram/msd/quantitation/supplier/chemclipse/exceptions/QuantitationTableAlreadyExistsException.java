/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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

public class QuantitationTableAlreadyExistsException extends Exception {

	/**
	 * Renew this uid on each change.
	 */
	private static final long serialVersionUID = -654372555624344439L;

	public QuantitationTableAlreadyExistsException() {
		super();
	}

	public QuantitationTableAlreadyExistsException(String message) {
		super(message);
	}
}
