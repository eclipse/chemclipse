/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class VendorMassSpectrumIdentifierSettings extends AbstractMassSpectrumIdentifierSettings implements IVendorMassSpectrumIdentifierSettings {

	private String nistApplication;
	private int numberOfTargets = PreferenceSupplier.DEF_NUMBER_OF_TARGETS;
	private boolean storeTargets;
	private int timeoutInMinutes;

	public VendorMassSpectrumIdentifierSettings() {
		nistApplication = "";
	}

	@Override
	public String getNistApplication() {

		return nistApplication;
	}

	@Override
	public void setNistApplication(String nistApplication) {

		if(nistApplication != null) {
			this.nistApplication = nistApplication;
		}
	}

	@Override
	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	@Override
	public void setNumberOfTargets(int numberOfTargets) {

		if(numberOfTargets >= PreferenceSupplier.MIN_NUMBER_OF_TARGETS && numberOfTargets <= PreferenceSupplier.MAX_NUMBER_OF_TARGETS) {
			this.numberOfTargets = numberOfTargets;
		}
	}

	@Override
	public boolean getStoreTargets() {

		return storeTargets;
	}

	@Override
	public void setStoreTargets(boolean storeTargets) {

		this.storeTargets = storeTargets;
	}

	@Override
	public int getTimeoutInMinutes() {

		return timeoutInMinutes;
	}

	@Override
	public void setTimeoutInMinutes(int timeoutInMinutes) {

		this.timeoutInMinutes = timeoutInMinutes;
	}
}
