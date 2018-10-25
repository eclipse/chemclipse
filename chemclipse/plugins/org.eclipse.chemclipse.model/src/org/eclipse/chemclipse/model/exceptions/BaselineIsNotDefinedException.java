/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.exceptions;

public class BaselineIsNotDefinedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 825282295872083616L;

	public BaselineIsNotDefinedException() {

		super();
	}

	public BaselineIsNotDefinedException(String message) {

		super(message);
	}
}
