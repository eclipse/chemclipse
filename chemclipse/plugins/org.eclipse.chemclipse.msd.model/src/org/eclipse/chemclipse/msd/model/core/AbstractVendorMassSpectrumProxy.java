/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

/**
 * This is a proxy.
 * Some vendor files are just too big: ~100 MB up to 15 GB.
 * Hence, each scan shall be imported just on demand.
 * This proxy encapsulates the normal mass spectrum to ensure that
 * the complete ion data is loaded when it is needed.
 *
 */
public abstract class AbstractVendorMassSpectrumProxy extends AbstractVendorMassSpectrum implements IVendorMassSpectrumProxy {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 6994933565324921239L;
	//
	private boolean isProxy = true;
	/*
	 * Both values are used in proxy modus to display
	 * the chromatogram TIC.
	 */
	private int numberOfIons;
	private float totalSignal;

	@Override
	public void setNumberOfIons(int numberOfIons) {

		this.numberOfIons = numberOfIons;
	}

	@Override
	public void setTotalSignal(float totalSignal) {

		this.totalSignal = totalSignal;
	}

	@Override
	public int getNumberOfIons() {

		if(isProxy) {
			return numberOfIons;
		} else {
			return super.getNumberOfIons();
		}
	}

	@Override
	public float getTotalSignal() {

		if(isProxy) {
			return totalSignal;
		} else {
			return super.getTotalSignal();
		}
	}

	@Override
	public boolean hasIons() {

		checkProxyAndImportOnDemand();
		return super.hasIons();
	}

	@Override
	public void enforceLoadScanProxy() {

		checkProxyAndImportOnDemand();
	}

	@Override
	public List<IIon> getIons() {

		checkProxyAndImportOnDemand();
		return super.getIons();
	}

	@Override
	public AbstractVendorMassSpectrumProxy addIons(List<IIon> ions, boolean addIntensities) {

		checkProxyAndImportOnDemand();
		super.addIons(ions, addIntensities);
		return this;
	}

	@Override
	public AbstractVendorMassSpectrumProxy addIon(IIon ion, boolean checked) {

		checkProxyAndImportOnDemand();
		super.addIon(ion, checked);
		return this;
	}

	@Override
	public AbstractVendorMassSpectrumProxy removeIon(IIon ion) {

		checkProxyAndImportOnDemand();
		super.removeIon(ion);
		return this;
	}

	@Override
	public AbstractVendorMassSpectrumProxy removeAllIons() {

		checkProxyAndImportOnDemand();
		super.removeAllIons();
		return this;
	}

	@Override
	public AbstractVendorMassSpectrumProxy removeIon(int ion) {

		checkProxyAndImportOnDemand();
		super.removeIon(ion);
		return this;
	}

	@Override
	public AbstractVendorMassSpectrumProxy removeIons(Set<Integer> ions) {

		checkProxyAndImportOnDemand();
		super.removeIons(ions);
		return this;
	}

	@Override
	public AbstractVendorMassSpectrumProxy removeIons(IMarkedIons excludedIons) {

		checkProxyAndImportOnDemand();
		super.removeIons(excludedIons);
		return this;
	}

	@Override
	public float getTotalSignal(IMarkedIons excludedIons) {

		checkProxyAndImportOnDemand();
		return super.getTotalSignal(excludedIons);
	}

	@Override
	public double getBasePeak() {

		checkProxyAndImportOnDemand();
		return super.getBasePeak();
	}

	@Override
	public float getBasePeakAbundance() {

		checkProxyAndImportOnDemand();
		return super.getBasePeakAbundance();
	}

	@Override
	public IIon getHighestAbundance() {

		checkProxyAndImportOnDemand();
		return super.getHighestAbundance();
	}

	@Override
	public IIon getHighestIon() {

		checkProxyAndImportOnDemand();
		return super.getHighestIon();
	}

	@Override
	public IIon getLowestAbundance() {

		checkProxyAndImportOnDemand();
		return super.getLowestAbundance();
	}

	@Override
	public IIon getLowestIon() {

		checkProxyAndImportOnDemand();
		return super.getLowestIon();
	}

	@Override
	public IIonBounds getIonBounds() {

		checkProxyAndImportOnDemand();
		return super.getIonBounds();
	}

	@Override
	public IIon getIon(int ion) throws AbundanceLimitExceededException, IonLimitExceededException {

		checkProxyAndImportOnDemand();
		return super.getIon(ion);
	}

	@Override
	public IIon getIon(double ion) throws AbundanceLimitExceededException, IonLimitExceededException {

		checkProxyAndImportOnDemand();
		return super.getIon(ion);
	}

	@Override
	public IIon getIon(double ion, int precision) throws AbundanceLimitExceededException, IonLimitExceededException {

		checkProxyAndImportOnDemand();
		return super.getIon(ion, precision);
	}

	@Override
	public void adjustIons(float percentage) {

		checkProxyAndImportOnDemand();
		super.adjustIons(percentage);
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		checkProxyAndImportOnDemand();
		super.adjustTotalSignal(totalSignal);
	}

	@Override
	public IScanMSD getMassSpectrum(IMarkedIons excludedIons) {

		checkProxyAndImportOnDemand();
		return super.getMassSpectrum(excludedIons);
	}

	@Override
	public IScanMSD normalize() {

		checkProxyAndImportOnDemand();
		return super.normalize();
	}

	@Override
	public IScanMSD normalize(float base) {

		checkProxyAndImportOnDemand();
		return super.normalize(base);
	}

	private void checkProxyAndImportOnDemand() {

		if(isProxy) {
			isProxy = false;
			importIons();
		}
	}
}
