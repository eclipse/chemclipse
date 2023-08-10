/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 * Philip Wenig - refactor menu categories
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterWSDProcessSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.CHROMATOGRAM_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IChromatogramFilterSupportWSD support = ChromatogramFilterWSD.getChromatogramFilterSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableFilterIds()) {
				list.add(new ChromatogramFilterWSDProcessorSupplier(support.getFilterSupplier(processorId), this));
			}
			return list;
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramFilterWSDProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramFilterSettings> {

		private IChromatogramFilterSupplier supplier;

		@SuppressWarnings("unchecked")
		public ChromatogramFilterWSDProcessorSupplier(IChromatogramFilterSupplier supplier, IProcessTypeSupplier parent) {

			super("ChromatogramFilterWSD." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), (Class<IChromatogramFilterSettings>)supplier.getSettingsClass(), parent, DataType.WSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
				if(processSettings instanceof IChromatogramFilterSettings) {
					messageConsumer.addMessages(ChromatogramFilterWSD.applyFilter(chromatogramSelectionWSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(ChromatogramFilterWSD.applyFilter(chromatogramSelectionWSD, supplier.getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only WSD chromatogram supported, skipp processing");
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
