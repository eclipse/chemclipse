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
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractMassSpectrumIdentifierSettings;

public class VendorMassSpectrumIdentifierSettings extends AbstractMassSpectrumIdentifierSettings implements IVendorMassSpectrumIdentifierSettings {

	private String massSpectraFiles = "";
	private boolean usePreOptimization;
	private double thresholdPreOptimization;
	private int numberOfTargets;
	private float minMatchFactor;
	private float minReverseMatchFactor;
	private boolean addUnknownMzListTarget;
	private String alternateIdentifierId = "";

	@Override
	public String getMassSpectraFiles() {

		return massSpectraFiles;
	}

	@Override
	public void setMassSpectraFiles(String massSpectraFiles) {

		this.massSpectraFiles = massSpectraFiles;
	}

	@Override
	public boolean isUsePreOptimization() {

		return usePreOptimization;
	}

	@Override
	public void setUsePreOptimization(boolean usePreOptimization) {

		this.usePreOptimization = usePreOptimization;
	}

	@Override
	public double getThresholdPreOptimization() {

		return thresholdPreOptimization;
	}

	@Override
	public void setThresholdPreOptimization(double thresholdPreOptimization) {

		this.thresholdPreOptimization = thresholdPreOptimization;
	}

	@Override
	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	@Override
	public void setNumberOfTargets(int numberOfTargets) {

		this.numberOfTargets = numberOfTargets;
	}

	@Override
	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	@Override
	public void setMinMatchFactor(float minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	@Override
	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	@Override
	public void setMinReverseMatchFactor(float minReverseMatchFactor) {

		this.minReverseMatchFactor = minReverseMatchFactor;
	}

	@Override
	public boolean isAddUnknownMzListTarget() {

		return addUnknownMzListTarget;
	}

	@Override
	public void setAddUnknownMzListTarget(boolean addUnknownMzListTarget) {

		this.addUnknownMzListTarget = addUnknownMzListTarget;
	}

	@Override
	public String getAlternateIdentifierId() {

		return alternateIdentifierId;
	}

	@Override
	public void setAlternateIdentifierId(String alternateIdentifierId) {

		this.alternateIdentifierId = alternateIdentifierId;
	}
}
