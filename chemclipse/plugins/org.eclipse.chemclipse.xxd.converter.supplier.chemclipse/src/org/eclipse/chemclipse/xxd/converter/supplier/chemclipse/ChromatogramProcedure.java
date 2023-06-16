/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - update version
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramExportConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.procedures.Procedure;
import org.eclipse.chemclipse.processing.supplier.IProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {Procedure.class})
public class ChromatogramProcedure implements Procedure<ChromatogramProcedureSettings> {

	private static final String NAME = "Copy Chromatogram Selection";
	//
	private final IChromatogramImportConverter<IChromatogramMSD> MSD_IMPORT = new org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
	private final IChromatogramImportConverter<IChromatogramCSD> CSD_IMPORT = new org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
	private final IChromatogramImportConverter<IChromatogramWSD> WSD_IMPORT = new org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter.ChromatogramImportConverter();
	//
	private final IChromatogramExportConverter MSD_EXPORT = new org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter.ChromatogramExportConverter();
	private final IChromatogramExportConverter CSD_EXPORT = new org.eclipse.chemclipse.csd.converter.supplier.chemclipse.converter.ChromatogramExportConverter();
	private final IChromatogramExportConverter WSD_EXPORT = new org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.converter.ChromatogramExportConverter();

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public String getDescription() {

		return "Creates a deep copy of the current chromatogram selection for doing some sub-processing";
	}

	@Override
	public Class<ChromatogramProcedureSettings> getConfigClass() {

		return ChromatogramProcedureSettings.class;
	}

	@Override
	public <ResultType> IProcessExecutionConsumer<ResultType> createConsumer(ChromatogramProcedureSettings settings, IProcessExecutionConsumer<ResultType> currentConsumer, ProcessExecutionContext context) {

		ResultType result = currentConsumer.getResult();
		if(result instanceof IChromatogramSelection<?, ?> originalSelection) {
			IChromatogram<?> chromatogram = originalSelection.getChromatogram();
			IChromatogramSelection<?, ?> copiedSelection;
			//
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = copyChromatogram(chromatogram, MSD_EXPORT, MSD_IMPORT, context);
				if(chromatogramMSD == null) {
					return null;
				}
				copiedSelection = new ChromatogramSelectionMSD(chromatogramMSD);
			} else if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = copyChromatogram(chromatogram, CSD_EXPORT, CSD_IMPORT, context);
				if(chromatogramCSD == null) {
					return null;
				}
				copiedSelection = new ChromatogramSelectionCSD(chromatogramCSD);
			} else if(chromatogram instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogramWSD = copyChromatogram(chromatogram, WSD_EXPORT, WSD_IMPORT, context);
				if(chromatogramWSD == null) {
					return null;
				}
				copiedSelection = new ChromatogramSelectionWSD(chromatogramWSD);
			} else {
				context.addErrorMessage(NAME, "Unsupported chromatogram type: " + chromatogram.getClass().getSimpleName());
				return null;
			}
			//
			IChromatogram<?> copiedChromatogram = copiedSelection.getChromatogram();
			if(settings.isAddAsReferenceChromatogram()) {
				chromatogram.addReferencedChromatogram(copiedChromatogram);
			}
			//
			if(settings.isCopyChromatogramSelectionRange()) {
				copiedSelection.setRangeRetentionTime(originalSelection.getStartRetentionTime(), originalSelection.getStopRetentionTime());
			}
			//
			copiedChromatogram.setFile(null);
			copiedChromatogram.setDataName(settings.getName().replace(IProcessSettings.VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName()));
			//
			return currentConsumer.withResult(copiedSelection);
		}
		return null;
	}

	private static <T extends IChromatogram<?>> T copyChromatogram(IChromatogram<?> chromatogram, IChromatogramExportConverter exporter, IChromatogramImportConverter<T> importer, ProcessExecutionContext context) {

		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), 100);
		//
		try {
			File tempFile = File.createTempFile(chromatogram.getName(), ".ocb");
			tempFile.deleteOnExit();
			//
			IProcessingInfo<?> convert = exporter.convert(tempFile, chromatogram, subMonitor.split(50));
			if(convert.hasErrorMessages()) {
				context.addMessages(convert);
				return null;
			}
			//
			IProcessingInfo<T> info = importer.convert(tempFile, subMonitor.split(50));
			if(info.hasErrorMessages()) {
				context.addMessages(info);
				return null;
			}
			return info.getProcessingResult();
		} catch(IOException e) {
			context.addErrorMessage(NAME, "Cloning has failed", e);
		}
		return null;
	}

	@Override
	public DataCategory[] getDataCategories() {

		return new DataCategory[]{DataCategory.WSD, DataCategory.CSD, DataCategory.MSD};
	}
}
