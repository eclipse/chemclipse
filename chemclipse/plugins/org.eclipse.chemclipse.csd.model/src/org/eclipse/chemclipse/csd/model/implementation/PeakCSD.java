/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.implementation;

import org.eclipse.chemclipse.csd.model.core.AbstractPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;

public class PeakCSD extends AbstractPeakCSD implements IPeakCSD {

	public PeakCSD(IPeakModelCSD peakModel, String modelDescription) throws IllegalArgumentException {

		super(peakModel, modelDescription);
	}

	public PeakCSD(IPeakModelCSD peakModel) throws IllegalArgumentException {

		super(peakModel);
	}
}