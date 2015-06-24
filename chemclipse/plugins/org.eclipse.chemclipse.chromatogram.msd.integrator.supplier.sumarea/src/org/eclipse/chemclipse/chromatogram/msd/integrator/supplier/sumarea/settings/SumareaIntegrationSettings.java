/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.AbstractChromatogramIntegrationSettings;

/**
 * @author eselmeister
 */
public class SumareaIntegrationSettings extends AbstractChromatogramIntegrationSettings implements ISumareaIntegrationSettings {

	private IMarkedIons selectedIons;

	public SumareaIntegrationSettings() {

		selectedIons = new MarkedIons();
	}

	@Override
	public IMarkedIons getSelectedIons() {

		return selectedIons;
	}
}
