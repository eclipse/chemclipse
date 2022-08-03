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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class BaselineDetectorProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Baseline Detector";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> supplierList = new ArrayList<>();
		try {
			IBaselineDetectorSupport support = BaselineDetector.getBaselineDetectorSupport();
			for(String processorId : support.getAvailableDetectorIds()) {
				IBaselineDetectorSupplier supplier = support.getBaselineDetectorSupplier(processorId);
				supplierList.add(new BaselineDetectorProcessorSupplier(supplier, this));
			}
		} catch(NoBaselineDetectorAvailableException e) {
			Collections.emptyList();
		}
		return supplierList;
	}

	private static final class BaselineDetectorProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IBaselineDetectorSettings> {

		@SuppressWarnings("unchecked")
		public BaselineDetectorProcessorSupplier(IBaselineDetectorSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getDetectorName(), supplier.getDescription(), (Class<IBaselineDetectorSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD, DataType.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IBaselineDetectorSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings == null) {
				messageConsumer.addMessages(BaselineDetector.setBaseline(chromatogramSelection, getId(), monitor));
			} else {
				messageConsumer.addMessages(BaselineDetector.setBaseline(chromatogramSelection, processSettings, getId(), monitor));
			}
			return chromatogramSelection;
		}
	}
}
