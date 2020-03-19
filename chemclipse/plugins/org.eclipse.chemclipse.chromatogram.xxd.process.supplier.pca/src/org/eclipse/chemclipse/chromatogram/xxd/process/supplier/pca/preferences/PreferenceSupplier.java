/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String N_INPUT_FILE = "INPUT_FILE";
	/*
	 * General settings
	 */
	public static final String[][] ALGORITHM_TYPES = new String[][]{//
			{Algorithm.SVD.toString(), Algorithm.SVD.toString()}, //
			{Algorithm.NIPALS.toString(), Algorithm.NIPALS.toString()}, //
			{Algorithm.OPLS.toString(), Algorithm.OPLS.toString()}//
	};
	//
	public static final String P_FILES_PATH_IMPORT_CHROMATOGRAMS = "filePathImportChromatograms";
	public static final String DEF_FILES_PATH_IMPORT_CHROMATOGRAMS = "";
	public static final String P_ALGORITHM_TYPE = "algorithmType";
	public static final String DEF_ALGORITHM_TYPE = Algorithm.SVD.toString();
	public static final String P_REMOVE_USELESS_VARIABLES = "removeUselessVariables";
	public static final boolean DEF_REMOVE_USELESS_VARIABLES = true;
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
		defaultValues.put(P_FILES_PATH_IMPORT_CHROMATOGRAMS, DEF_FILES_PATH_IMPORT_CHROMATOGRAMS);
		defaultValues.put(P_ALGORITHM_TYPE, DEF_ALGORITHM_TYPE);
		defaultValues.put(P_REMOVE_USELESS_VARIABLES, Boolean.toString(DEF_REMOVE_USELESS_VARIABLES));
		defaultValues.put(P_NUMBER_OF_COMPONENTS, Integer.toString(DEF_NUMBER_OF_COMPONENTS));
		defaultValues.put(P_RETENTION_TIME_WINDOW_PEAKS, Double.toString(DEF_RETENTION_TIME_WINDOW_PEAKS));
		defaultValues.put(P_SCORE_PLOT_2D_SYMBOL_SIZE, Integer.toString(DEF_SCORE_PLOT_2D_SYMBOL_SIZE));
		defaultValues.put(P_SCORE_PLOT_2D_SYMBOL_TYPE, DEF_SCORE_PLOT_2D_SYMBOL_TYPE);
		defaultValues.put(P_LOADING_PLOT_2D_SYMBOL_SIZE, Integer.toString(DEF_LOADING_PLOT_2D_SYMBOL_SIZE));
		defaultValues.put(P_LOADING_PLOT_2D_SYMBOL_TYPE, DEF_LOADING_PLOT_2D_SYMBOL_TYPE);
		defaultValues.put(P_COLOR_SCHEME, DEF_COLOR_SCHEME);
		return defaultValues;
	}

	public static IAnalysisSettings getPcaSettings() {

		IAnalysisSettings analysisSettings = new AnalysisSettings();
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		analysisSettings.setNumberOfPrincipalComponents(preferences.getInt(P_NUMBER_OF_COMPONENTS, DEF_NUMBER_OF_COMPONENTS));
		analysisSettings.setAlgorithm(Algorithm.valueOf(preferences.get(P_ALGORITHM_TYPE, DEF_ALGORITHM_TYPE)));
		analysisSettings.setRemoveUselessVariables(preferences.getBoolean(P_REMOVE_USELESS_VARIABLES, DEF_REMOVE_USELESS_VARIABLES));
		return analysisSettings;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static int getNumberOfComponents() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_NUMBER_OF_COMPONENTS, DEF_NUMBER_OF_COMPONENTS);
	}

	public static String getColorScheme() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_COLOR_SCHEME, DEF_COLOR_SCHEME);
	}
}
