/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author eselmeister
 */
public class FilterChromatogramRunnable extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String description = "Ion Remover Chromatogram Filter";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.chromatogram";

	public FilterChromatogramRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)getChromatogramSelection();
			/*
			 * The filter settings.
			 */
			ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
			IMarkedIons excludedIons = chromatogramFilterSettings.getIonsToRemove();
			/*
			 * Check whether to use the ions, stored in the settings or to
			 * use the ions stored in each's chromatogram options page
			 * (excluded ions).
			 */
			Set<Integer> ions = new HashSet<Integer>();
			if(PreferenceSupplier.useSettings()) {
				ions = PreferenceSupplier.getIons();
			} else {
				/*
				 * Try to retrieve the ions, the user has set in the
				 * chromatogram option page.
				 */
				if(chromatogramSelection != null) {
					ions = chromatogramSelection.getExcludedIons().getIonsNominal();
				}
			}
			/*
			 * Set the ions that will be removed.
			 */
			for(int ion : ions) {
				excludedIons.add(new MarkedIon(ion));
			}
			/*
			 * Apply the filter.
			 */
			final IChromatogramFilterProcessingInfo processingInfo = ChromatogramFilterMSD.applyFilter(chromatogramSelection, chromatogramFilterSettings, FILTER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	// ----------------------------------------------------------------------IRunnableWithProgress
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask("Ion Remover Filter", IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
	// ----------------------------------------------------------------------IRunnableWithProgress
}
