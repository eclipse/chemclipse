/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;

public class TargetReferencesSupport {

	public static final DecimalFormat DECIMAL_FORMAT_RI = ValueFormat.getDecimalFormatEnglish("0.00");
	public static final DecimalFormat DECIMAL_FORMAT_AREA_PERCENT = ValueFormat.getDecimalFormatEnglish("0.000");
	//
	private static final IPreferenceStore PREFERENCE_STORE = Activator.getDefault().getPreferenceStore();

	public static FontData getPeakFontData() {

		String name = PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		int height = PREFERENCE_STORE.getInt(PreferenceSupplier.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		int style = PREFERENCE_STORE.getInt(PreferenceSupplier.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		//
		return new FontData(name, height, style);
	}

	public static FontData getScanFontData() {

		String name = PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		int height = PREFERENCE_STORE.getInt(PreferenceSupplier.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		int style = PREFERENCE_STORE.getInt(PreferenceSupplier.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		//
		return new FontData(name, height, style);
	}

	public static Color getActiveColor() {

		if(!PreferencesSupport.isDarkTheme())
			return Colors.getColor(PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_ACTIVE_TARGET_LABEL_FONT_COLOR));
		else
			return Colors.getColor(PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_ACTIVE_TARGET_LABEL_FONT_DARK_COLOR));
	}

	public static Color getInactiveColor() {

		if(!PreferencesSupport.isDarkTheme())
			return Colors.getColor(PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_INACTIVE_TARGET_LABEL_FONT_COLOR));
		else
			return Colors.getColor(PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_INACTIVE_TARGET_LABEL_FONT_DARK_COLOR));
	}

	public static Color getIdColor() {

		if(!PreferencesSupport.isDarkTheme())
			return Colors.getColor(PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_ID_TARGET_LABEL_FONT_COLOR));
		else
			return Colors.getColor(PREFERENCE_STORE.getString(PreferenceSupplier.P_CHROMATOGRAM_ID_TARGET_LABEL_FONT_DARK_COLOR));
	}
}