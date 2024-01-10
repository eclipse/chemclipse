/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.report.Activator;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_REPORT_EXPORT_FOLDER = "reportExportFolder";
	public static final String DEF_REPORT_EXPORT_FOLDER = "";

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_REPORT_EXPORT_FOLDER, DEF_REPORT_EXPORT_FOLDER);
	}

	public static String getReportExportFolder() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_REPORT_EXPORT_FOLDER, DEF_REPORT_EXPORT_FOLDER);
	}
}
