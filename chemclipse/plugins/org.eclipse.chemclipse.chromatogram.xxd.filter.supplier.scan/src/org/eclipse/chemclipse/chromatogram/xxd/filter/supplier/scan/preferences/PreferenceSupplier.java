/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.model.ScanSelectorOption;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsCleaner;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsDeleteIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsDuplicator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsRemover;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsRetentionIndexSelector;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsScanMerger;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsScanSelector;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final Character REMOVE_SIGN = 'X';
	public static final Character PRESERVE_SIGN = 'O';
	//
	public static final double MIN_SCAN_SELECTOR_VALUE = 0.0d;
	public static final double MAX_SCAN_SELECTOR_VALUE = Double.MAX_VALUE;
	//
	public static final String P_REMOVER_PATTERN = "removerPattern";
	public static final String DEF_REMOVER_PATTERN = "XO";
	public static final String CHECK_REMOVER_PATTERN = "^[OX]+";
	//
	public static final String P_SCAN_SELECTOR_OPTION = "scanSelectorOption";
	public static final String DEF_SCAN_SELECTOR_OPTION = ScanSelectorOption.RETENTION_TIME_MS.name();
	public static final String P_SCAN_SELECTOR_VALUE = "scanSelectorValue";
	public static final double DEF_SCAN_SELECTOR_VALUE = 1000.0d;
	public static final String P_MERGE_SCANS = "mergeScans";
	public static final boolean DEF_MERGE_SCANS = true;
	public static final String P_CLIP_SCAN_NUMBER_PATTERN = "clipScanNumberPattern";
	public static final String DEF_CLIP_SCAN_NUMBER_PATTERN = "1";

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN);
		putDefault(P_SCAN_SELECTOR_OPTION, DEF_SCAN_SELECTOR_OPTION);
		putDefault(P_SCAN_SELECTOR_VALUE, Double.toString(DEF_SCAN_SELECTOR_VALUE));
		putDefault(P_MERGE_SCANS, Boolean.toString(DEF_MERGE_SCANS));
		putDefault(P_CLIP_SCAN_NUMBER_PATTERN, DEF_CLIP_SCAN_NUMBER_PATTERN);
	}

	public static FilterSettingsCleaner getCleanerFilterSettings() {

		return new FilterSettingsCleaner();
	}

	public static FilterSettingsRemover getRemoverFilterSettings() {

		FilterSettingsRemover filterSettings = new FilterSettingsRemover();
		filterSettings.setScanRemoverPattern(INSTANCE().get(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN));
		//
		return filterSettings;
	}

	public static FilterSettingsDuplicator getDuplicatorFilterSettings() {

		FilterSettingsDuplicator settings = new FilterSettingsDuplicator();
		settings.setMergeScans(INSTANCE().getBoolean(P_MERGE_SCANS, DEF_MERGE_SCANS));
		//
		return settings;
	}

	public static FilterSettingsScanMerger getScanMergerFilterSettings() {

		return new FilterSettingsScanMerger();
	}

	public static FilterSettingsDeleteIdentifier getDeleteIdentifierFilterSettings() {

		return new FilterSettingsDeleteIdentifier();
	}

	public static FilterSettingsScanSelector getFilterSettingsScanSelector() {

		FilterSettingsScanSelector settings = new FilterSettingsScanSelector();
		settings.setScanSelectorOption(getScanSelectorOption());
		settings.setScanSelectorValue(getScanSelectorValue());
		//
		return settings;
	}

	public static FilterSettingsRetentionIndexSelector getFilterSettingsRetentionIndexSelector() {

		return new FilterSettingsRetentionIndexSelector();
	}

	public static String getScanRemoverPattern() {

		return INSTANCE().get(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN);
	}

	public static String getScanNumberPattern() {

		return INSTANCE().get(P_CLIP_SCAN_NUMBER_PATTERN, DEF_CLIP_SCAN_NUMBER_PATTERN);
	}

	private static ScanSelectorOption getScanSelectorOption() {

		try {
			return ScanSelectorOption.valueOf(INSTANCE().get(P_SCAN_SELECTOR_OPTION, DEF_SCAN_SELECTOR_OPTION));
		} catch(Exception e) {
			return ScanSelectorOption.RETENTION_TIME_MS;
		}
	}

	private static double getScanSelectorValue() {

		return INSTANCE().getDouble(P_SCAN_SELECTOR_VALUE, DEF_SCAN_SELECTOR_VALUE);
	}
}