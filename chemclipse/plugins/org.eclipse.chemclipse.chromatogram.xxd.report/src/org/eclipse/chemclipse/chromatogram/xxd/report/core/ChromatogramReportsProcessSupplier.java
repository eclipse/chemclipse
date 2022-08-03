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
package org.eclipse.chemclipse.chromatogram.xxd.report.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.DefaultChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramReportsProcessSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Reports";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			List<IProcessSupplier<?>> list = new ArrayList<>();
			IChromatogramReportSupport support = ChromatogramReports.getChromatogramReportSupplierSupport();
			for(String processorId : support.getAvailableProcessorIds()) {
				IChromatogramReportSupplier supplier = support.getReportSupplier(processorId);
				list.add(new ChromatogramReportProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoReportSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramReportProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramReportSettings> {

		private IChromatogramReportSupplier supplier;

		@SuppressWarnings("unchecked")
		public ChromatogramReportProcessorSupplier(IChromatogramReportSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getReportName(), supplier.getDescription(), (Class<IChromatogramReportSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD, DataType.WSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramReportSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			IChromatogramReportSettings settings;
			if(processSettings instanceof IChromatogramReportSettings) {
				settings = processSettings;
			} else {
				settings = new DefaultChromatogramReportSettings();
			}
			File exportFolder = settings.getExportFolder();
			if(exportFolder == null) {
				messageConsumer.addErrorMessage(getName(), "No outputfolder specified and no default configured");
				return chromatogramSelection;
			}
			String extension = supplier.getFileExtension();
			if(exportFolder.exists() || exportFolder.mkdirs()) {
				IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
				File file = new File(exportFolder, settings.getFileNamePattern().replace(IChromatogramReportSettings.VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName()).replace(IChromatogramReportSettings.VARIABLE_EXTENSION, extension));
				IProcessingInfo<?> info = ChromatogramReports.generate(file, settings.isAppend(), chromatogram, settings, getId(), monitor);
				messageConsumer.addMessages(info);
				messageConsumer.addInfoMessage(getName(), "Report written to " + file.getAbsolutePath());
			} else {
				messageConsumer.addErrorMessage(getName(), "The specified outputfolder does not exits and can't be created");
			}
			return chromatogramSelection;
		}
	}
}
