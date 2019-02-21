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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.core;

import java.text.MessageFormat;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterList;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.filter.IScanFilter;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.util.IonSettingUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IScanFilter.class, Filter.class})
public class IonRemoverScanFilter implements IScanFilter<MassSpectrumFilterSettings> {

	@Override
	public String getFilterName() {

		return "Ion Remover";
	}

	@Override
	public String getFilterDescription() {

		return "Removes slected ions from the massspectrum of filtered scans";
	}

	@Override
	public MassSpectrumFilterSettings createNewConfiguration() {

		return new MassSpectrumFilterSettings();
	}

	@Override
	public IProcessingResult<Boolean> filterIScans(FilterList<IScan> filterItems, MassSpectrumFilterSettings configuration, IProgressMonitor monitor) throws IllegalArgumentException {

		if(configuration == null) {
			configuration = createNewConfiguration();
		}
		IonSettingUtil settingIon = new IonSettingUtil();
		IMarkedIons markedIons = new MarkedIons(settingIon.extractIons(settingIon.deserialize(configuration.getIonsToRemove())));
		SubMonitor subMonitor = SubMonitor.convert(monitor, filterItems.size());
		DefaultProcessingResult<Boolean> result = new DefaultProcessingResult<>();
		int modified = 0;
		for(IScan scan : filterItems) {
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				// TODO it might be good to have a config option to store the result in the "optimizedMassspectrum" instead of modify the original mass spectrum
				scanMSD.removeIons(markedIons);
				// sadly we don't really know if ANY ion is removed, but we mark the result as if we have at least removed one ion
				// The caller might recalculate data if needed
				scanMSD.setDirty(true);
				subMonitor.worked(1);
				modified++;
			} else {
				throw new IllegalArgumentException("Input contains invalid scan, use IScanFilter#acceptsIScan(IScan) to check for compatible types");
			}
		}
		result.setProcessingResult(modified > 0 ? Boolean.TRUE : Boolean.FALSE);
		result.addMessage(new ProcessingMessage(MessageType.INFO, getFilterName(), MessageFormat.format("{0} scan(s) where processed", modified)));
		return result;
	}

	@Override
	public boolean acceptsIScan(IScan item) {

		return item instanceof IScanMSD;
	}
}
