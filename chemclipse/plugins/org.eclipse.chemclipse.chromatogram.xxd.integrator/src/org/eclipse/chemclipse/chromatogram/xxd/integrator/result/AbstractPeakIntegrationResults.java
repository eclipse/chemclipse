/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author eselmeister
 */
public abstract class AbstractPeakIntegrationResults implements IPeakIntegrationResults {

	private List<IPeakIntegrationResult> peakIntegrationResults;

	public AbstractPeakIntegrationResults() {
		peakIntegrationResults = new ArrayList<IPeakIntegrationResult>();
	}

	@Override
	public void add(IPeakIntegrationResult peakIntegrationResult) {

		peakIntegrationResults.add(peakIntegrationResult);
	}

	@Override
	public void remove(IPeakIntegrationResult peakIntegrationResult) {

		peakIntegrationResults.remove(peakIntegrationResult);
	}

	@Override
	public int size() {

		return peakIntegrationResults.size();
	}

	@Override
	public IPeakIntegrationResult getPeakIntegrationResult(int i) {

		if(i >= 0 && i < peakIntegrationResults.size()) {
			return peakIntegrationResults.get(i);
		} else {
			return null;
		}
	}

	// TODO JUnit und absichern
	@Override
	public List<IPeakIntegrationResult> getPeakIntegrationResultList() {

		return peakIntegrationResults;
	}

	@Override
	public List<IPeakIntegrationResult> getPeakIntegrationResultList(int ion) {

		Set<Integer> ions;
		List<IPeakIntegrationResult> results = new ArrayList<IPeakIntegrationResult>();
		for(IPeakIntegrationResult result : peakIntegrationResults) {
			ions = result.getIntegratedTraces();
			if(ions.size() == 1 && ions.contains(ion)) {
				results.add(result);
			}
		}
		return results;
	}

	@Override
	public List<IPeakIntegrationResult> getPeakIntegrationResultThatContains(int ion) {

		Set<Integer> ions;
		List<IPeakIntegrationResult> results = new ArrayList<IPeakIntegrationResult>();
		for(IPeakIntegrationResult result : peakIntegrationResults) {
			ions = result.getIntegratedTraces();
			if(ions.contains(ion)) {
				results.add(result);
			}
		}
		return results;
	}

	@Override
	public double getTotalPeakArea() {

		double integratedArea = 0.0d;
		for(IPeakIntegrationResult result : peakIntegrationResults) {
			integratedArea += result.getIntegratedArea();
		}
		return integratedArea;
	}
}
