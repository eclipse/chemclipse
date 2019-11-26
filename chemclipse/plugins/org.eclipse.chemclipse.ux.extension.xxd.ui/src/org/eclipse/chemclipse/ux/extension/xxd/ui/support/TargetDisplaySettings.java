/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.io.File;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public interface TargetDisplaySettings {

	enum LibraryField {
		NAME, CAS, CLASSIFICATION, FORMULA, SYNONYMS;
	}

	boolean showPeakLabels();

	boolean showScanLables();

	LibraryField getField();

	static TargetDisplaySettings getSettings(File file, IPreferenceStore preferenceStore) {

		boolean showChromatogramPeakLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
		boolean showChromatogramScanLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS);
		return new TargetDisplaySettings() {

			@Override
			public boolean showPeakLabels() {

				return showChromatogramPeakLabels;
			}

			@Override
			public boolean showScanLables() {

				return showChromatogramScanLabels;
			}

			@Override
			public LibraryField getField() {

				return LibraryField.NAME;
			}

			@Override
			public boolean isVisible(IIdentificationTarget target) {

				return true;
			}
		};
	}

	boolean isVisible(IIdentificationTarget target);
}
