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

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.chemclipse.logging.ui.Activator;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleAppender extends AppenderSkeleton {

	protected IConsoleManager consoleManager;
	protected String symbolicName;
	protected String logLevel = "ERROR";

	@Override
	protected void append(LoggingEvent arg0) {

		if(consoleManager == null) {
			consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			symbolicName = Activator.getDefault().getBundle().getSymbolicName();
		}
		/*
		 * Check the layout.
		 */
		if(layout == null) {
			return;
		}
		/*
		 * Append the message.
		 */
		StringBuffer message = new StringBuffer();
		message.append(layout.format(arg0));
		/*
		 * Check the level.
		 */
		Level level = arg0.getLevel();
		if(level.toInt() >= Level.toLevel(logLevel).toInt()) {
			/*
			 * Write to each opened console.
			 */
			IConsole[] existing = consoleManager.getConsoles();
			for(int i = 0; i < existing.length; i++) {
				MessageConsoleStream stream = ((MessageConsole)existing[i]).newMessageStream();
				try {
					stream.write(message.toString().getBytes());
					stream.flush();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {

		consoleManager = null;
		symbolicName = null;
	}

	public boolean requiresLayout() {

		return true;
	}

	public String getLogLevel() {

		return logLevel;
	}

	public void setLogLevel(String logLevel) {

		this.logLevel = logLevel;
	}
}
