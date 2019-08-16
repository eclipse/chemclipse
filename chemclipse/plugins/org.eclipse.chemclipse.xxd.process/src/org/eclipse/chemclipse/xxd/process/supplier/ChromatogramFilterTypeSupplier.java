/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add datatypes to supplier, merge with MSD supplier, add prefix to prevent id-clash
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Filter";
	private static final String CHROMATOGRAMFILTER_ALL_PREFIX = "ChromatogramFilter.";
	private static final String CHROMATOGRAMFILTER_MSD_PREFIX = "ChromatogramFilterMSD.";
	// these holds the "old" ids used for the processors to ensure backward compatibility
	private Set<String> backCompatAll = new HashSet<>();
	private Set<String> backCompatMSD = new HashSet<>();

	public ChromatogramFilterTypeSupplier() {
		super(CATEGORY);
		try {
			IChromatogramFilterSupport support = ChromatogramFilter.getChromatogramFilterSupport();
			for(String processorId : support.getAvailableFilterIds()) {
				IChromatogramFilterSupplier supplier = support.getFilterSupplier(processorId);
				backCompatAll.add(processorId);
				ProcessorSupplier processorSupplier = new ProcessorSupplier(CHROMATOGRAMFILTER_ALL_PREFIX + processorId, ALL_DATA_TYPES);
				processorSupplier.setName(supplier.getFilterName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			// don't need to add something here
		}
		try {
			IChromatogramFilterSupportMSD support = ChromatogramFilterMSD.getChromatogramFilterSupport();
			for(String processorId : support.getAvailableFilterIds()) {
				IChromatogramFilterSupplierMSD supplier = support.getFilterSupplier(processorId);
				backCompatMSD.add(processorId);
				ProcessorSupplier processorSupplier = new ProcessorSupplier(CHROMATOGRAMFILTER_MSD_PREFIX + processorId, MSD_DATA_TYPES);
				processorSupplier.setName(supplier.getFilterName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			// don't need to add something here
		}
	}

	@Override
	protected String getBackCompatId(String id) {

		if(backCompatAll.contains(id)) {
			return CHROMATOGRAMFILTER_ALL_PREFIX + id;
		} else if(backCompatMSD.contains(id)) {
			return CHROMATOGRAMFILTER_MSD_PREFIX + id;
		}
		return super.getBackCompatId(id);
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		boolean startsWithAllPrefix = processorId.startsWith(CHROMATOGRAMFILTER_ALL_PREFIX);
		boolean startsWithMSDPrefix = processorId.startsWith(CHROMATOGRAMFILTER_MSD_PREFIX);
		if(startsWithAllPrefix || backCompatAll.contains(processorId)) {
			if(startsWithAllPrefix) {
				processorId = processorId.substring(CHROMATOGRAMFILTER_ALL_PREFIX.length());
			}
			if(processSettings != null && processSettings instanceof IChromatogramFilterSettings) {
				return getProcessingResult(ChromatogramFilter.applyFilter(chromatogramSelection, (IChromatogramFilterSettings)processSettings, processorId, monitor), chromatogramSelection);
			} else {
				return getProcessingResult(ChromatogramFilter.applyFilter(chromatogramSelection, processorId, monitor), chromatogramSelection);
			}
		} else if(startsWithMSDPrefix || backCompatMSD.contains(processorId)) {
			if(startsWithMSDPrefix) {
				processorId = processorId.substring(CHROMATOGRAMFILTER_MSD_PREFIX.length());
			}
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				if(processSettings instanceof IChromatogramFilterSettings) {
					getProcessingResult(ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, (IChromatogramFilterSettings)processSettings, processorId, monitor), chromatogramSelection);
				} else {
					getProcessingResult(ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, processorId, monitor), chromatogramSelection);
				}
			} else {
				return getProcessingInfoError(processorId);
			}
		}
		return null;
	}
}
