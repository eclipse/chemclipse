/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;

public class PreferenceSupplier {

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {

	}

	/**
	 * Returns the x offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayXOffset() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_OVERLAY_X_OFFSET);
	}

	/**
	 * Returns the y offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayYOffset() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_OVERLAY_Y_OFFSET);
	}
}
