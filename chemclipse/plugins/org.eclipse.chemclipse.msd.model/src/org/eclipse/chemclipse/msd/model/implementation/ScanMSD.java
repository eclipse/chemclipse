/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.AbstractScanMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

/**
 * If a new mass spectrum type should be implemented, extend the abstract class {@link AbstractScanMSD} and not this class.
 * 
 * @author Philip Wenig
 * @author Alexander Kerner
 */
public class ScanMSD extends AbstractScanMSD implements IScanMSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1794251778820195779L;
	private static final Logger logger = Logger.getLogger(ScanMSD.class);

	public ScanMSD() {
		super();
	}

	public ScanMSD(final Collection<? extends IIon> ions) {

		super(ions);
	}
	
	/**
	 * Creates a new instance of {@code ScanMSD} by creating a
	 * shallow copy of provided {@code templateScan}.
	 * </p>
	 * To create a deep copy, use {@link #makeDeepCopy()}.
	 * 
	 * @param templateScan
	 *            {@link IScanMSD scan} that is used as a template
	 */
	public ScanMSD(IScanMSD templateScan) {
		super(templateScan);
	}

	// -------------------------------IMassSpectrumCloneable
	@Override
	public IScanMSD makeDeepCopy() throws CloneNotSupportedException {

		IScanMSD massSpectrum = (IScanMSD)super.clone();
		IIon defaultIon;
		/*
		 * The instance variables have been copied by super.clone();.<br/> The
		 * ions in the ion list need not to be removed via
		 * removeAllIons as the method super.clone() has created a new
		 * list.<br/> It is necessary to fill the list again, as the abstract
		 * super class does not know each available type of ion.<br/>
		 * Make a deep copy of all ions.
		 */
		for(IIon ion : getIons()) {
			try {
				defaultIon = new Ion(ion.getIon(), ion.getAbundance());
				massSpectrum.addIon(defaultIon);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
		return massSpectrum;
	}

	@Override
	public ScanMSD addIons(List<IIon> ions, boolean addIntensities) {

		super.addIons(ions, addIntensities);
		return this;
	}

	@Override
	public ScanMSD addIon(IIon ion) {

		super.addIon(ion);
		return this;
	}

	@Override
	public ScanMSD addIon(IIon ion, boolean checked) {

		super.addIon(ion, checked);
		return this;
	}

	@Override
	public ScanMSD addIon(boolean addIntensity, IIon ion) {

		super.addIon(addIntensity, ion);
		return this;
	}

	@Override
	protected IScanMSD clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
	// -------------------------------IMassSpectrumCloneable
}
