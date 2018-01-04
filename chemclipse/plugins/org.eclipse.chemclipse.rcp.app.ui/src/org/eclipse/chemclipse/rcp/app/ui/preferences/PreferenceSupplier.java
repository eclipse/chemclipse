/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.preferences;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceSupplier {

	public static void setChangePerspectivesAutomatically(boolean changeAutomatically) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.P_CHANGE_PERSPECTIVE_AUTOMATICALLY, changeAutomatically);
	}

	public static boolean isChangePerspectivesAutomatically() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_CHANGE_PERSPECTIVE_AUTOMATICALLY);
	}
}
