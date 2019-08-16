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
package org.eclipse.chemclipse.progress.ui;

import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.progress.ui.internal.core.UIStatusLineLogger;

import org.eclipse.ui.IStartup;

/**
 * @author eselmeister
 */
public class PluginStartup implements IStartup {

	private static UIStatusLineLogger uiStatusLineLogger;

	@Override
	public void earlyStartup() {

		/*
		 * Create a new ui status line logger instance and add it as a listener
		 * to StatusLineLogger.
		 */
		uiStatusLineLogger = new UIStatusLineLogger();
		StatusLineLogger.add(uiStatusLineLogger);
	}

	/**
	 * Returns the instance of ui status line logger or null if not exists.
	 * 
	 * @return
	 */
	public static UIStatusLineLogger getUIStatusLineLogger() {

		return uiStatusLineLogger;
	}
}
