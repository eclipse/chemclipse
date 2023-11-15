/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.chromatogram.model;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.tsd.model.core.AbstractChromatogramTSD;

public abstract class AbstractChromatogramAdapterTSD extends AbstractChromatogramTSD {

	private static final long serialVersionUID = 7181095941633328215L;

	public AbstractChromatogramAdapterTSD(IChromatogram<?> chromatogram) {

		super(chromatogram);
	}

	@Override
	public double getPeakIntegratedArea() {

		double integratedArea = 0.0d;
		IChromatogram<?> chromatogram = getChromatogram();
		if(chromatogram != null) {
			integratedArea = chromatogram.getPeakIntegratedArea();
		}
		//
		return integratedArea;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

	}
}