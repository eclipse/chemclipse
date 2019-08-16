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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.internal.core.settings.IRetentionTimeRange;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.internal.core.settings.RetentionTimeRange;

public class AreaSupport implements IAreaSupport {

	private double minimumArea = IAreaSupport.INITIAL_AREA_REJECT;
	private List<IRetentionTimeRange> ranges;

	public AreaSupport() {
		ranges = new ArrayList<IRetentionTimeRange>();
	}

	// ----------------------------------------IAreaSupport
	@Override
	public void setAreaSumOn(int startRetentionTime, int stopRetentionTime) {

		IRetentionTimeRange retentionTimeRange = new RetentionTimeRange(startRetentionTime, stopRetentionTime);
		ranges.add(retentionTimeRange);
	}

	@Override
	public boolean isAreaSumOn(int startRetentionTime) {

		boolean areaSumOn = false;
		exitloop:
		for(IRetentionTimeRange range : ranges) {
			if(startRetentionTime >= range.getStartRetentionTime() && startRetentionTime <= range.getStopRetentionTime()) {
				areaSumOn = true;
				break exitloop;
			}
		}
		return areaSumOn;
	}

	@Override
	public void setMinimumArea(double minimumArea) {

		if(minimumArea >= 0.0d) {
			this.minimumArea = minimumArea;
		}
	}

	@Override
	public double getMinimumArea() {

		return minimumArea;
	}

	@Override
	public void reset() {

		minimumArea = IAreaSupport.INITIAL_AREA_REJECT;
		resetAreaSumOn();
	}

	@Override
	public void resetAreaSumOn() {

		ranges.clear();
	}

	// ----------------------------------------IAreaSupport
	// ----------------------------------------IReportDecider
	@Override
	public boolean report(IPeak peak) {

		if(peak == null) {
			return false;
		}
		if(peak.getIntegratedArea() < minimumArea) {
			return false;
		}
		return true;
	}
	// ----------------------------------------IReportDecider
}
