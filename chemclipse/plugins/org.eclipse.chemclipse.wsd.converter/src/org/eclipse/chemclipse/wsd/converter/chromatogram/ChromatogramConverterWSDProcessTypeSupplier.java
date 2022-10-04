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
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.chromatogram;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramExportSettings;
import org.eclipse.chemclipse.converter.core.IConverterSupport;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramConverterWSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Export";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		List<ISupplier> suppliers = new ArrayList<>(ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport().getSupplier(IConverterSupport.EXPORT_SUPPLIER));
		for(ISupplier supplier : suppliers) {
			list.add(new ChromatogramConverterWSDProcessorSupplier(supplier, this));
		}
		return list;
	}

	private static final class ChromatogramConverterWSDProcessorSupplier extends ChromatogramSelectionProcessorSupplier<ChromatogramExportSettings> {

		private ISupplier supplier;

		public ChromatogramConverterWSDProcessorSupplier(ISupplier supplier, IProcessTypeSupplier parent) {

			super("wsd.export." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), ChromatogramExportSettings.class, parent, DataType.WSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ChromatogramExportSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings == null) {
				processSettings = new ChromatogramExportSettings();
			}
			File exportFolder = processSettings.getExportFolder();
			if(exportFolder == null) {
				messageConsumer.addErrorMessage(getName(), "No outputfolder specified and no default configured");
				return chromatogramSelection;
			}
			if(exportFolder.exists() || exportFolder.mkdirs()) {
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram instanceof IChromatogramWSD) {
					File file = processSettings.getExportFile(supplier.getFileExtension(), chromatogram);
					IProcessingInfo<File> info = ChromatogramConverterWSD.getInstance().convert(file, (IChromatogramWSD)chromatogram, supplier.getId(), monitor);
					messageConsumer.addMessages(info);
					if(info != null && info.getProcessingResult() != null) {
						File result = info.getProcessingResult();
						messageConsumer.addInfoMessage(getName(), "Exported data to " + result.getAbsolutePath());
					}
				} else {
					messageConsumer.addWarnMessage(getName(), "Only WSD Chromatograms supported, skipp processing");
				}
			} else {
				messageConsumer.addErrorMessage(getName(), "The specified outputfolder (" + exportFolder.getAbsolutePath() + ") does not exits and can't be created");
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
