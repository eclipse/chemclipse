/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Janos Binder - new features
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.settings.PeakDatabaseSettings;
import org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.settings.PeakQuantifierSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_USE_QUANTITATION_DATABASE_EDITOR = "useQuantitationDatabaseEditor";
	public static final boolean DEF_USE_QUANTITATION_DATABASE_EDITOR = false;
	public static final String P_SELECTED_QUANTITATION_DATABASE = "selectedQuantitationDatabase";
	public static final String DEF_SELECTED_QUANTITATION_DATABASE = "";
	public static final String P_FILTER_PATH_NEW_QUANT_DB = "filterPathNewQuantDB";
	public static final String DEF_FILTER_PATH_NEW_QUANT_DB = "";
	//
	public static final double MIN_RETENTION_TIME = 0.0d;
	public static final double MAX_RETENTION_TIME = Double.MAX_VALUE;
	public static final String P_RETENTION_TIME_NEGATIVE_DEVIATION = "retentionTimeNegativeDeviation";
	public static final double DEF_RETENTION_TIME_NEGATIVE_DEVIATION = 0.5d; // Minutes
	public static final String P_RETENTION_TIME_POSITIVE_DEVIATION = "retentionTimePositiveDeviation";
	public static final double DEF_RETENTION_TIME_POSITIVE_DEVIATION = 0.5d; // Minutes
	//
	public static final float MIN_RETENTION_INDEX = 0.0f;
	public static final float MAX_RETENTION_INDEX = Float.MAX_VALUE;
	public static final String P_RETENTION_INDEX_NEGATIVE_DEVIATION = "retentionIndexNegativeDeviation";
	public static final float DEF_RETENTION_INDEX_NEGATIVE_DEVIATION = 10.0f; // Index
	public static final String P_RETENTION_INDEX_POSITIVE_DEVIATION = "retentionIndexPositiveDeviation";
	public static final float DEF_RETENTION_INDEX_POSITIVE_DEVIATION = 10.0f; // Index
	//
	public static final String QUANTITATION_STRATEGY_NONE = "NONE";
	public static final String QUANTITATION_STRATEGY_RETENTION_TIME = "RT";
	public static final String QUANTITATION_STRATEGY_REFERENCES = "REFS";
	public static final String QUANTITATION_STRATEGY_NAME = "NAME";
	//
	public static final String[][] QUANTITATION_STRATEGY_OPTIONS = new String[][]{//
			{"None", QUANTITATION_STRATEGY_NONE}, //
			{"Retention Time", QUANTITATION_STRATEGY_RETENTION_TIME}, //
			{"References", QUANTITATION_STRATEGY_REFERENCES}, //
			{"Name", QUANTITATION_STRATEGY_NAME}};
	//
	public static final String P_QUANTITATION_STRATEGY = "quantitationStrategy";
	public static final String DEF_QUANTITATION_STRATEGY = QUANTITATION_STRATEGY_NONE;
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
		//
		defaultValues.put(P_USE_QUANTITATION_DATABASE_EDITOR, Boolean.toString(DEF_USE_QUANTITATION_DATABASE_EDITOR));
		defaultValues.put(P_SELECTED_QUANTITATION_DATABASE, DEF_SELECTED_QUANTITATION_DATABASE);
		defaultValues.put(P_FILTER_PATH_NEW_QUANT_DB, DEF_FILTER_PATH_NEW_QUANT_DB);
		defaultValues.put(P_RETENTION_TIME_NEGATIVE_DEVIATION, Double.toString(DEF_RETENTION_TIME_NEGATIVE_DEVIATION));
		defaultValues.put(P_RETENTION_TIME_POSITIVE_DEVIATION, Double.toString(DEF_RETENTION_TIME_POSITIVE_DEVIATION));
		defaultValues.put(P_RETENTION_INDEX_NEGATIVE_DEVIATION, Float.toString(DEF_RETENTION_INDEX_NEGATIVE_DEVIATION));
		defaultValues.put(P_RETENTION_INDEX_POSITIVE_DEVIATION, Float.toString(DEF_RETENTION_INDEX_POSITIVE_DEVIATION));
		defaultValues.put(P_QUANTITATION_STRATEGY, DEF_QUANTITATION_STRATEGY);
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getSelectedQuantitationDatabase() {

		return INSTANCE().get(P_SELECTED_QUANTITATION_DATABASE, DEF_SELECTED_QUANTITATION_DATABASE);
	}

	public static void setSelectedQuantitationDatabase(String selectedQuantitationDatabase) {

		INSTANCE().put(P_SELECTED_QUANTITATION_DATABASE, selectedQuantitationDatabase);
	}

	public static PeakQuantifierSettings getPeakQuantifierSettings() {

		PeakQuantifierSettings peakQuantifierSettings = new PeakQuantifierSettings();
		return peakQuantifierSettings;
	}

	public static PeakDatabaseSettings getPeakDatabaseSettings() {

		PeakDatabaseSettings peakDatabaseSettings = new PeakDatabaseSettings();
		return peakDatabaseSettings;
	}

	public static String getFilterPathNewQuantDB() {

		return INSTANCE().get(P_FILTER_PATH_NEW_QUANT_DB, DEF_FILTER_PATH_NEW_QUANT_DB);
	}

	public static void setFilterPathNewQuantDB(String filterPath) {

		INSTANCE().put(P_FILTER_PATH_NEW_QUANT_DB, filterPath);
	}

	public static double getRetentionTimeNegativeDeviation() {

		return INSTANCE().getDouble(P_RETENTION_TIME_NEGATIVE_DEVIATION, DEF_RETENTION_TIME_NEGATIVE_DEVIATION);
	}

	public static double getRetentionTimePositiveDeviation() {

		return INSTANCE().getDouble(P_RETENTION_TIME_POSITIVE_DEVIATION, DEF_RETENTION_TIME_POSITIVE_DEVIATION);
	}

	public static float getRetentionIndexNegativeDeviation() {

		return INSTANCE().getFloat(P_RETENTION_INDEX_NEGATIVE_DEVIATION, DEF_RETENTION_INDEX_NEGATIVE_DEVIATION);
	}

	public static float getRetentionIndexPositiveDeviation() {

		return INSTANCE().getFloat(P_RETENTION_INDEX_POSITIVE_DEVIATION, DEF_RETENTION_INDEX_POSITIVE_DEVIATION);
	}

	public static String getQuantitationStrategy() {

		return INSTANCE().get(P_QUANTITATION_STRATEGY, DEF_QUANTITATION_STRATEGY);
	}
}