/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.tsd.converter.Activator;
import org.eclipse.chemclipse.tsd.converter.core.IImportConverterTSD;
import org.eclipse.chemclipse.tsd.converter.service.IConverterServiceTSD;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramPeakTSD;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramTSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterTSD implements IChromatogramConverter<IChromatogramPeakTSD, IChromatogramTSD> {

	private static ChromatogramConverterTSD instance = null;

	public static IChromatogramConverter<IChromatogramPeakTSD, IChromatogramTSD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterTSD();
		}
		//
		return instance;
	}

	public IChromatogramConverterSupport getChromatogramConverterSupport() {

		ChromatogramConverterSupport chromatogramConverterSupport = new ChromatogramConverterSupport(DataCategory.TSD);
		Object[] services = Activator.getDefault().getConverterServices();
		for(Object service : services) {
			if(service instanceof IConverterServiceTSD) {
				IConverterServiceTSD converterServiceTSD = (IConverterServiceTSD)service;
				ChromatogramSupplier supplier = new ChromatogramSupplier();
				supplier.setFileExtension(converterServiceTSD.getFileExtension());
				supplier.setFileName(converterServiceTSD.getFileName());
				supplier.setDirectoryExtension(converterServiceTSD.getDirectoryExtension());
				/*
				 * Check if these values contain not allowed characters. If yes than
				 * do not add the supplier to the supported converter list.
				 */
				if(Converter.isValid(supplier.getFileExtension()) && Converter.isValid(supplier.getFileName()) && Converter.isValid(supplier.getDirectoryExtension())) {
					supplier.setId(converterServiceTSD.getId());
					supplier.setDescription(converterServiceTSD.getDescription());
					supplier.setFilterName(converterServiceTSD.getFilterName());
					supplier.setImportable(converterServiceTSD.isImportable());
					supplier.setExportable(converterServiceTSD.isExportable());
					supplier.setMagicNumberMatcher(converterServiceTSD.getMagicNumberMatcher());
					chromatogramConverterSupport.add(supplier);
				}
			}
		}
		//
		return chromatogramConverterSupport;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		System.out.println("A");
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, String converterId, IProgressMonitor monitor) {

		System.out.println("B");
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramTSD> convert(File file, IProgressMonitor monitor) {

		Object[] services = Activator.getDefault().getConverterServices();
		for(Object service : services) {
			if(service instanceof IConverterServiceTSD) {
				IConverterServiceTSD converterServiceTSD = (IConverterServiceTSD)service;
				if(file.getName().endsWith(converterServiceTSD.getFileExtension())) {
					IImportConverterTSD importConverterTSD = converterServiceTSD.getImportConverter();
					return importConverterTSD.convert(file, monitor);
				}
			}
		}
		//
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramTSD> convert(File file, String converterId, IProgressMonitor monitor) {

		System.out.println("D");
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramTSD> getChromatogram(File file, boolean overview, IProgressMonitor monitor) {

		System.out.println("E");
		return null;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo<IChromatogramTSD> processingInfo, IProgressMonitor monitor) {

		System.out.println("F");
	}

	@Override
	public IProcessingInfo<File> convert(File file, IChromatogramTSD chromatogram, String converterId, IProgressMonitor monitor) {

		System.out.println("G");
		return null;
	}
}