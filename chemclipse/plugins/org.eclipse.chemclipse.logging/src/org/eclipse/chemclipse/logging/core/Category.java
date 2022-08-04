/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

public class Category {

	private final ILog logger;

	@SuppressWarnings("rawtypes")
	public Category(final Class clazz) {

		logger = Platform.getLog(clazz);
	}

	public void error(final String message) {

		logger.error(message);
	}

	public void error(Throwable t) {

		if(t != null) {
			logger.error(t.getMessage(), t);
		}
	}

	public void error(final String message, Throwable t) {

		logger.error(message, t);
	}

	public void info(Object object) {

		logger.info(object.toString());
	}

	public void info(final String message) {

		logger.info(message);
	}

	public void info(Throwable t) {

		if(t != null) {
			logger.info(t.getMessage(), t);
		}
	}

	public void info(final String message, Throwable t) {

		logger.info(message, t);
	}

	public void warn(final String message) {

		logger.warn(message);
	}

	public void warn(final String message, Throwable t) {

		logger.warn(message, t);
	}

	public void warn(Throwable t) {

		if(t != null) {
			logger.warn(t.getMessage(), t);
		}
	}
}
