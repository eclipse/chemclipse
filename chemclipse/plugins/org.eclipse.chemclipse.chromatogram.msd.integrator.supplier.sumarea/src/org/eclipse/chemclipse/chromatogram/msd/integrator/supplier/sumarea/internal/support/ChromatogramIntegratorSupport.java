/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core.BackgroundIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core.ISumareaIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.support.util.IonSettingUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegratorSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegratorSupport.class);
	public static String INTEGRATOR_DESCRIPTION = "SumArea Integrator";

	public IChromatogramIntegrationResults calculateChromatogramIntegrationResults(IChromatogramSelectionMSD chromatogramSelection, ChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		boolean integrateAll = false;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		IExtractedIonSignalExtractor extractedIonSignalExtractor;
		try {
			extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			int startIon = extractedIonSignals.getStartIon();
			int stopIon = extractedIonSignals.getStopIon();
			/*
			 * Each ion result will be added to the results instance.
			 */
			IChromatogramIntegrationResults chromatogramIntegrationResults = new ChromatogramIntegrationResults();
			/*
			 * Selected Ions (size == 0 : integrate all)
			 */
			IMarkedIons selectedIons = getSelectedIons(chromatogramIntegrationSettings);
			Set<Integer> selectedIonsNominal = selectedIons.getIonsNominal();
			if(selectedIonsNominal.size() == 0 || selectedIonsNominal.contains(0)) {
				integrateAll = true;
			}
			/*
			 * Calculate area.
			 */
			ISumareaIntegrator chromatogramIntegrator = new ChromatogramIntegrator();
			ISumareaIntegrator backgroundIntegrator = new BackgroundIntegrator();
			/*
			 * Chromatogram Integration Entries
			 */
			List<IIntegrationEntry> chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
			List<IIntegrationEntry> backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
			/*
			 * TODO Optimize
			 */
			for(int ion = startIon; ion <= stopIon; ion++) {
				monitor.subTask("Integrate the chromatogram area of ion: " + ion);
				if(integrateAll) {
					calculateIntegrationResults(ion, chromatogramIntegrator, backgroundIntegrator, chromatogramSelection, chromatogramIntegrationEntries, backgroundIntegrationEntries, chromatogramIntegrationResults);
				} else if(selectedIonsNominal.contains(ion)) {
					calculateIntegrationResults(ion, chromatogramIntegrator, backgroundIntegrator, chromatogramSelection, chromatogramIntegrationEntries, backgroundIntegrationEntries, chromatogramIntegrationResults);
				}
			}
			/*
			 * Set the integration entries.
			 */
			chromatogram.setIntegratedArea(chromatogramIntegrationEntries, backgroundIntegrationEntries, INTEGRATOR_DESCRIPTION);
			//
			return chromatogramIntegrationResults;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
			return null;
		}
	}

	/*
	 * TODO Optimize
	 */
	private void calculateIntegrationResults(int ion, ISumareaIntegrator chromatogramIntegrator, ISumareaIntegrator backgroundIntegrator, IChromatogramSelectionMSD chromatogramSelection, List<IIntegrationEntry> chromatogramIntegrationEntries, List<IIntegrationEntry> backgroundIntegrationEntries, IChromatogramIntegrationResults chromatogramIntegrationResults) {

		IIntegrationEntry chromatogramIntegrationEntry = calculateChromatogramIonArea(ion, chromatogramIntegrator, chromatogramSelection);
		IIntegrationEntry backgroundIntegrationEntry = calculateBackgroundIonArea(ion, backgroundIntegrator, chromatogramSelection);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		setIntegrationResult(ion, chromatogramIntegrationResults, chromatogramIntegrationEntry.getIntegratedArea(), backgroundIntegrationEntry.getIntegratedArea());
	}

	private void setIntegrationResult(int ion, IChromatogramIntegrationResults chromatogramIntegrationResults, double chromatogramArea, double backgroundArea) {

		/*
		 * Result
		 */
		IChromatogramIntegrationResult chromatogramIntegrationResult = new ChromatogramIntegrationResult(ion, chromatogramArea, backgroundArea);
		chromatogramIntegrationResults.add(chromatogramIntegrationResult);
	}

	private IIntegrationEntry calculateChromatogramIonArea(int ion, ISumareaIntegrator chromatogramIntegrator, IChromatogramSelectionMSD chromatogramSelection) {

		double chromatogramArea = chromatogramIntegrator.integrate(chromatogramSelection, ion);
		IIntegrationEntry chromatogramIntegrationEntry = new IntegrationEntry(ion, chromatogramArea);
		return chromatogramIntegrationEntry;
	}

	private IIntegrationEntry calculateBackgroundIonArea(int ion, ISumareaIntegrator backgroundIntegrator, IChromatogramSelectionMSD chromatogramSelection) {

		double backgroundArea = backgroundIntegrator.integrate(chromatogramSelection, ion);
		IIntegrationEntry backgroundIntegrationEntry = new IntegrationEntry(ion, backgroundArea);
		return backgroundIntegrationEntry;
	}

	private IMarkedIons getSelectedIons(ChromatogramIntegrationSettings chromatogramIntegrationSettings) {

		String ions = chromatogramIntegrationSettings.getSelectedIons();
		IonSettingUtil ionSettingUtil = new IonSettingUtil();
		return new MarkedIons(ionSettingUtil.extractIons(ionSettingUtil.deserialize(ions)), IMarkedIons.IonMarkMode.INCLUDE);
	}
}
