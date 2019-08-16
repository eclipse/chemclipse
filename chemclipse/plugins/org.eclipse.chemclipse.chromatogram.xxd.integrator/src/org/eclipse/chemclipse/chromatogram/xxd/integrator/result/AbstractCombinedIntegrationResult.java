/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

public abstract class AbstractCombinedIntegrationResult implements ICombinedIntegrationResult {

	private IChromatogramIntegrationResults chromatogramIntegrationResults;
	private IPeakIntegrationResults peakIntegrationResults;

	public AbstractCombinedIntegrationResult(IChromatogramIntegrationResults chromatogramIntegrationResults, IPeakIntegrationResults peakIntegrationResults) {
		this.chromatogramIntegrationResults = chromatogramIntegrationResults;
		this.peakIntegrationResults = peakIntegrationResults;
	}

	@Override
	public IChromatogramIntegrationResults getChromatogramIntegrationResults() {

		return chromatogramIntegrationResults;
	}

	@Override
	public IPeakIntegrationResults getPeakIntegrationResults() {

		return peakIntegrationResults;
	}
}
