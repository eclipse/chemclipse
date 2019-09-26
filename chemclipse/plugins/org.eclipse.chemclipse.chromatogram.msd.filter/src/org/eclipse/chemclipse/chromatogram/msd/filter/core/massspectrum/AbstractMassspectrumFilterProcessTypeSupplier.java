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
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractMassspectrumFilterProcessTypeSupplier implements IProcessTypeSupplier {

	private String category;
	private String prefix;
	private Function<IChromatogramSelection<?, ?>, List<IScanMSD>> extractionFunction;

	public AbstractMassspectrumFilterProcessTypeSupplier(String category, String prefix, Function<IChromatogramSelection<?, ?>, List<IScanMSD>> extractionFunction) {
		this.category = category;
		this.prefix = prefix;
		this.extractionFunction = extractionFunction;
	}

	@Override
	public String getCategory() {

		return category;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IMassSpectrumFilterSupport filterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
			List<IProcessSupplier<?>> list = new ArrayList<IProcessSupplier<?>>();
			for(String id : filterSupport.getAvailableFilterIds()) {
				list.add(new MassSpectrumFilterProcessorSupplier(filterSupport.getFilterSupplier(id), extractionFunction, this));
			}
			return list;
		} catch(NoMassSpectrumFilterSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class MassSpectrumFilterProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IMassSpectrumFilterSettings> {

		private IMassSpectrumFilterSupplier supplier;
		private Function<IChromatogramSelection<?, ?>, List<IScanMSD>> extractionFunction;

		@SuppressWarnings("unchecked")
		public MassSpectrumFilterProcessorSupplier(IMassSpectrumFilterSupplier supplier, Function<IChromatogramSelection<?, ?>, List<IScanMSD>> extractionFunction, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getFilterName(), supplier.getDescription(), (Class<IMassSpectrumFilterSettings>)supplier.getConfigClass(), parent, DataType.MSD);
			this.supplier = supplier;
			this.extractionFunction = extractionFunction;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IMassSpectrumFilterSettings processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

			List<IScanMSD> massspectras = extractionFunction.apply(chromatogramSelection);
			messageConsumer.addMessages(MassSpectrumFilter.applyFilter(massspectras, processSettings, supplier.getId(), monitor));
			return chromatogramSelection;
		}
	}
}
