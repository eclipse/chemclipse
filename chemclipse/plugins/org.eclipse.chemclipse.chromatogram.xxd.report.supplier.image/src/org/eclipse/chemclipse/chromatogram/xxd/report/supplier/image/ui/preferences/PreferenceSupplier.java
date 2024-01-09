/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - preference initializer
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.ImageFormat;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.ImageReportSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

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
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_APPEND_FILES, Boolean.toString(DEF_APPEND_FILES));
		putDefault(P_WIDTH, Integer.toString(DEF_WIDTH));
		putDefault(P_HEIGHT, Integer.toString(DEF_HEIGHT));
		putDefault(P_ADD_PEAKS, Boolean.toString(DEF_ADD_PEAKS));
		putDefault(P_ADD_SCANS, Boolean.toString(DEF_ADD_SCANS));
		putDefault(P_FORMAT, DEF_FORMAT);
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

		return INSTANCE().getBoolean(P_APPEND_FILES, DEF_APPEND_FILES);
	}

	public static int getWidth() {

		return INSTANCE().getInteger(P_WIDTH, DEF_WIDTH);
	}

	public static int getHeight() {

		return INSTANCE().getInteger(P_HEIGHT, DEF_HEIGHT);
	}

	public static boolean isPeaks() {

		return INSTANCE().getBoolean(P_ADD_PEAKS, DEF_ADD_PEAKS);
	}

	public static boolean isScans() {

		return INSTANCE().getBoolean(P_ADD_SCANS, DEF_ADD_SCANS);
	}

	public static ImageFormat getFormat() {

		return ImageFormat.valueOf(INSTANCE().get(P_FORMAT, DEF_FORMAT));
	}
}