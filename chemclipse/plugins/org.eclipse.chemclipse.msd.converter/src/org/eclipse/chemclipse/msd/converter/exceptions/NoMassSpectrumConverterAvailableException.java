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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.exceptions;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;

public class NoMassSpectrumConverterAvailableException extends NoConverterAvailableException {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -7732390162073307409L;

	public NoMassSpectrumConverterAvailableException() {
		super();
	}

	public NoMassSpectrumConverterAvailableException(final String message) {
		super(message);
	}
}
