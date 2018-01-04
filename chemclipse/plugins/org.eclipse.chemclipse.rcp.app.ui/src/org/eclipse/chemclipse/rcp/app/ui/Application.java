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
package org.eclipse.chemclipse.rcp.app.ui;

import java.util.Properties;

import org.eclipse.chemclipse.rcp.app.ui.internal.support.ApplicationSupportCLI;
import org.eclipse.chemclipse.rcp.app.ui.internal.support.ApplicationSupportDefault;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/*
	 * See VM arguments in the product configuration.
	 */
	private static final String D_ENABLE_CLI_SUPPORT = "enable.cli.support";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	public Object start(IApplicationContext context) {

		boolean enableCLISupport = false;
		/*
		 * Get the setting.
		 */
		Properties properties = System.getProperties();
		Object object = properties.get(D_ENABLE_CLI_SUPPORT);
		if(object != null && object instanceof String) {
			enableCLISupport = Boolean.parseBoolean((String)object);
		}
		/*
		 * When using the Apache CLI option, RCPTT doesn't work properly.
		 * In most cases, it's not needed so it is disabled by default.
		 * It can be enabled by modifying the -D option in the *.ini file.
		 * -Denable.cli.support=true
		 */
		if(enableCLISupport) {
			ApplicationSupportCLI applicationSupport = new ApplicationSupportCLI();
			return applicationSupport.start(context);
		} else {
			ApplicationSupportDefault applicationSupport = new ApplicationSupportDefault();
			return applicationSupport.start(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {

		if(!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {

				if(!display.isDisposed())
					workbench.close();
			}
		});
	}
}
