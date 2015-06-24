/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.core;

import org.apache.log4j.Logger;

public class Category {

	private Logger logger;

	@SuppressWarnings("rawtypes")
	public Category(Class clazz) {

		logger = Logger.getLogger(clazz);
	}

	/**
	 * @param message
	 */
	public void trace(Object message) {

		logger.trace(message);
	}

	/**
	 * @param message
	 */
	public void debug(Object message) {

		logger.debug(message);
	}

	/**
	 * @param message
	 */
	public void info(Object message) {

		logger.info(message);
	}

	/**
	 * @param message
	 */
	public void warn(Object message) {

		logger.warn(message);
	}

	/**
	 * @param message
	 */
	public void error(Object message) {

		logger.error(message);
	}

	/**
	 * @param message
	 */
	public void fatal(Object message) {

		logger.fatal(message);
	}
}
