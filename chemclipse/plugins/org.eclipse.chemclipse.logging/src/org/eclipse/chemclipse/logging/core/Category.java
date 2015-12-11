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

	private final Logger logger;

	@SuppressWarnings("rawtypes")
	public Category(final Class clazz) {

		logger = Logger.getLogger(clazz);
	}

	public void debug(final Object message) {

		logger.debug(message);
	}

	public void debug(final Object message, Throwable t) {

		logger.debug(message, t);
	}

	public void error(final Object message) {

		logger.error(message);
	}

	public void error(final Object message, Throwable t) {

		logger.error(message, t);
	}

	public void fatal(final Object message) {

		logger.fatal(message);
	}

	public void fatal(final Object message, Throwable t) {

		logger.fatal(message, t);
	}

	public void info(final Object message) {

		logger.info(message);
	}

	public void info(final Object message, Throwable t) {

		logger.info(message, t);
	}

	public void trace(final Object message) {

		logger.trace(message);
	}

	public void trace(final Object message, Throwable t) {

		logger.trace(message, t);
	}

	public void warn(final Object message) {

		logger.warn(message);
	}

	public void warn(final Object message, Throwable t) {

		logger.warn(message, t);
	}
}
