/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.exceptions;

public class NoChromatogramClassifierSupplierAvailableException extends Exception {

	private static final long serialVersionUID = -2019887628389096078L;

	public NoChromatogramClassifierSupplierAvailableException() {

		super();
	}

	public NoChromatogramClassifierSupplierAvailableException(String message) {

		super(message);
	}
}
