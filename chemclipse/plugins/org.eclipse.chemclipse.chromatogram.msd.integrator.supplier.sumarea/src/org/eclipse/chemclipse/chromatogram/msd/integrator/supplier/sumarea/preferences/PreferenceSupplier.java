/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.preferences;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.Activator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SELECTED_IONS = "selectedIons";
	public static final String DEF_SELECTED_IONS = ""; // none selected means integrate all ion

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_SELECTED_IONS, DEF_SELECTED_IONS);
	}

	/**
	 * Returns a settings instance.
	 */
	public static ChromatogramIntegrationSettings getIntegrationSettings() {

		ChromatogramIntegrationSettings integrationSettings = new ChromatogramIntegrationSettings();
		String ions = INSTANCE().get(P_SELECTED_IONS, DEF_SELECTED_IONS);
		integrationSettings.setSelectedIons(ions);
		return integrationSettings;
	}
}