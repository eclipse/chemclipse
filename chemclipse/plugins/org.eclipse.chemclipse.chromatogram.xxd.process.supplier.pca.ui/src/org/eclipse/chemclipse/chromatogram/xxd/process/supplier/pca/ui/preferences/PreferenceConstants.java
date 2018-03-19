/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.swtchart.ILineSeries.PlotSymbolType;

public class PreferenceConstants {

	public static final String[][] ALGORITHM_TYPES = new String[][]{//
			{IPcaSettings.PCA_ALGO_SVD, IPcaSettings.PCA_ALGO_SVD}, //
			{IPcaSettings.PCA_ALGO_NIPALS, IPcaSettings.PCA_ALGO_NIPALS}, //
			{IPcaSettings.OPLS_ALGO_NIPALS, IPcaSettings.OPLS_ALGO_NIPALS}//
	};
	// General settings
	public static final String P_ALGORITHM_TYPE = "algorithmType";
	public static final String DEF_ALGORITHM_TYPE = IPcaSettings.PCA_ALGO_NIPALS;
	public static final String P_NUMBER_OF_COMPONENTS = "numberOfComponents";
	public static final int MIN_NUMBER_OF_COMPONENTS = 1;
	public static final int MAX_NUMBER_OF_COMPONENTS = 1000;
	public static final int DEF_NUMBER_OF_COMPONENTS = 3;
	public static final String P_AUTO_REEVALUATE = "autoReevaluate";
	public static final boolean DEF_AUTO_REEVALUATE = false;
	public static final String P_RETENTION_TIME_WINDOW_PEAKS = "retentionTimeWindowPeaks";
	public static final double DEF_RETENTION_TIME_WINDOW_PEAKS = 0.1;
	// Score Plot general Settings
	public static final String P_SCORE_PLOT_2D_SYMBOL_SIZE = "scorePlot2dSymbolSize";
	public static final int DEF_SCORE_PLOT_2D_SYMBOL_SIZE = 6;
	public static final int MIN_SCORE_PLOT_2D_SYMBOL_SIZE = 1;
	public static final int MAX_SCORE_PLOT_2D_SYMBOL_SIZE = 100;
	public static final String P_SCORE_PLOT_2D_SYMBOL_TYPE = "scorePlot2dSymbolType";
	public static final String DEF_SCORE_PLOT_2D_SYMBOL_TYPE = PlotSymbolType.SQUARE.toString();
	// Loading Plot general Settings
	public static final String P_LOADING_PLOT_2D_SYMBOL_SIZE = "loadingPlot2dSymbolSize";
	public static final int DEF_LOADING_PLOT_2D_SYMBOL_SIZE = 6;
	public static final int MIN_LOADING_PLOT_2D_SYMBOL_SIZE = 1;
	public static final int MAX_LOADING_PLOT_2D_SYMBOL_SIZE = 100;
	public static final String P_LOADING_PLOT_2D_SYMBOL_TYPE = "loadingPlot2dSymbolType";
	public static final String DEF_LOADING_PLOT_2D_SYMBOL_TYPE = PlotSymbolType.CIRCLE.toString();
}
