/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.model.core;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;

public abstract class AbstractChromatogramTSD extends AbstractChromatogram<IChromatogramPeakTSD> implements IChromatogramTSD {

	private static final long serialVersionUID = 7761066909104550509L;
	private IChromatogram<?> chromatogram = null;

	public AbstractChromatogramTSD() {

		this(null);
	}

	public AbstractChromatogramTSD(IChromatogram<?> chromatogram) {

		this.chromatogram = chromatogram;
	}

	@Override
	public IChromatogram<?> getChromatogram() {

		return chromatogram;
	}
}