/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui;

import java.util.Map;

import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The shared instance
	private static Activator plugin;
	private ScopedPreferenceStore preferenceStoreChromatogram = null;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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

	public IPreferenceStore getPreferenceStoreChromatogram() {

		// Create the preference store lazily.
		if(preferenceStoreChromatogram == null) {
			IPreferenceSupplier preferenceSupplier = PreferenceSupplier.INSTANCE();
			preferenceStoreChromatogram = new ScopedPreferenceStore(preferenceSupplier.getScopeContext(), preferenceSupplier.getPreferenceNode());
			Map<String, String> initializationEntries = preferenceSupplier.getDefaultValues();
			for(Map.Entry<String, String> entry : initializationEntries.entrySet()) {
				preferenceStoreChromatogram.setDefault(entry.getKey(), entry.getValue());
			}
		}
		return preferenceStoreChromatogram;
	}
}
