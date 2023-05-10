/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui;

import org.eclipse.chemclipse.rcp.app.ui.console.MessageConsoleSupport;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class PluginStartup implements IStartup {

	@Override
	public void earlyStartup() {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				MessageConsoleSupport consoleSupport = new MessageConsoleSupport();
				consoleSupport.printConfiguration();
			}
		});
	}
}