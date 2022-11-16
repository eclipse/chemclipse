/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.exception;

public class MathIllegalArgumentException extends IllegalArgumentException {

	private static final long serialVersionUID = 9006355266314003863L;

	public MathIllegalArgumentException() {

		super();
	}

	public MathIllegalArgumentException(String message, Throwable cause) {

		super(message, cause);
	}

	public MathIllegalArgumentException(String s) {

		super(s);
	}

	public MathIllegalArgumentException(Throwable cause) {

		super(cause);
	}
}
