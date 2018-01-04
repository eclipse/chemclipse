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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChromatogramIntegrationResults implements IChromatogramIntegrationResults {

	private List<IChromatogramIntegrationResult> chromatogramIntegrationResults;

	public AbstractChromatogramIntegrationResults() {
		chromatogramIntegrationResults = new ArrayList<IChromatogramIntegrationResult>();
	}

	@Override
	public void add(IChromatogramIntegrationResult chromatogramIntegrationResult) {

		chromatogramIntegrationResults.add(chromatogramIntegrationResult);
	}

	@Override
	public void remove(IChromatogramIntegrationResult chromatogramIntegrationResult) {

		chromatogramIntegrationResults.remove(chromatogramIntegrationResult);
	}

	@Override
	public List<IChromatogramIntegrationResult> getChromatogramIntegrationResultList() {

		return chromatogramIntegrationResults;
	}

	@Override
	public IChromatogramIntegrationResult getChromatogramIntegrationResult(int index) {

		if(index >= 0 && index < size()) {
			return chromatogramIntegrationResults.get(index);
		}
		return null;
	}

	@Override
	public int size() {

		return chromatogramIntegrationResults.size();
	}

	@Override
	public double getTotalChromatogramArea() {

		double integratedArea = 0.0d;
		for(IChromatogramIntegrationResult result : chromatogramIntegrationResults) {
			integratedArea += result.getChromatogramArea();
		}
		return integratedArea;
	}

	@Override
	public double getTotalBackgroundArea() {

		double integratedArea = 0.0d;
		for(IChromatogramIntegrationResult result : chromatogramIntegrationResults) {
			integratedArea += result.getBackgroundArea();
		}
		return integratedArea;
	}
}
