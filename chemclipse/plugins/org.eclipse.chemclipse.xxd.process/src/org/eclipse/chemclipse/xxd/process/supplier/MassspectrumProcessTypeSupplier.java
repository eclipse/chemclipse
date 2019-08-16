/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassspectrumProcessTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.WSD};
	public static final String CATEGORY_PEAK = "Peak Massspectrum Filter";
	public static final String CATEGORY_SCAN = "Scan Massspectrum Filter";
	private String prefix;
	private static final IMassSpectrumFilterSupport FILTER_SUPPORT = MassSpectrumFilter.getMassSpectrumFilterSupport();

	private MassspectrumProcessTypeSupplier(String category, String prefix) {
		super(category);
		this.prefix = prefix;
		try {
			List<String> ids = FILTER_SUPPORT.getAvailableFilterIds();
			for(String id : ids) {
				IMassSpectrumFilterSupplier supplier = FILTER_SUPPORT.getFilterSupplier(id);
				ProcessorSupplier processorSupplier = new ProcessorSupplier(prefix + id, DATA_TYPES);
				processorSupplier.setName(supplier.getFilterName());
				processorSupplier.setDescription(supplier.getDescription());
				// TODO processorSupplier.setSettingsClass();
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoMassSpectrumFilterSupplierAvailableException e) {
			// don't mind
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processorId.startsWith(prefix)) {
			processorId = processorId.substring(prefix.length());
		}
		IMassSpectrumFilterSettings massSpectrumFilterSettings;
		if(processSettings instanceof IMassSpectrumFilterSettings) {
			massSpectrumFilterSettings = (IMassSpectrumFilterSettings)processSettings;
		} else {
			massSpectrumFilterSettings = null;
		}
		List<IScanMSD> massspectras = new ArrayList<>();
		if(CATEGORY_PEAK.equals(getCategory())) {
			List<?> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
			for(Object object : peaks) {
				if(object instanceof IPeakMSD) {
					massspectras.add(((IPeakMSD)object).getExtractedMassSpectrum());
				}
			}
		} else {
			List<?> scans = chromatogramSelection.getChromatogram().getScans();
			for(Object object : scans) {
				if(object instanceof IScanMSD) {
					massspectras.add((IScanMSD)object);
				}
			}
		}
		return getProcessingResult(MassSpectrumFilter.applyFilter(massspectras, massSpectrumFilterSettings, processorId, monitor), chromatogramSelection);
	}

	public static final MassspectrumProcessTypeSupplier createPeakFilterSupplier() {

		return new MassspectrumProcessTypeSupplier(CATEGORY_PEAK, "mzfilter.msd.peak");
	}

	public static final MassspectrumProcessTypeSupplier createScanFilterSupplier() {

		return new MassspectrumProcessTypeSupplier(CATEGORY_SCAN, "mzfilter.msd.scan");
	}
}
