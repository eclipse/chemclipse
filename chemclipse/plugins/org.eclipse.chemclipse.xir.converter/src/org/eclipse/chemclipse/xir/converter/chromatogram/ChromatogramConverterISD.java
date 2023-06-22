/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xir.converter.Activator;
import org.eclipse.chemclipse.xir.converter.service.IConverterServiceISD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramPeakISD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterISD implements IChromatogramConverter<IChromatogramPeakISD, IChromatogramISD> {

	private static ChromatogramConverterISD instance = null;

	public static IChromatogramConverter<IChromatogramPeakISD, IChromatogramISD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterISD();
		}
		//
		return instance;
	}

	@Override
	public IChromatogramConverterSupport getChromatogramConverterSupport() {

		ChromatogramConverterSupport chromatogramConverterSupport = new ChromatogramConverterSupport(DataCategory.ISD);
		Object[] services = Activator.getDefault().getConverterServices();
		if(services == null) {
			return chromatogramConverterSupport;
		}
		//
		for(Object service : services) {
			if(service instanceof IConverterServiceISD converterServiceISD) {
				ChromatogramSupplier supplier = new ChromatogramSupplier();
				supplier.setFileExtension(converterServiceISD.getFileExtension());
				supplier.setFileName(converterServiceISD.getFileName());
				supplier.setDirectoryExtension(converterServiceISD.getDirectoryExtension());
				/*
				 * Check if these values contain not allowed characters. If yes than
				 * do not add the supplier to the supported converter list.
				 */
				if(Converter.isValid(supplier.getFileExtension()) && Converter.isValid(supplier.getFileName()) && Converter.isValid(supplier.getDirectoryExtension())) {
					supplier.setId(converterServiceISD.getId());
					supplier.setDescription(converterServiceISD.getDescription());
					supplier.setFilterName(converterServiceISD.getFilterName());
					supplier.setImportable(converterServiceISD.isImportable());
					supplier.setExportable(converterServiceISD.isExportable());
					supplier.setMagicNumberMatcher(converterServiceISD.getMagicNumberMatcher());
					chromatogramConverterSupport.add(supplier);
				}
			}
		}
		//
		return chromatogramConverterSupport;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, String converterId, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramISD> convert(File file, IProgressMonitor monitor) {

		Object[] services = Activator.getDefault().getConverterServices();
		for(Object service : services) {
			if(service instanceof IConverterServiceISD converterServiceISD) {
				if(file.getName().endsWith(converterServiceISD.getFileExtension())) {
					IImportConverterISD importConverterISD = converterServiceISD.getImportConverter();
					return importConverterISD.convert(file, monitor);
				}
			}
		}
		//
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramISD> convert(File file, String converterId, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramISD> getChromatogram(File file, boolean overview, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo<IChromatogramISD> processingInfo, IProgressMonitor monitor) {

	}

	@Override
	public IProcessingInfo<File> convert(File file, IChromatogramISD chromatogram, String converterId, IProgressMonitor monitor) {

		return null;
	}
}