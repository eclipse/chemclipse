/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.container.definition;

public class Container {

	/*
	 * These are the attributes of the extension point elements.
	 */
	public static final String ID = "id"; //$NON-NLS-1$
	public static final String MAGIC_NUMBER_MATCHER = "magicNumberMatcher"; //$NON-NLS-1$
	public static final String GET_CONTENTS = "getContents"; //$NON-NLS-1$

	private Container() {

		// static methods only
	}
}
