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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.AbstractIntegrationSettings;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractPeakIntegrationSettings extends AbstractIntegrationSettings implements IPeakIntegrationSettings, IReportDecider {

	@JsonIgnore
	private IMarkedIons selectedIons;
	@JsonIgnore
	private IAreaSupport areaSupport;
	@JsonIgnore
	private IIntegrationSupport integratorSupport;
	@JsonIgnore
	private List<IReportDecider> reportDeciders;

	public AbstractPeakIntegrationSettings() {
		selectedIons = new MarkedIons();
		areaSupport = new AreaSupport();
		integratorSupport = new IntegrationSupport();
		reportDeciders = new ArrayList<IReportDecider>();
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
		if(peak instanceof IPeakMSD) {
			startRetentionTime = ((IPeakMSD)peak).getPeakModel().getStartRetentionTime();
		} else if(peak instanceof IPeakCSD) {
			startRetentionTime = ((IPeakCSD)peak).getPeakModel().getStartRetentionTime();
		} else if(peak instanceof IPeakWSD) {
			startRetentionTime = ((IPeakWSD)peak).getPeakModel().getStartRetentionTime();
		}
		//
		boolean sumOn = getAreaSupport().isAreaSumOn(startRetentionTime);
		ISettingStatus status = new SettingStatus(report, sumOn);
		return status;
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
	public IMarkedIons getSelectedIons() {

		return selectedIons;
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
