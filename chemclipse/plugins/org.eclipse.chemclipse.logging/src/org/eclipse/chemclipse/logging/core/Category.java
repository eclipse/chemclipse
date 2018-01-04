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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Category {

	private final Logger logger;

	@SuppressWarnings("rawtypes")
	public Category(final Class clazz) {
		logger = Logger.getLogger(clazz);
	}

	public void debug(final Object message) {

		logger.log(Category.class.getName(), Level.DEBUG, message, null);
	}

	public void debug(final Object message, Throwable t) {

		logger.log(Category.class.getName(), Level.DEBUG, message, t);
	}

	public void error(final Object message) {

		logger.log(Category.class.getName(), Level.ERROR, message, null);
	}

	public void error(final Object message, Throwable t) {

		logger.log(Category.class.getName(), Level.ERROR, message, t);
	}

	public void fatal(final Object message) {

		logger.log(Category.class.getName(), Level.FATAL, message, null);
	}

	public void fatal(final Object message, Throwable t) {

		logger.log(Category.class.getName(), Level.FATAL, message, t);
	}

	public void info(final Object message) {

		logger.log(Category.class.getName(), Level.INFO, message, null);
	}

	public void info(final Object message, Throwable t) {

		logger.log(Category.class.getName(), Level.INFO, message, t);
	}

	public void trace(final Object message) {

		logger.log(Category.class.getName(), Level.TRACE, message, null);
	}

	public void trace(final Object message, Throwable t) {

		logger.log(Category.class.getName(), Level.TRACE, message, t);
	}

	public void warn(final Object message) {

		logger.log(Category.class.getName(), Level.WARN, message, null);
	}

	public void warn(final Object message, Throwable t) {

		logger.log(Category.class.getName(), Level.WARN, message, t);
	}
}
