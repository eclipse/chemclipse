/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SELECTED_PROFILE = "selectedProfile";
	public static final String DEF_SELECTED_PROFILE = "";
	public static final String P_SHOW_PERSPECTIVE_DIALOG = "showPerspectiveDialog";
	public static final boolean DEF_SHOW_PERSPECTIVE_DIALOG = true;
	public static final String P_CHANGE_PERSPECTIVE_AUTOMATICALLY = "changePerspectiveAutomatically";
	public static final boolean DEF_CHANGE_PERSPECTIVE_AUTOMATICALLY = true;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		/*
		 * Don't use Activator.getDefault().getBundle().getSymbolicName(); here
		 * as it might have been not initialized yet.
		 */
		return "org.eclipse.chemclipse.rcp.app.ui";
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_SELECTED_PROFILE, DEF_SELECTED_PROFILE);
		putDefault(P_SHOW_PERSPECTIVE_DIALOG, DEF_SHOW_PERSPECTIVE_DIALOG);
		putDefault(P_CHANGE_PERSPECTIVE_AUTOMATICALLY, DEF_CHANGE_PERSPECTIVE_AUTOMATICALLY);
	}

	public static void setChangePerspectivesAutomatically(boolean changeAutomatically) {

		INSTANCE().setBoolean(P_CHANGE_PERSPECTIVE_AUTOMATICALLY, changeAutomatically);
	}

	public static boolean isChangePerspectivesAutomatically() {

		return INSTANCE().getBoolean(P_CHANGE_PERSPECTIVE_AUTOMATICALLY);
	}

	/**
	 * Returns whether the perspective dialog should be shown or not.
	 * 
	 * @return boolean
	 */
	public static boolean getShowPerspectiveDialog() {

		return INSTANCE().getBoolean(P_SHOW_PERSPECTIVE_DIALOG);
	}

	/**
	 * Sets whether the perspective dialog should be shown or not.
	 * 
	 * @param showPerspectiveDialog
	 */
	public static void setShowPerspectiveDialog(boolean showPerspectiveDialog) {

		INSTANCE().setBoolean(P_SHOW_PERSPECTIVE_DIALOG, showPerspectiveDialog);
	}

	/**
	 * Returns whether the perspective should be changed automatically.
	 * 
	 * @return
	 */
	public static boolean getChangePerspectiveAutomatically() {

		return INSTANCE().getBoolean(P_CHANGE_PERSPECTIVE_AUTOMATICALLY);
	}

	/**
	 * Sets whether the perspective should be changed automatically.
	 * 
	 * @return
	 */
	public static void setChangePerspectiveAutomatically(boolean changePerspectiveAutomatically) {

		INSTANCE().setBoolean(P_CHANGE_PERSPECTIVE_AUTOMATICALLY, changePerspectiveAutomatically);
	}
}