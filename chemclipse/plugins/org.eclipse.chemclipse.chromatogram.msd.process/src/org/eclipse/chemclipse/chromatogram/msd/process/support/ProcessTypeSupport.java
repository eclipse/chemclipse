/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.ChromatogramIdentifierTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.ChromatogramIntegratorTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.ClassifierTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.CombinedIntegratorTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.PeakDetectorTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.PeakFilterTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.PeakIdentifierTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.internal.support.PeakQuantitationTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class ProcessTypeSupport {

	private static final Logger logger = Logger.getLogger(ProcessTypeSupport.class);
	private List<IProcessTypeSupplier> processTypeSuppliers;

	public ProcessTypeSupport() {
		processTypeSuppliers = new ArrayList<IProcessTypeSupplier>();
		//
		processTypeSuppliers.add(new BaselineDetectorTypeSupplier());
		processTypeSuppliers.add(new ChromatogramIdentifierTypeSupplier());
		processTypeSuppliers.add(new ChromatogramIntegratorTypeSupplier());
		processTypeSuppliers.add(new ClassifierTypeSupplier());
		processTypeSuppliers.add(new CombinedIntegratorTypeSupplier());
		processTypeSuppliers.add(new ChromatogramFilterTypeSupplier());
		processTypeSuppliers.add(new PeakFilterTypeSupplier());
		processTypeSuppliers.add(new PeakDetectorTypeSupplier());
		processTypeSuppliers.add(new PeakIdentifierTypeSupplier());
		processTypeSuppliers.add(new PeakIntegratorTypeSupplier());
		processTypeSuppliers.add(new PeakQuantitationTypeSupplier());
	}

	/**
	 * Returns an array of the processor names.
	 * 
	 * @param processCategory
	 * @param processorIds
	 * @return String[]
	 */
	public String[] getProcessorNames(String processCategory, String[] processorIds) {

		int size = processorIds.length;
		String[] processorNames = new String[size];
		for(int index = 0; index < size; index++) {
			processorNames[index] = getProcessorName(processCategory, processorIds[index]);
		}
		return processorNames;
	}

	/**
	 * Returns the processor name.
	 * 
	 * @param entry
	 * @return
	 */
	public String getProcessorName(IChromatogramProcessEntry entry) {

		return getProcessorName(entry.getProcessCategory(), entry.getProcessorId());
	}

	/**
	 * Returns the appropriate processor name.
	 * 
	 * @param processType
	 * @param processorId
	 * @return String
	 */
	public String getProcessorName(String processCategory, String processorId) {

		String processorName = IProcessTypeSupplier.NOT_AVAILABLE;
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			/*
			 * Check each registered process supplier.
			 */
			if(processTypeSupplier.getCategory().equals(processCategory)) {
				try {
					processorName = processTypeSupplier.getProcessorName(processorId);
					return processorName;
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		return processorName;
	}

	/**
	 * Returns the processor categories.
	 * 
	 * @return String[]
	 */
	public String[] getProcessorCategories() {

		String[] processorNames = new String[processTypeSuppliers.size()];
		int index = 0;
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			processorNames[index] = processTypeSupplier.getCategory();
			index++;
		}
		return processorNames;
	}

	/**
	 * Returns the available plugin names.
	 * 
	 * @param processCategory
	 * @return
	 */
	public String[] getPluginIds(String processCategory) {

		String[] pluginIds = {IProcessTypeSupplier.NOT_AVAILABLE};
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			/*
			 * Check each registered process supplier.
			 */
			if(processTypeSupplier.getCategory().equals(processCategory)) {
				try {
					pluginIds = processTypeSupplier.getPluginIds().toArray(new String[]{});
					return pluginIds;
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		return pluginIds;
	}

	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, IChromatogramProcessEntry processEntry, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			/*
			 * Check each registered process supplier.
			 */
			if(processTypeSupplier.getCategory().equals(processEntry.getProcessCategory())) {
				return processTypeSupplier.applyProcessor(chromatogramSelection, processEntry.getProcessorId(), monitor);
			}
		}
		processingInfo.addErrorMessage("Process Type Support", "There was now supplier to process the chromatogram selection.");
		return processingInfo;
	}
}
