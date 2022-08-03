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
package org.eclipse.chemclipse.chromatogram.filter.core.chromatogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterProcessSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Filter";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IChromatogramFilterSupport support = ChromatogramFilter.getChromatogramFilterSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableFilterIds()) {
				IChromatogramFilterSupplier supplier = support.getFilterSupplier(processorId);
				list.add(new ChromatogramFilterProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramFilterProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramFilterSettings> {

		private IChromatogramFilterSupplier supplier;

		@SuppressWarnings("unchecked")
		public ChromatogramFilterProcessorSupplier(IChromatogramFilterSupplier supplier, IProcessTypeSupplier parent) {
			super("ChromatogramFilter." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), (Class<IChromatogramFilterSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD, DataType.WSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings instanceof IChromatogramFilterSettings) {
				messageConsumer.addMessages(ChromatogramFilter.applyFilter(chromatogramSelection, processSettings, supplier.getId(), monitor));
			} else {
				messageConsumer.addMessages(ChromatogramFilter.applyFilter(chromatogramSelection, supplier.getId(), monitor));
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
