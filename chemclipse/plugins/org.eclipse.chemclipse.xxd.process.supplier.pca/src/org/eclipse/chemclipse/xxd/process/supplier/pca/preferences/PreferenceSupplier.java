/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.pca.Activator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.LabelOptionPCA;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferenceSupplier implements IPreferenceSupplier {

	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
	//
	public static final String N_INPUT_FILE = "INPUT_FILE";
	//
	public static final String P_ALGORITHM = "algorithm";
	public static final String DEF_ALGORITHM = Algorithm.NIPALS.toString();
	public static final String P_REMOVE_USELESS_VARIABLES = "removeUselessVariables";
	public static final boolean DEF_REMOVE_USELESS_VARIABLES = true;
	public static final String P_LABEL_OPTION_PCA = "labelOptionPCA";
	public static final String DEF_LABEL_OPTION_PCA = LabelOptionPCA.GROUP_NAME.name();
	//
	public static final String P_NUMBER_OF_COMPONENTS = "numberOfComponents";
	public static final int MIN_NUMBER_OF_COMPONENTS = 3;
	public static final int MAX_NUMBER_OF_COMPONENTS = 1000;
	public static final int DEF_NUMBER_OF_COMPONENTS = 3;
	//
	public static final String P_RETENTION_TIME_WINDOW_PEAKS = "retentionTimeWindowPeaks";
	public static final double DEF_RETENTION_TIME_WINDOW_PEAKS = 0.1;
	// Score Plot general Settings
	public static final String P_SCORE_PLOT_2D_SYMBOL_SIZE = "scorePlot2dSymbolSize";
	public static final int DEF_SCORE_PLOT_2D_SYMBOL_SIZE = 6;
	public static final int MIN_SCORE_PLOT_2D_SYMBOL_SIZE = 1;
	public static final int MAX_SCORE_PLOT_2D_SYMBOL_SIZE = 100;
	public static final String P_SCORE_PLOT_2D_SYMBOL_TYPE = "scorePlot2dSymbolType";
	public static final String DEF_SCORE_PLOT_2D_SYMBOL_TYPE = "CIRCLE";
	// Loading Plot general Settings
	public static final String P_LOADING_PLOT_2D_SYMBOL_SIZE = "loadingPlot2dSymbolSize";
	public static final int DEF_LOADING_PLOT_2D_SYMBOL_SIZE = 6;
	public static final int MIN_LOADING_PLOT_2D_SYMBOL_SIZE = 1;
	public static final int MAX_LOADING_PLOT_2D_SYMBOL_SIZE = 100;
	public static final String P_LOADING_PLOT_2D_SYMBOL_TYPE = "loadingPlot2dSymbolType";
	public static final String DEF_LOADING_PLOT_2D_SYMBOL_TYPE = "CIRCLE";
	//
	public static final String P_COLOR_SCHEME = "colorScheme";
	public static final String DEF_COLOR_SCHEME = "Print";
	//
	public static final String P_PATH_IMPORT_FILE = "pathImportFile";
	public static final String DEF_PATH_IMPORT_FILE = "";
	public static final String P_PATH_EXPORT_FILE = "pathExportFile";
	public static final String DEF_PATH_EXPORT_FILE = "";
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
		defaultValues.put(P_ALGORITHM, DEF_ALGORITHM);
		defaultValues.put(P_REMOVE_USELESS_VARIABLES, Boolean.toString(DEF_REMOVE_USELESS_VARIABLES));
		defaultValues.put(P_LABEL_OPTION_PCA, DEF_LABEL_OPTION_PCA);
		defaultValues.put(P_NUMBER_OF_COMPONENTS, Integer.toString(DEF_NUMBER_OF_COMPONENTS));
		defaultValues.put(P_RETENTION_TIME_WINDOW_PEAKS, Double.toString(DEF_RETENTION_TIME_WINDOW_PEAKS));
		defaultValues.put(P_SCORE_PLOT_2D_SYMBOL_SIZE, Integer.toString(DEF_SCORE_PLOT_2D_SYMBOL_SIZE));
		defaultValues.put(P_SCORE_PLOT_2D_SYMBOL_TYPE, DEF_SCORE_PLOT_2D_SYMBOL_TYPE);
		defaultValues.put(P_LOADING_PLOT_2D_SYMBOL_SIZE, Integer.toString(DEF_LOADING_PLOT_2D_SYMBOL_SIZE));
		defaultValues.put(P_LOADING_PLOT_2D_SYMBOL_TYPE, DEF_LOADING_PLOT_2D_SYMBOL_TYPE);
		defaultValues.put(P_COLOR_SCHEME, DEF_COLOR_SCHEME);
		defaultValues.put(P_PATH_IMPORT_FILE, DEF_PATH_IMPORT_FILE);
		defaultValues.put(P_PATH_EXPORT_FILE, DEF_PATH_EXPORT_FILE);
		return defaultValues;
	}

	public static IAnalysisSettings getPcaSettings() {

		IAnalysisSettings analysisSettings = new AnalysisSettings();
		analysisSettings.setNumberOfPrincipalComponents(getNumberOfPrincipalComponents());
		analysisSettings.setAlgorithm(getAlgorithm());
		analysisSettings.setRemoveUselessVariables(isRemoveUselessVariables());
		analysisSettings.setLabelOptionPCA(getLabelOptionPCA());
		//
		return analysisSettings;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static int getNumberOfPrincipalComponents() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_NUMBER_OF_COMPONENTS, DEF_NUMBER_OF_COMPONENTS);
	}

	public static String getColorScheme() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_COLOR_SCHEME, DEF_COLOR_SCHEME);
	}

	public static String getPathImportFile() {

		return getFilterPath(P_PATH_IMPORT_FILE, DEF_PATH_IMPORT_FILE);
	}

	public static void setPathImportFile(String filterPath) {

		putString(P_PATH_IMPORT_FILE, filterPath);
	}

	public static String getPathExportFile() {

		return getFilterPath(P_PATH_EXPORT_FILE, DEF_PATH_EXPORT_FILE);
	}

	public static void setPathExportFile(String filterPath) {

		putString(P_PATH_EXPORT_FILE, filterPath);
	}

	public static boolean isRemoveUselessVariables() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_REMOVE_USELESS_VARIABLES, DEF_REMOVE_USELESS_VARIABLES);
	}

	public static Algorithm getAlgorithm() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return Algorithm.valueOf(preferences.get(P_ALGORITHM, DEF_ALGORITHM));
		} catch(Exception e) {
			return Algorithm.NIPALS;
		}
	}

	public static LabelOptionPCA getLabelOptionPCA() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return LabelOptionPCA.valueOf(preferences.get(P_LABEL_OPTION_PCA, DEF_LABEL_OPTION_PCA));
		} catch(Exception e) {
			return LabelOptionPCA.SAMPLE_NAME;
		}
	}

	private static String getFilterPath(String key, String def) {

		IEclipsePreferences eclipsePreferences = INSTANCE().getPreferences();
		return eclipsePreferences.get(key, def);
	}

	private static void putString(String key, String value) {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			preferences.put(key, value);
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}