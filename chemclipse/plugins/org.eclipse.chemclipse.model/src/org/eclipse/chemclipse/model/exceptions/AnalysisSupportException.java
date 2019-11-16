/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - change checked to runtime exception
 *******************************************************************************/
package org.eclipse.chemclipse.model.exceptions;

public class AnalysisSupportException extends IllegalArgumentException {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 134049248548566712L;

	public AnalysisSupportException() {
		super();
	}

	public AnalysisSupportException(String message) {
		super(message);
	}
}
