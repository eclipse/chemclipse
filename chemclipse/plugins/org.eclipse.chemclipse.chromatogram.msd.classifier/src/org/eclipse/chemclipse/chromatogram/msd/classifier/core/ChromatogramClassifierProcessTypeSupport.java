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
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.NoChromatogramClassifierSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramClassifierProcessTypeSupport implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Classifier";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IChromatogramClassifierSupport support = ChromatogramClassifier.getChromatogramClassifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableClassifierIds()) {
				IChromatogramClassifierSupplier supplier = support.getClassifierSupplier(processorId);
				list.add(new ChromatogramClassifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramClassifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramClassifierSettings> {

		@SuppressWarnings("unchecked")
		public ChromatogramClassifierProcessorSupplier(IChromatogramClassifierSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getClassifierName(), supplier.getDescription(), (Class<IChromatogramClassifierSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD, DataType.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings instanceof IChromatogramClassifierSettings) {
				messageConsumer.addMessages(ChromatogramClassifier.applyClassifier(chromatogramSelection, processSettings, getId(), monitor));
			} else {
				messageConsumer.addMessages(ChromatogramClassifier.applyClassifier(chromatogramSelection, getId(), monitor));
			}
			return chromatogramSelection;
		}
	}
}
