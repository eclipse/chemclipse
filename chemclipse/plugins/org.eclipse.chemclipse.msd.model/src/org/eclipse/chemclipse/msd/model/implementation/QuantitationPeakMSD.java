/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public class QuantitationPeakMSD extends AbstractQuantitationPeak implements IQuantitationPeak {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -4605765688648534473L;

	public QuantitationPeakMSD(IPeakMSD referencePeakMSD, double concentration, String concentrationUnit) {
		super(referencePeakMSD, concentration, concentrationUnit);
	}
}
