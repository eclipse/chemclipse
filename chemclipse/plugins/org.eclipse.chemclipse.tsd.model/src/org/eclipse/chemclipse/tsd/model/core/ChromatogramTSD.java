/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class ChromatogramTSD extends AbstractChromatogramTSD {

	private static final long serialVersionUID = 1133678989144771061L;

	@Override
	public double getPeakIntegratedArea() {

		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

	}
}