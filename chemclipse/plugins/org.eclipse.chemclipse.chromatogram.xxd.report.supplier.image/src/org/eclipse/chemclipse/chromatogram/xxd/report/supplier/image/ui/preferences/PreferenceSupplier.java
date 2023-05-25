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
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.ImageFormat;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.ImageReportSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_APPEND_FILES = "appendFiles";
	public static final boolean DEF_APPEND_FILES = false;
	public static final String P_WIDTH = "width";
	public static final int DEF_WIDTH = 1920;
	public static final String P_HEIGHT = "height";
	public static final int DEF_HEIGHT = 1080;
	public static final String P_ADD_PEAKS = "addPeaks";
	public static final boolean DEF_ADD_PEAKS = false;
	public static final String P_ADD_SCANS = "addScans";
	public static final boolean DEF_ADD_SCANS = false;
	public static final String P_FORMAT = "imageFormat";
	public static final String DEF_FORMAT = ImageFormat.PNG.name();
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<>();
		defaultValues.put(P_APPEND_FILES, Boolean.toString(DEF_APPEND_FILES));
		defaultValues.put(P_WIDTH, Integer.toString(DEF_WIDTH));
		defaultValues.put(P_HEIGHT, Integer.toString(DEF_HEIGHT));
		defaultValues.put(P_ADD_PEAKS, Boolean.toString(DEF_ADD_PEAKS));
		defaultValues.put(P_ADD_SCANS, Boolean.toString(DEF_ADD_SCANS));
		defaultValues.put(P_FORMAT, DEF_FORMAT);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ImageReportSettings getReportSettings() {

		ImageReportSettings settings = new ImageReportSettings();
		settings.setWidth(getWidth());
		settings.setHeight(getHeight());
		settings.setPeaks(isPeaks());
		settings.setScans(isScans());
		settings.setFormat(getFormat());
		return settings;
	}

	public static boolean isAppendFiles() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_APPEND_FILES, DEF_APPEND_FILES);
	}

	public static int getWidth() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_WIDTH, DEF_WIDTH);
	}

	public static int getHeight() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_HEIGHT, DEF_HEIGHT);
	}

	public static boolean isPeaks() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_ADD_PEAKS, DEF_ADD_PEAKS);
	}

	public static boolean isScans() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_ADD_SCANS, DEF_ADD_SCANS);
	}

	public static ImageFormat getFormat() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return ImageFormat.valueOf(preferences.get(P_FORMAT, DEF_FORMAT));
	}
}
