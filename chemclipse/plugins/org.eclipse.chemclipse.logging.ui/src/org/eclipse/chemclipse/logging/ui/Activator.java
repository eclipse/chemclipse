/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.chemclipse.logging.support.PropertiesUtil;
import org.eclipse.chemclipse.logging.ui.support.LoggerSupport;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initLogger();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {

		return plugin;
	}

	/**
	 * Reading the log4j properties.
	 */
	private void initLogger() {

		/*
		 * Load the properties.
		 */
		Properties properties = PropertiesUtil.getLog4jProperties();
		/*
		 * Append
		 */
		properties.setProperty(PropertiesUtil.ROOT_LOGGER_KEY, PropertiesUtil.ROOT_LOGGER_VALUE);
		/*
		 * Configure log4j properties.
		 */
		try {
			PropertyConfigurator.configure(properties);
			LoggerSupport.getInstance().initConsole();
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}
