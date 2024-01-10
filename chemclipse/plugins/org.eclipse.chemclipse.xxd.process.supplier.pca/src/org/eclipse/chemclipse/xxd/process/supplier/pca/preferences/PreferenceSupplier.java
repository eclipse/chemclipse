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
package org.eclipse.chemclipse.xxd.process.supplier.pca.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.pca.Activator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.LabelOptionPCA;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

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

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_ALGORITHM, DEF_ALGORITHM);
		putDefault(P_REMOVE_USELESS_VARIABLES, Boolean.toString(DEF_REMOVE_USELESS_VARIABLES));
		putDefault(P_LABEL_OPTION_PCA, DEF_LABEL_OPTION_PCA);
		putDefault(P_NUMBER_OF_COMPONENTS, Integer.toString(DEF_NUMBER_OF_COMPONENTS));
		putDefault(P_RETENTION_TIME_WINDOW_PEAKS, Double.toString(DEF_RETENTION_TIME_WINDOW_PEAKS));
		putDefault(P_SCORE_PLOT_2D_SYMBOL_SIZE, Integer.toString(DEF_SCORE_PLOT_2D_SYMBOL_SIZE));
		putDefault(P_SCORE_PLOT_2D_SYMBOL_TYPE, DEF_SCORE_PLOT_2D_SYMBOL_TYPE);
		putDefault(P_LOADING_PLOT_2D_SYMBOL_SIZE, Integer.toString(DEF_LOADING_PLOT_2D_SYMBOL_SIZE));
		putDefault(P_LOADING_PLOT_2D_SYMBOL_TYPE, DEF_LOADING_PLOT_2D_SYMBOL_TYPE);
		putDefault(P_COLOR_SCHEME, DEF_COLOR_SCHEME);
		putDefault(P_PATH_IMPORT_FILE, DEF_PATH_IMPORT_FILE);
		putDefault(P_PATH_EXPORT_FILE, DEF_PATH_EXPORT_FILE);
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

	public static int getNumberOfPrincipalComponents() {

		return INSTANCE().getInteger(P_NUMBER_OF_COMPONENTS, DEF_NUMBER_OF_COMPONENTS);
	}

	public static String getColorScheme() {

		return INSTANCE().get(P_COLOR_SCHEME, DEF_COLOR_SCHEME);
	}

	public static String getPathImportFile() {

		return INSTANCE().get(P_PATH_IMPORT_FILE, DEF_PATH_IMPORT_FILE);
	}

	public static void setPathImportFile(String filterPath) {

		INSTANCE().put(P_PATH_IMPORT_FILE, filterPath);
	}

	public static String getPathExportFile() {

		return INSTANCE().get(P_PATH_EXPORT_FILE, DEF_PATH_EXPORT_FILE);
	}

	public static void setPathExportFile(String filterPath) {

		INSTANCE().put(P_PATH_EXPORT_FILE, filterPath);
	}

	public static boolean isRemoveUselessVariables() {

		return INSTANCE().getBoolean(P_REMOVE_USELESS_VARIABLES, DEF_REMOVE_USELESS_VARIABLES);
	}

	public static Algorithm getAlgorithm() {

		try {
			return Algorithm.valueOf(INSTANCE().get(P_ALGORITHM, DEF_ALGORITHM));
		} catch(Exception e) {
			return Algorithm.NIPALS;
		}
	}

	public static LabelOptionPCA getLabelOptionPCA() {

		try {
			return LabelOptionPCA.valueOf(INSTANCE().get(P_LABEL_OPTION_PCA, DEF_LABEL_OPTION_PCA));
		} catch(Exception e) {
			return LabelOptionPCA.SAMPLE_NAME;
		}
	}
}