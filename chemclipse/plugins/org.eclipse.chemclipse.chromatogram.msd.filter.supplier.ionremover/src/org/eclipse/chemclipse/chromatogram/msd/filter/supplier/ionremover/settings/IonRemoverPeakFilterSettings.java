/*******************************************************************************
 * Copyright (c) 2014, 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractPeakFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

public class IonRemoverPeakFilterSettings extends AbstractPeakFilterSettings implements IIonRemoverPeakFilterSettings {

	private IMarkedIons ionsToRemove;

	/**
	 * Initialize the excluded ions instance.
	 */
	public IonRemoverPeakFilterSettings() {
		ionsToRemove = new MarkedIons();
	}

	@Override
	public IMarkedIons getIonsToRemove() {

		return ionsToRemove;
	}
}
