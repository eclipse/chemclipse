/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.AbstractChromatogramIntegrationSettings;

/**
 * @author eselmeister
 */
public class SumareaIntegrationSettings extends AbstractChromatogramIntegrationSettings implements ISumareaIntegrationSettings {

	private String selectedIons;

	public SumareaIntegrationSettings() {
	}

	@Override
	public String getSelectedIons() {

		return selectedIons;
	}

	@Override
	public void setSelectedIons(String selectedIons) {

		this.selectedIons = selectedIons;
	}
}
