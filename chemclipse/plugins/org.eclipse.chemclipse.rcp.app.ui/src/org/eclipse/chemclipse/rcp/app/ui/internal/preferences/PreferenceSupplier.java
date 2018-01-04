/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceConstants;

public class PreferenceSupplier {

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {
	}

	/**
	 * Returns whether the perspective dialog should be shown or not.
	 * 
	 * @return boolean
	 */
	public static boolean getShowPerspectiveDialog() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_PERSPECTIVE_DIALOG);
	}

	/**
	 * Sets whether the perspective dialog should be shown or not.
	 * 
	 * @param showPerspectiveDialog
	 */
	public static void setShowPerspectiveDialog(boolean showPerspectiveDialog) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.P_SHOW_PERSPECTIVE_DIALOG, showPerspectiveDialog);
	}

	/**
	 * Returns whether the perspective should be changed automatically.
	 * 
	 * @return
	 */
	public static boolean getChangePerspectiveAutomatically() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_CHANGE_PERSPECTIVE_AUTOMATICALLY);
	}

	/**
	 * Sets whether the perspective should be changed automatically.
	 * 
	 * @return
	 */
	public static void setChangePerspectiveAutomatically(boolean changePerspectiveAutomatically) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.P_CHANGE_PERSPECTIVE_AUTOMATICALLY, changePerspectiveAutomatically);
	}
}
