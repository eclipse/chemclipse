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
package org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions;

public class FileIsNotWriteableException extends Exception {

	private static final long serialVersionUID = -8192251127934002970L;

	public FileIsNotWriteableException() {
		super();
	}

	public FileIsNotWriteableException(String message) {
		super(message);
	}
}
