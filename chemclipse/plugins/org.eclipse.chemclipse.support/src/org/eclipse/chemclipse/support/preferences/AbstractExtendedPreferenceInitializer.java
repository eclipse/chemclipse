/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public abstract class AbstractExtendedPreferenceInitializer extends AbstractPreferenceInitializer {

	private final IPreferenceSupplier preferenceSupplier;

	/**
	 * Set the appropriate preference supplier.
	 * 
	 * @param preferenceSupplier
	 */
	public AbstractExtendedPreferenceInitializer(IPreferenceSupplier preferenceSupplier) {
		this.preferenceSupplier = preferenceSupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {

		IEclipsePreferences preferences = preferenceSupplier.getPreferences();
		Map<String, String> defaultValues = preferenceSupplier.getDefaultValues();
		for(Map.Entry<String, String> entry : defaultValues.entrySet()) {
			/*
			 * Add if the doesn't exists already.
			 */
			if(null == preferences.get(entry.getKey(), null)) {
				preferences.put(entry.getKey(), entry.getValue());
			}
		}
		/*
		 * Flush the preferences.
		 */
		try {
			preferences.flush();
		} catch(BackingStoreException e) {
			// can't flush then
		}
	}
}
