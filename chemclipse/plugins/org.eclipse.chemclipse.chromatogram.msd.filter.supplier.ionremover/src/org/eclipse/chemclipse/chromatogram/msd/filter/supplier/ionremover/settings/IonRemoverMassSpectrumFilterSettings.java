/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

public class IonRemoverMassSpectrumFilterSettings extends AbstractMassSpectrumFilterSettings implements IIonRemoverMassSpectrumFilterSettings {

	private IMarkedIons ionsToRemove;

	/**
	 * Initialize the excluded ions instance.
	 */
	public IonRemoverMassSpectrumFilterSettings() {
		ionsToRemove = new MarkedIons();
	}

	@Override
	public IMarkedIons getIonsToRemove() {

		return ionsToRemove;
	}
}
