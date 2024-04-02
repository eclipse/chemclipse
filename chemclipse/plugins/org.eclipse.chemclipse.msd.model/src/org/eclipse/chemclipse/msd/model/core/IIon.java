/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.core.runtime.IAdaptable;

/**
 * This Interface declares a simple ion.<br/>
 * For example:<br/>
 * Nitrogen: N2<br/>
 * ion -> 28.00<br/>
 * abundance -> 1000.00<br/>
 * High resolution mass spectra can also be represented, as for ion and
 * abundance are stored as float values. <br/>
 * Class structure of ion:<br/>
 * <br/>
 * [AbstractIon]<br/>
 * implements (IIon)<br/>
 * |---------------------------------------------<br/>
 * | |<br/>
 * [AbstractSupplierIon] [AbstractPeakIon] implements
 * (ISupplierIon) implements (IPeakIon) |<br/>
 * |<br/>
 * -------------------------------------------------<br/>
 * | | |<br/>
 * [AgilentIon] [NetCDFIon] [MzXMLIon]<br/>
 *
 * @author Philip Wenig
 * @author Alexander Kerner
 */
public interface IIon extends IIonSerializable, IAdaptable, Comparable<IIon> {

	String TIC_DESCRIPTION = "TIC";
	double TIC_ION = 0.0d;
	float ZERO_INTENSITY = 0.0f;

	/**
	 * Returns the value of the actual ion.
	 *
	 * @return double
	 */
	double getIon();

	/**
	 * Sets the new value of the ion.
	 *
	 * @param ion
	 *            - The new Value of the ion
	 * @return true if ion is valid
	 */
	boolean setIon(double ion);

	/**
	 * Returns the actual abundance of the ion.
	 *
	 * @return float - abundance
	 */
	float getAbundance();

	/**
	 * Sets an abundance value for the ion.
	 *
	 * @param abundance
	 * @return true if the value is valid
	 */
	boolean setAbundance(float abundance);

	/**
	 * Returns the ion transition.
	 * If no triple quadrupole / ion transition is used, null will be returned.
	 *
	 * @return {@link IIonTransition}
	 */
	IIonTransition getIonTransition();
}
