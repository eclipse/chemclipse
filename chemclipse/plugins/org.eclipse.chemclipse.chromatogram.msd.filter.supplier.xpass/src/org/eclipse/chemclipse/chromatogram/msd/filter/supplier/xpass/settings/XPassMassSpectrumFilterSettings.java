/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;

public class XPassMassSpectrumFilterSettings extends AbstractMassSpectrumFilterSettings implements IXPassMassSpectrumFilterSettings {

	private int numberHighest;
	private int numberLowest;

	@Override
	public int getNumberHighest() {

		return numberHighest;
	}

	@Override
	public void setNumberHighest(int numberHighest) {

		this.numberHighest = numberHighest;
	}

	@Override
	public int getNumberLowest() {

		return numberLowest;
	}

	@Override
	public void setNumberLowest(int numberLowest) {

		this.numberLowest = numberLowest;
	}
}
