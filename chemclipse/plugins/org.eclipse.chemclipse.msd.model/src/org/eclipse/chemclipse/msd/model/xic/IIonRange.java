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
package org.eclipse.chemclipse.msd.model.xic;

/**
 * A ion range represents the start and stop ion value which should be
 * used for example to compare two mass spectra.
 * 
 * @author eselmeister
 */
public interface IIonRange {

	int MIN_ION = 1;
	int MAX_ION = 65535;

	/**
	 * Returns the start mass over charge value (ion).
	 * 
	 * @return int
	 */
	int getStartIon();

	/**
	 * Returns the stop mass over charge value (ion).
	 * 
	 * @return int
	 */
	int getStopIon();
}
