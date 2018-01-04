/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.support;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class LoggerSupport {

	private static LoggerSupport loggerSupport;

	/*
	 * This class is a singleton.
	 */
	private LoggerSupport() {
	}

	/**
	 * Returns a LoggerSupport instance.
	 * 
	 * @return
	 */
	public static synchronized LoggerSupport getInstance() {

		if(loggerSupport != null) {
			return loggerSupport;
		}
		loggerSupport = new LoggerSupport();
		return loggerSupport;
	}

	/**
	 * Initialize the message console.
	 * 
	 * @return
	 */
	public MessageConsole initConsole() {

		String systemConsoleName = "ChemClipse Logging";
		/*
		 * Get the console plugin.
		 */
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		/*
		 * Return an existing console.
		 */
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		IConsole[] existing = consoleManager.getConsoles();
		for(int i = 0; i < existing.length; i++) {
			if(systemConsoleName.equals(existing[i].getName())) {
				/*
				 * Activate the console.
				 */
				((MessageConsole)existing[i]).activate();
				return (MessageConsole)existing[i];
			}
		}
		/*
		 * Create a new console if not exists.
		 */
		MessageConsole messageConsole = new MessageConsole(systemConsoleName, null);
		IConsole[] consoles = new IConsole[]{messageConsole};
		consoleManager.addConsoles(consoles);
		return messageConsole;
	}
}
