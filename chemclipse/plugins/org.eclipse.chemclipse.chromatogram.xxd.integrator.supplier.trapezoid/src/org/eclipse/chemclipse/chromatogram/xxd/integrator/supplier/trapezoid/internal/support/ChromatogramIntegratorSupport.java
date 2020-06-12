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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.BackgroundIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegratorSupport {

	public static final String INTEGRATOR_DESCRIPTION = "Trapezoid";

	@SuppressWarnings("rawtypes")
	public IChromatogramIntegrationResults calculateChromatogramIntegrationResults(IChromatogramSelection chromatogramSelection, ChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		/*
		 * Get the chromatogram and background area.
		 */
		List<IIntegrationEntry> chromatogramIntegrationEntries = calculateChromatogramIntegrationEntry(chromatogramSelection, monitor);
		List<IIntegrationEntry> backgroundIntegrationEntries = calculateBackgroundIntegrationEntry(chromatogramSelection, monitor);
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		chromatogram.setIntegratedArea(chromatogramIntegrationEntries, backgroundIntegrationEntries, INTEGRATOR_DESCRIPTION);
		/*
		 * Chromatogram Results
		 */
		double chromatogramArea = getArea(chromatogramIntegrationEntries);
		double backgroundArea = getArea(backgroundIntegrationEntries);
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
	private List<IIntegrationEntry> calculateChromatogramIntegrationEntry(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		List<IIntegrationEntry> chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
		ChromatogramIntegrator chromatogramIntegrator = new ChromatogramIntegrator();
		double chromatogramArea = chromatogramIntegrator.integrate(chromatogramSelection);
		/*
		 * Create the MSD/FID entry.
		 */
		IIntegrationEntry chromatogramIntegrationEntry = null;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			chromatogramIntegrationEntry = new IntegrationEntry(ISignal.TOTAL_INTENSITY, chromatogramArea);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			chromatogramIntegrationEntry = new IntegrationEntry(chromatogramArea);
		}
		//
		if(chromatogramIntegrationEntry != null) {
			chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		}
		//
		return chromatogramIntegrationEntries;
	}

	@SuppressWarnings("rawtypes")
	private List<IIntegrationEntry> calculateBackgroundIntegrationEntry(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		List<IIntegrationEntry> backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
		BackgroundIntegrator backgroundIntegrator = new BackgroundIntegrator();
		double backgroundArea = backgroundIntegrator.integrate(chromatogramSelection);
		/*
		 * Create the MSD/FID entry.
		 */
		IIntegrationEntry backgroundIntegrationEntry = null;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			backgroundIntegrationEntry = new IntegrationEntry(ISignal.TOTAL_INTENSITY, backgroundArea);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			backgroundIntegrationEntry = new IntegrationEntry(backgroundArea);
		}
		//
		if(backgroundIntegrationEntry != null) {
			backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		}
		//
		return backgroundIntegrationEntries;
	}

	private double getArea(List<IIntegrationEntry> integrationEntries) {

		double area = 0.0d;
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			area += integrationEntry.getIntegratedArea();
		}
		return area;
	}
}
