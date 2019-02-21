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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.CutOffScanFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.CutOffScanFilterSettings.CutDirection;
import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterList;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.filter.IScanFilter;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IScanFilter.class, Filter.class})
public class CutOffScanFilter implements IScanFilter<CutOffScanFilterSettings> {

	@Override
	public String getFilterName() {

		return "Ion cut off";
	}

	@Override
	public String getFilterDescription() {

		return "Cut off the lowest/highest abundance ions";
	}

	@Override
	public CutOffScanFilterSettings createNewConfiguration() {

		return new CutOffScanFilterSettings();
	}

	@Override
	public IProcessingResult<Boolean> filterIScans(FilterList<IScan> filterItems, CutOffScanFilterSettings configuration, IProgressMonitor monitor) throws IllegalArgumentException {

		if(configuration == null) {
			configuration = createNewConfiguration();
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, filterItems.size());
		DefaultProcessingResult<Boolean> result = new DefaultProcessingResult<>();
		int modified = 0;
		for(IScan scan : filterItems) {
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				if(processScan(scanMSD, configuration)) {
					scanMSD.setDirty(true);
					modified++;
				}
				subMonitor.worked(1);
			} else {
				throw new IllegalArgumentException("Input contains invalid scan, use IScanFilter#acceptsIScan(IScan) to check for compatible types");
			}
		}
		result.setProcessingResult(modified > 0 ? Boolean.TRUE : Boolean.FALSE);
		result.addMessage(new ProcessingMessage(MessageType.INFO, getFilterName(), MessageFormat.format("{0} scan(s) where processed", modified)));
		return result;
	}

	private boolean processScan(IScanMSD massSpectrum, CutOffScanFilterSettings configuration) {

		boolean removed = false;
		List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
		Collections.sort(ions, new IonAbundanceComparator(configuration.getCutDirection() == CutDirection.HIGH ? SortOrder.DESC : SortOrder.ASC));
		int counter = 0;
		for(IIon ion : ions) {
			if(counter >= configuration.getCutNumber()) {
				massSpectrum.removeIon(ion);
				removed = true;
			}
			counter++;
		}
		return removed;
	}

	@Override
	public boolean acceptsIScan(IScan item) {

		return item instanceof IScanMSD;
	}
}
