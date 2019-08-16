/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.AbstractScanMSD;
import org.eclipse.chemclipse.msd.model.core.AbstractRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.logging.core.Logger;

/**
 * If a new mass spectrum type should be implemented, extend the abstract class {@link AbstractScanMSD} and not this class.
 * 
 * @author eselmeister
 */
public class RegularMassSpectrum extends AbstractRegularMassSpectrum implements IRegularMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1093372356722684379L;
	private static final Logger logger = Logger.getLogger(RegularMassSpectrum.class);

	// -------------------------------IMassSpectrumCloneable
	@Override
	public IRegularMassSpectrum makeDeepCopy() throws CloneNotSupportedException {

		IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)super.clone();
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
	protected Object clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
	// -------------------------------IMassSpectrumCloneable
}
