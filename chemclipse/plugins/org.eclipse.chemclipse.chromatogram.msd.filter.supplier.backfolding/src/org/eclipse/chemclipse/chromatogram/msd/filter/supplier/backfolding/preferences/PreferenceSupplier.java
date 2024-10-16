/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_BACKFOLDING_RUNS = "backfoldingRuns";
	public static final String P_MAX_RETENTION_TIME_SHIFT = "maxRetentionTimeShift";
	public static final int MIN_BACKFOLDING_RUNS = 1;
	public static final int MAX_BACKFOLDING_RUNS = 10;
	public static final int DEF_BACKFOLDING_RUNS = 3;
	public static final int MIN_RETENTION_TIME_SHIFT = 500; // 0.008 minutes
	public static final int MAX_RETENTION_TIME_SHIFT = 25000; // 0.416 minutes
	public static final int DEF_RETENTION_TIME_SHIFT = 5000; // 0.08 minutes

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_BACKFOLDING_RUNS, Integer.toString(DEF_BACKFOLDING_RUNS));
		putDefault(P_MAX_RETENTION_TIME_SHIFT, Integer.toString(DEF_RETENTION_TIME_SHIFT));
	}

	public static ChromatogramFilterSettings getFilterSettings() {

		ChromatogramFilterSettings filterSettings = new ChromatogramFilterSettings();
		filterSettings.setMaximumRetentionTimeShift(INSTANCE().getInteger(P_MAX_RETENTION_TIME_SHIFT, DEF_RETENTION_TIME_SHIFT));
		filterSettings.setNumberOfBackfoldingRuns(INSTANCE().getInteger(P_BACKFOLDING_RUNS, DEF_BACKFOLDING_RUNS));
		return filterSettings;
	}

	public static int getMaxRetentionTimeShift() {

		return INSTANCE().getInteger(P_MAX_RETENTION_TIME_SHIFT, DEF_RETENTION_TIME_SHIFT);
	}

	public static int getBackfoldingRuns() {

		return INSTANCE().getInteger(P_BACKFOLDING_RUNS, DEF_BACKFOLDING_RUNS);
	}
}