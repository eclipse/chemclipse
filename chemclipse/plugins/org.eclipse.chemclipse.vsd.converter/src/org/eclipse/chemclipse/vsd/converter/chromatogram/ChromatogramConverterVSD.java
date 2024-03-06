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
package org.eclipse.chemclipse.vsd.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.vsd.converter.Activator;
import org.eclipse.chemclipse.vsd.converter.service.IConverterServiceVSD;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramPeakVSD;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterVSD implements IChromatogramConverter<IChromatogramPeakVSD, IChromatogramVSD> {

	private static ChromatogramConverterVSD instance = null;

	public static IChromatogramConverter<IChromatogramPeakVSD, IChromatogramVSD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterVSD();
		}
		//
		return instance;
	}

	@Override
	public IChromatogramConverterSupport getChromatogramConverterSupport() {

		ChromatogramConverterSupport chromatogramConverterSupport = new ChromatogramConverterSupport(DataCategory.VSD);
		Object[] services = Activator.getDefault().getConverterServices();
		if(services == null) {
			return chromatogramConverterSupport;
		}
		//
		for(Object service : services) {
			if(service instanceof IConverterServiceVSD converterServiceVSD) {
				ChromatogramSupplier supplier = new ChromatogramSupplier();
				supplier.setFileExtension(converterServiceVSD.getFileExtension());
				supplier.setFileName(converterServiceVSD.getFileName());
				supplier.setDirectoryExtension(converterServiceVSD.getDirectoryExtension());
				/*
				 * Check if these values contain not allowed characters. If yes than
				 * do not add the supplier to the supported converter list.
				 */
				if(Converter.isValid(supplier.getFileExtension()) && Converter.isValid(supplier.getFileName()) && Converter.isValid(supplier.getDirectoryExtension())) {
					supplier.setId(converterServiceVSD.getId());
					supplier.setDescription(converterServiceVSD.getDescription());
					supplier.setFilterName(converterServiceVSD.getFilterName());
					supplier.setImportable(converterServiceVSD.isImportable());
					supplier.setExportable(converterServiceVSD.isExportable());
					supplier.setMagicNumberMatcher(converterServiceVSD.getMagicNumberMatcher());
					supplier.setFileContentMatcher(converterServiceVSD.getFileContentMatcher());
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
	public IProcessingInfo<IChromatogramVSD> convert(File file, IProgressMonitor monitor) {

		Object[] services = Activator.getDefault().getConverterServices();
		for(Object service : services) {
			if(service instanceof IConverterServiceVSD converterServiceVSD) {
				if(file.getName().endsWith(converterServiceVSD.getFileExtension())) {
					IImportConverterVSD importConverterVSD = converterServiceVSD.getImportConverter();
					return importConverterVSD.convert(file, monitor);
				}
			}
		}
		//
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramVSD> convert(File file, String converterId, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramVSD> getChromatogram(File file, boolean overview, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo<IChromatogramVSD> processingInfo, IProgressMonitor monitor) {

	}

	@Override
	public IProcessingInfo<File> convert(File file, IChromatogramVSD chromatogram, String converterId, IProgressMonitor monitor) {

		return null;
	}
}