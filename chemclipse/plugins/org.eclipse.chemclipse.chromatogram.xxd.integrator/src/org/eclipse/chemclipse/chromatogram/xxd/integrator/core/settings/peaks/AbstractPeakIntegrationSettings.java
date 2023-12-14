/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.AbstractIntegrationSettings;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.MarkedTraces;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractPeakIntegrationSettings extends AbstractIntegrationSettings implements IPeakIntegrationSettings, IReportDecider {

	@JsonIgnore
	private IMarkedTraces<IMarkedTrace> markedTraces = new MarkedTraces();
	@JsonIgnore
	private IAreaSupport areaSupport;
	@JsonIgnore
	private IIntegrationSupport integratorSupport;
	@JsonIgnore
	private List<IReportDecider> reportDeciders;

	public AbstractPeakIntegrationSettings() {

		areaSupport = new AreaSupport();
		integratorSupport = new IntegrationSupport();
		reportDeciders = new ArrayList<>();
		/*
		 * The report decider support decisions about integrating or not
		 * integrating a peak.
		 */
		reportDeciders.add(areaSupport);
		reportDeciders.add(integratorSupport);
	}

	@Override
	public ISettingStatus getSettingStatus(IPeak peak) {

		boolean report = report(peak);
		int startRetentionTime = 0;
		if(peak instanceof IPeakMSD peakMSD) {
			startRetentionTime = peakMSD.getPeakModel().getStartRetentionTime();
		} else if(peak instanceof IPeakCSD peakCSD) {
			startRetentionTime = peakCSD.getPeakModel().getStartRetentionTime();
		} else if(peak instanceof IPeakWSD peakWSD) {
			startRetentionTime = peakWSD.getPeakModel().getStartRetentionTime();
		}
		//
		boolean sumOn = getAreaSupport().isAreaSumOn(startRetentionTime);
		return new SettingStatus(report, sumOn);
	}

	@Override
	public void addReportDecider(IReportDecider reportDecider) {

		reportDeciders.add(reportDecider);
	}

	@Override
	public void removeReportDecider(IReportDecider reportDecider) {

		reportDeciders.remove(reportDecider);
	}

	@Override
	public IMarkedTraces<IMarkedTrace> getMarkedTraces() {

		return markedTraces;
	}

	@Override
	public IAreaSupport getAreaSupport() {

		return areaSupport;
	}

	@Override
	public IIntegrationSupport getIntegrationSupport() {

		return integratorSupport;
	}

	@Override
	public boolean report(IPeak peak) {

		boolean report = true;
		/*
		 * If at least one report decider says no to report this peak, the peak
		 * will not be considered to be reported.
		 */
		exitloop:
		for(IReportDecider reportDecider : reportDeciders) {
			if(!reportDecider.report(peak)) {
				report = false;
				break exitloop;
			}
		}
		return report;
	}
}
