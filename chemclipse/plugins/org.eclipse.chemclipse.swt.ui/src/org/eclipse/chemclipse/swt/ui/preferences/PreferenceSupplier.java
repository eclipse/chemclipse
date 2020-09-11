/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.preferences;

import org.eclipse.chemclipse.swt.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceSupplier {

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {

	}

	public static boolean condenseCycleNumberScans() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS);
	}

	public static boolean showRetentionIndexWithoutDecimals() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
	}

	public static boolean showAreaWithoutDecimals() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS);
	}

	public static boolean isSortCaseSensitive() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SORT_CASE_SENSITIVE);
	}

	public static boolean useAlternateWindowMoveDirection() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION);
	}

	public static boolean isSearchCaseSensitive() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SEARCH_CASE_SENSITIVE);
	}

	public static void setSearchCaseSensitive(boolean searchCaseSensitive) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.P_SEARCH_CASE_SENSITIVE, searchCaseSensitive);
	}
}
