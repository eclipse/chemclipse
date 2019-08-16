/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.exceptions;

public class NoFilterAvailableException extends Exception {

	private static final long serialVersionUID = -6571154972789722125L;

	public NoFilterAvailableException() {
		super();
	}

	public NoFilterAvailableException(String message) {
		super(message);
	}
}
