/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.CalculatorStrategy;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.CalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.MassSpectrumIdentifierAlkaneSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.PeakIdentifierAlkaneSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ResetterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String[][] CALCULATOR_OPTIONS = new String[][]{ //
			{CalculatorStrategy.AUTO.label(), CalculatorStrategy.AUTO.name()}, //
			{CalculatorStrategy.CHROMATOGRAM.label(), CalculatorStrategy.CHROMATOGRAM.name()}, //
			{CalculatorStrategy.FILES.label(), CalculatorStrategy.FILES.name()} //
	};
	/*
	 * RI Calculation
	 */
	public static final String P_RETENTION_INDEX_FILES = "retentionIndexFiles";
	public static final String DEF_RETENTION_INDEX_FILES = "";
	public static final String P_CALCULATOR_STRATEGY = "calculatorStrategy";
	public static final String DEF_CALCULATOR_STRATEGY = CalculatorStrategy.FILES.name();
	public static final String P_USE_DEFAULT_COLUMN = "useDefaultColumn";
	public static final boolean DEF_USE_DEFAULT_COLUMN = true;
	public static final String P_PROCESS_REFERENCED_CHROMATOGRAMS = "processReferencedChromatograms";
	public static final boolean DEF_PROCESS_REFERENCED_CHROMATOGRAMS = true;
	public static final String P_FILTER_PATH_INDEX_FILES = "filterPathIndexFiles";
	public static final String DEF_FILTER_PATH_INDEX_FILES = "";
	public static final String P_FILTER_PATH_MODELS_MSD = "filterPathModelsMSD";
	public static final String DEF_FILTER_PATH_MODELS_MSD = "";
	public static final String P_FILTER_PATH_MODELS_CSD = "filterPathModelsCSD";
	public static final String DEF_FILTER_PATH_MODELS_CSD = "";
	/*
	 * Alkane Identifier
	 */
	public static final String P_NUMBER_OF_TARGETS = "numberOfTargets";
	public static final int DEF_NUMBER_OF_TARGETS = 15;
	public static final int MIN_NUMBER_OF_TARGETS = 1;
	public static final int MAX_NUMBER_OF_TARGETS = 100;
	public static final String P_MIN_MATCH_FACTOR = "minMatchFactor";
	public static final float DEF_MIN_MATCH_FACTOR = 70.0f;
	public static final float MIN_MIN_MATCH_FACTOR = 0.0f;
	public static final float MAX_MIN_MATCH_FACTOR = 100.0f;
	public static final String P_MIN_REVERSE_MATCH_FACTOR = "minReverseMatchFactor";
	public static final float DEF_MIN_REVERSE_MATCH_FACTOR = 70.0f;
	public static final float MIN_MIN_REVERSE_MATCH_FACTOR = 0.0f;
	public static final float MAX_MIN_REVERSE_MATCH_FACTOR = 100.0f;
	//
	public static final String P_LIST_PATH_IMPORT_FILE = "listPathImportCalibrationFile";
	public static final String DEF_LIST_PATH_IMPORT_FILE = "";
	public static final String P_LIST_PATH_IMPORT_TEMPLATE = "listPathImportTemplate";
	public static final String DEF_LIST_PATH_IMPORT_TEMPLATE = "";
	public static final String P_LIST_PATH_EXPORT_TEMPLATE = "listPathExportTemplate";
	public static final String DEF_LIST_PATH_EXPORT_TEMPLATE = "";
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

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_RETENTION_INDEX_FILES, DEF_RETENTION_INDEX_FILES);
		defaultValues.put(P_CALCULATOR_STRATEGY, DEF_CALCULATOR_STRATEGY);
		defaultValues.put(P_USE_DEFAULT_COLUMN, Boolean.toString(DEF_USE_DEFAULT_COLUMN));
		defaultValues.put(P_PROCESS_REFERENCED_CHROMATOGRAMS, Boolean.toString(DEF_PROCESS_REFERENCED_CHROMATOGRAMS));
		defaultValues.put(P_FILTER_PATH_INDEX_FILES, DEF_FILTER_PATH_INDEX_FILES);
		defaultValues.put(P_FILTER_PATH_MODELS_MSD, DEF_FILTER_PATH_MODELS_MSD);
		defaultValues.put(P_FILTER_PATH_MODELS_CSD, DEF_FILTER_PATH_MODELS_CSD);
		defaultValues.put(P_NUMBER_OF_TARGETS, Integer.toString(DEF_NUMBER_OF_TARGETS));
		defaultValues.put(P_MIN_MATCH_FACTOR, Float.toString(DEF_MIN_MATCH_FACTOR));
		defaultValues.put(P_MIN_REVERSE_MATCH_FACTOR, Float.toString(DEF_MIN_REVERSE_MATCH_FACTOR));
		defaultValues.put(P_LIST_PATH_IMPORT_FILE, DEF_LIST_PATH_IMPORT_FILE);
		defaultValues.put(P_LIST_PATH_IMPORT_TEMPLATE, DEF_LIST_PATH_IMPORT_TEMPLATE);
		defaultValues.put(P_LIST_PATH_EXPORT_TEMPLATE, DEF_LIST_PATH_EXPORT_TEMPLATE);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static PeakIdentifierAlkaneSettings getPeakIdentifierAlkaneSettings() {

		PeakIdentifierAlkaneSettings settings = new PeakIdentifierAlkaneSettings();
		settings.setNumberOfTargets(getNumberOfTargets());
		settings.setMinMatchFactor(getMinMatchFactor());
		settings.setMinReverseMatchFactor(getMinReverseMatchFactor());
		return settings;
	}

	public static MassSpectrumIdentifierAlkaneSettings getMassSpectrumIdentifierAlkaneSettings() {

		MassSpectrumIdentifierAlkaneSettings settings = new MassSpectrumIdentifierAlkaneSettings();
		settings.setNumberOfTargets(getNumberOfTargets());
		settings.setMinMatchFactor(getMinMatchFactor());
		settings.setMinReverseMatchFactor(getMinReverseMatchFactor());
		return settings;
	}

	public static CalculatorSettings getChromatogramCalculatorSettings() {

		CalculatorSettings settings = new CalculatorSettings();
		settings.setCalibrationFile(null); // Only used by dynamic process settings.
		settings.setRetentionIndexFiles(getRetentionIndexFiles());
		settings.setCalculatorStrategy(getCalculatorStrategy());
		settings.setUseDefaultColumn(isUseDefaultColumn());
		settings.setProcessReferencedChromatograms(isProcessReferencedChromatograms());
		return settings;
	}

	public static ResetterSettings getChromatogramResetterSettings() {

		ResetterSettings settings = new ResetterSettings();
		settings.setProcessReferencedChromatograms(isProcessReferencedChromatograms());
		return settings;
	}

	public static List<String> getRetentionIndexFiles() {

		FileListUtil fileListUtil = new FileListUtil();
		return fileListUtil.getFiles(INSTANCE().get(P_RETENTION_INDEX_FILES, DEF_RETENTION_INDEX_FILES));
	}

	public static void setRetentionIndexFiles(List<String> retentionIndexFiles) {

		FileListUtil fileListUtil = new FileListUtil();
		String items[] = retentionIndexFiles.toArray(new String[retentionIndexFiles.size()]);
		INSTANCE().put(P_RETENTION_INDEX_FILES, fileListUtil.createList(items));
	}

	public static String getFilterPathIndexFiles() {

		return INSTANCE().get(P_FILTER_PATH_INDEX_FILES, DEF_FILTER_PATH_INDEX_FILES);
	}

	public static void setFilterPathIndexFiles(String filterPath) {

		INSTANCE().put(P_FILTER_PATH_INDEX_FILES, filterPath);
	}

	public static String getFilterPathModelsMSD() {

		return INSTANCE().get(P_FILTER_PATH_MODELS_MSD, DEF_FILTER_PATH_MODELS_MSD);
	}

	public static void setFilterPathModelsMSD(String filterPath) {

		INSTANCE().put(P_FILTER_PATH_MODELS_MSD, filterPath);
	}

	public static String getFilterPathModelsCSD() {

		return INSTANCE().get(P_FILTER_PATH_MODELS_CSD, DEF_FILTER_PATH_MODELS_CSD);
	}

	public static void setFilterPathModelsCSD(String filterPath) {

		INSTANCE().put(P_FILTER_PATH_MODELS_CSD, filterPath);
	}

	public static int getNumberOfTargets() {

		return INSTANCE().getInteger(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS);
	}

	public static float getMinMatchFactor() {

		return INSTANCE().getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR);
	}

	public static float getMinReverseMatchFactor() {

		return INSTANCE().getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR);
	}

	public static String getListPathImportFile() {

		return INSTANCE().get(P_LIST_PATH_IMPORT_FILE, DEF_LIST_PATH_IMPORT_FILE);
	}

	public static void setListPathImportFile(String filterPath) {

		INSTANCE().put(P_LIST_PATH_IMPORT_FILE, filterPath);
	}

	public static String getListPathImportTemplate() {

		return INSTANCE().get(P_LIST_PATH_IMPORT_TEMPLATE, DEF_LIST_PATH_IMPORT_TEMPLATE);
	}

	public static void setListPathImportTemplate(String filterPath) {

		INSTANCE().put(P_LIST_PATH_IMPORT_TEMPLATE, filterPath);
	}

	public static String getListPathExportTemplate() {

		return INSTANCE().get(P_LIST_PATH_EXPORT_TEMPLATE, DEF_LIST_PATH_EXPORT_TEMPLATE);
	}

	public static void setListPathExportTemplate(String filterPath) {

		INSTANCE().put(P_LIST_PATH_EXPORT_TEMPLATE, filterPath);
	}

	private static CalculatorStrategy getCalculatorStrategy() {

		return CalculatorStrategy.valueOf(INSTANCE().get(P_CALCULATOR_STRATEGY, DEF_CALCULATOR_STRATEGY));
	}

	private static boolean isUseDefaultColumn() {

		return INSTANCE().getBoolean(P_USE_DEFAULT_COLUMN, DEF_USE_DEFAULT_COLUMN);
	}

	private static boolean isProcessReferencedChromatograms() {

		return INSTANCE().getBoolean(P_PROCESS_REFERENCED_CHROMATOGRAMS, DEF_PROCESS_REFERENCED_CHROMATOGRAMS);
	}
}