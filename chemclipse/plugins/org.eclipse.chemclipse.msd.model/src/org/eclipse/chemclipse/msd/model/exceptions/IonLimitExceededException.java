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
package org.eclipse.chemclipse.msd.model.exceptions;

import org.eclipse.chemclipse.model.exceptions.ValueLimitExceededException;

public class IonLimitExceededException extends ValueLimitExceededException {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -3071227582762126158L;

	public IonLimitExceededException() {
		super();
	}

	public IonLimitExceededException(String message) {
		super(message);
	}
}
