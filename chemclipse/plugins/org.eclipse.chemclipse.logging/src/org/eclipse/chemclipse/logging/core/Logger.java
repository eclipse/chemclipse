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
package org.eclipse.chemclipse.logging.core;

public class Logger extends Category {

	/**
	 * @param clazz
	 */
	@SuppressWarnings("rawtypes")
	protected Logger(Class clazz) {
		super(clazz);
	}

	/**
	 * Returns the logger.
	 * 
	 * @param clazz
	 * @return Logger
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(Class clazz) {

		return new Logger(clazz);
	}
}
