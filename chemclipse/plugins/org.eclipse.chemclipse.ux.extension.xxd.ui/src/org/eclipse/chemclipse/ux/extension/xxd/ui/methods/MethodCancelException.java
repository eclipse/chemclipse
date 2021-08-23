/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

public class MethodCancelException extends Exception {

	private static final long serialVersionUID = 1373953878031223890L;

	public MethodCancelException() {

		super();
	}

	public MethodCancelException(String message) {

		super(message);
	}
}