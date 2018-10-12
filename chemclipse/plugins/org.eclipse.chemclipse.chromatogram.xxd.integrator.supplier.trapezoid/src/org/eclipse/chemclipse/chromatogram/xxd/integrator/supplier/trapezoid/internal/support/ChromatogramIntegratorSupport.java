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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.BackgroundIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.IBackgroundIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.IChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.implementation.IntegrationEntryCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegratorSupport {

	public static final String INTEGRATOR_DESCRIPTION = "Integrator Trapezoid";

	@SuppressWarnings("rawtypes")
	public IChromatogramIntegrationResults calculateChromatogramIntegrationResults(IChromatogramSelection chromatogramSelection, ChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		/*
		 * Get the chromatogram and background area.
		 */
		IIntegrationEntry chromatogramIntegrationEntry = calculateChromatogramIntegrationEntry(chromatogramSelection, monitor);
		IIntegrationEntry backgroundIntegrationEntry = calculateBackgroundIntegrationEntry(chromatogramSelection, monitor);
		//
		double chromatogramArea = 0;
		double backgroundArea = 0;
		/*
		 * Chromatogram Area
		 */
		if(chromatogramIntegrationEntry != null) {
			chromatogramArea = chromatogramIntegrationEntry.getIntegratedArea();
		}
		/*
		 * Background Area
		 */
		if(backgroundIntegrationEntry != null) {
			backgroundArea = backgroundIntegrationEntry.getIntegratedArea();
		}
		/*
		 * Chromatogram Results
		 */
		IChromatogramIntegrationResults chromatogramIntegrationResults = new ChromatogramIntegrationResults();
		IChromatogramIntegrationResult chromatogramIntegrationResult = new ChromatogramIntegrationResult(chromatogramArea, backgroundArea);
		chromatogramIntegrationResults.add(chromatogramIntegrationResult);
		return chromatogramIntegrationResults;
	}

	/**
	 * May return null.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private IIntegrationEntry calculateChromatogramIntegrationEntry(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		monitor.subTask("Integrate the chromatogram area");
		IChromatogramIntegrator chromatogramIntegrator = new ChromatogramIntegrator();
		double chromatogramArea = chromatogramIntegrator.integrate(chromatogramSelection);
		/*
		 * Create the MSD/FID entry.
		 */
		IIntegrationEntry chromatogramIntegrationEntry = null;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			chromatogramIntegrationEntry = new IntegrationEntryMSD(AbstractIon.TIC_ION, chromatogramArea);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			chromatogramIntegrationEntry = new IntegrationEntryCSD(chromatogramArea);
		}
		//
		if(chromatogramIntegrationEntry != null) {
			List<IIntegrationEntry> chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
			chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			chromatogram.setChromatogramIntegratedArea(chromatogramIntegrationEntries, INTEGRATOR_DESCRIPTION);
		}
		//
		return chromatogramIntegrationEntry;
	}

	@SuppressWarnings("rawtypes")
	private IIntegrationEntry calculateBackgroundIntegrationEntry(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		monitor.subTask("Integrate the background area");
		IBackgroundIntegrator backgroundIntegrator = new BackgroundIntegrator();
		double backgroundArea = backgroundIntegrator.integrate(chromatogramSelection);
		/*
		 * Create the MSD/FID entry.
		 */
		IIntegrationEntry backgroundIntegrationEntry = null;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			backgroundIntegrationEntry = new IntegrationEntryMSD(AbstractIon.TIC_ION, backgroundArea);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			backgroundIntegrationEntry = new IntegrationEntryCSD(backgroundArea);
		}
		//
		if(backgroundIntegrationEntry != null) {
			List<IIntegrationEntry> backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
			backgroundIntegrationEntries.add(backgroundIntegrationEntry);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			chromatogram.setBackgroundIntegratedArea(backgroundIntegrationEntries, INTEGRATOR_DESCRIPTION);
		}
		//
		return backgroundIntegrationEntry;
	}
}
