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
package org.eclipse.chemclipse.converter.exceptions;

import java.io.IOException;

public class FileIsEmptyException extends IOException {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or methods.
	 */
	private static final long serialVersionUID = -782589424405249949L;

	public FileIsEmptyException() {
		super();
	}

	public FileIsEmptyException(final String message) {
		super(message);
	}
}
