/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.Objects;

import org.eclipse.chemclipse.msd.model.core.AbstractPeakIon;
import org.eclipse.chemclipse.msd.model.core.IIonMSn;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;

/**
 * A peak ion that keeps the ion transition (e.g. 107 &gt; 91.0 @20) of tandem MS data.
 */
public class PeakIonMSn extends AbstractPeakIon implements IIonMSn {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 3189264528801943212L;
	//
	private IIonTransition ionTransition;

	public PeakIonMSn(double ion, float abundance, IIonTransition ionTransition) throws NullPointerException {

		super(ion, abundance);
		if(ionTransition != null) {
			this.ionTransition = ionTransition;
		} else {
			throw new NullPointerException("The given ion transition instance should be not null.");
		}
	}

	public PeakIonMSn(IIonMSn ion) throws NullPointerException {

		this(ion.getIon(), ion.getAbundance(), ion.getIonTransition());
	}

	@Override
	public IIonTransition getIonTransition() {

		return ionTransition;
	}

	@Override
	public boolean equals(Object otherObject) {

		if(!super.equals(otherObject)) {
			return false;
		}
		PeakIonMSn other = (PeakIonMSn)otherObject;
		return Objects.equals(ionTransition, other.getIonTransition());
	}

	@Override
	public int hashCode() {

		return super.hashCode() + Objects.hashCode(ionTransition);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("ion=" + getIon());
		builder.append(",");
		builder.append("abundance=" + getAbundance());
		builder.append(",");
		builder.append("ionTransition=" + ionTransition);
		builder.append("]");
		return builder.toString();
	}
}
