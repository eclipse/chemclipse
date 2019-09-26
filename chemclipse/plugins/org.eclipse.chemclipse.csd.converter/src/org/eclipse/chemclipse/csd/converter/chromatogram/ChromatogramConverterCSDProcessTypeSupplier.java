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
package org.eclipse.chemclipse.csd.converter.chromatogram;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramExportSettings;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramConverterCSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Export";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		for(ISupplier supplier : ChromatogramConverterCSD.getInstance().getChromatogramConverterSupport().getExportSupplier()) {
			list.add(new ChromatogramConverterCSDProcessorSupplier(supplier, this));
		}
		return list;
	}

	private static final class ChromatogramConverterCSDProcessorSupplier extends ChromatogramSelectionProcessorSupplier<ChromatogramExportSettings> {

		private ISupplier supplier;

		public ChromatogramConverterCSDProcessorSupplier(ISupplier supplier, IProcessTypeSupplier parent) {
			super("csd.export." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), ChromatogramExportSettings.class, parent, DataType.CSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ChromatogramExportSettings processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

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
				if(chromatogram instanceof IChromatogramCSD) {
					File file = processSettings.getExportFileName(supplier.getFileExtension(), chromatogram);
					IProcessingInfo<File> info = ChromatogramConverterCSD.getInstance().convert(file, (IChromatogramCSD)chromatogram, supplier.getId(), monitor);
					messageConsumer.addMessages(info);
					if(info != null && info.getProcessingResult() != null) {
						File result = info.getProcessingResult();
						messageConsumer.addInfoMessage(getName(), "Exported data to " + result.getAbsolutePath());
					}
				} else {
					messageConsumer.addWarnMessage(getName(), "Only CSD Chromatograms supported, skipp processing");
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
