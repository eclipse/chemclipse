/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.peak.detector.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupport;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakDetectorCSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Detector";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakDetectorSupport support = PeakDetectorCSD.getPeakDetectorSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailablePeakDetectorIds()) {
				IPeakDetectorSupplier supplier = support.getPeakDetectorSupplier(processorId);
				list.add(new PeakDetectorProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoPeakDetectorAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakDetectorProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakDetectorSettingsCSD> {

		private IPeakDetectorSupplier supplier;

		@SuppressWarnings("unchecked")
		public PeakDetectorProcessorSupplier(IPeakDetectorSupplier supplier, IProcessTypeSupplier parent) {
			super("PeakDetectorCSD." + supplier.getId(), supplier.getPeakDetectorName(), supplier.getDescription(), (Class<IPeakDetectorSettingsCSD>)supplier.getSettingsClass(), parent, DataType.CSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakDetectorSettingsCSD processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
				if(processSettings instanceof IPeakDetectorSettingsCSD) {
					messageConsumer.addMessages(PeakDetectorCSD.detect(chromatogramSelectionCSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(PeakDetectorCSD.detect(chromatogramSelectionCSD, supplier.getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getDescription(), "Only CSD Chromatogram supported, skipp processing");
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
