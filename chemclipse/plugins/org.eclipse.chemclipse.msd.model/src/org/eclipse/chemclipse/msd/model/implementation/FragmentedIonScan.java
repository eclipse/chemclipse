/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.IFragmentedIonScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

/**
 * Default implementation for {@link IFragmentedIonScan}.
 * 
 * @author <a href="mailto:alexander.kerner@openchrom.net">Alexander Kerner</a>
 *
 */
public class FragmentedIonScan extends ScanMSD implements IFragmentedIonScan {

	private static final long serialVersionUID = -484619308862079562L;
	private double precursorIon = Double.NaN;

	public FragmentedIonScan() {
		super();
	}

	public FragmentedIonScan(IScanMSD templateScan) {
		super(templateScan);
	}

	@Override
	public double getPrecursorIon() {

		return precursorIon;
	}

	@Override
	public FragmentedIonScan setPrecursorIon(double precursorIon) {

		this.precursorIon = precursorIon;
		return this;
	}
}
