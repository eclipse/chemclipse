/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui;

import org.eclipse.chemclipse.support.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivatorUI {

	public static final String ICON_FOLDER_OPENED = "ICON_FOLDER_OPENED"; // $NON-NLS-1$
	public static final String ICON_FOLDER_CLOSED = "ICON_FOLDER_CLOSED"; // $NON-NLS-1$
	public static final String ICON_FILE = "ICON_FILE"; // $NON-NLS-1$
	/*
	 * Instance
	 */
	private static Activator plugin;
	private static BundleContext context;

	public static BundleContext getContext() {

		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		Activator.context = context;
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		/*
		 * Don't initialize the image registry.
		 * The plug-in crashed when running the unit tests.
		 * To be honest, I don't understand that.
		 */
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	public static AbstractActivatorUI getDefault() {

		return plugin;
	}
}