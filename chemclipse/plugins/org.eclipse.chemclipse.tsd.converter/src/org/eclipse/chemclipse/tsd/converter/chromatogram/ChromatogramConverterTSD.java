/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.tsd.converter.Activator;
import org.eclipse.chemclipse.tsd.converter.chromatogram.model.ConverterAdapterServiceTSD;
import org.eclipse.chemclipse.tsd.converter.core.IImportConverterTSD;
import org.eclipse.chemclipse.tsd.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.tsd.converter.service.IConverterServiceTSD;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramPeakTSD;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramTSD;
import org.eclipse.chemclipse.tsd.model.core.TypeTSD;
import org.eclipse.chemclipse.vsd.converter.chromatogram.ChromatogramConverterVSD;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
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

	@Override
	public IChromatogramConverterSupport getChromatogramConverterSupport() {

		ChromatogramConverterSupport chromatogramConverterSupport = new ChromatogramConverterSupport(DataCategory.TSD);
		//
		List<ChromatogramSupplier> chromatogramSuppliers = getChromatogramSupplier(getConverterServicesTSD());
		for(ChromatogramSupplier chromatogramSupplier : chromatogramSuppliers) {
			chromatogramConverterSupport.add(chromatogramSupplier);
		}
		//
		return chromatogramConverterSupport;
	}

	private List<ChromatogramSupplier> getChromatogramSupplier(List<IConverterServiceTSD> converterServices) {

		List<ChromatogramSupplier> chromatogramSupplier = new ArrayList<>();
		//
		for(IConverterServiceTSD converterServiceTSD : converterServices) {
			/*
			 * Header
			 */
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
				supplier.setFileContentMatcher(converterServiceTSD.getFileContentMatcher());
				chromatogramSupplier.add(supplier);
			}
		}
		//
		return chromatogramSupplier;
	}

	private List<IConverterServiceTSD> getConverterServicesTSD() {

		List<IConverterServiceTSD> converterServices = new ArrayList<>();
		/*
		 * Registered Services
		 */
		Object[] services = Activator.getDefault().getConverterServices();
		if(services != null) {
			for(Object service : services) {
				if(service instanceof IConverterServiceTSD converterServiceTSD) {
					converterServices.add(converterServiceTSD);
				}
			}
		}
		/*
		 * Adapter Services MSD
		 */
		if(PreferenceSupplier.isUseAdapterMSD()) {
			converterServices.addAll(getConverterAdapterServicesMSD());
		}
		/*
		 * Adapter Services WSD
		 */
		if(PreferenceSupplier.isUseAdapterWSD()) {
			converterServices.addAll(getConverterAdapterServicesWSD());
		}
		/*
		 * Adapter Services ISD
		 */
		if(PreferenceSupplier.isUseAdapterISD()) {
			converterServices.addAll(getConverterAdapterServicesISD());
		}
		//
		return converterServices;
	}

	private List<IConverterServiceTSD> getConverterAdapterServicesMSD() {

		IChromatogramConverterSupport converterSupportMSD = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
		return getConverterAdapterServices(converterSupportMSD.getSupplier(), TypeTSD.GC_MS);
	}

	private List<IConverterServiceTSD> getConverterAdapterServicesWSD() {

		IChromatogramConverterSupport converterSupportWSD = ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport();
		return getConverterAdapterServices(converterSupportWSD.getSupplier(), TypeTSD.HPLC_DAD);
	}

	private List<IConverterServiceTSD> getConverterAdapterServicesISD() {

		IChromatogramConverterSupport converterSupportISD = ChromatogramConverterVSD.getInstance().getChromatogramConverterSupport();
		return getConverterAdapterServices(converterSupportISD.getSupplier(), TypeTSD.HPLC_RAMAN);
	}

	private List<IConverterServiceTSD> getConverterAdapterServices(List<ISupplier> suppliers, TypeTSD typeTSD) {

		List<IConverterServiceTSD> converterServices = new ArrayList<>();
		//
		for(ISupplier supplierx : suppliers) {
			if(supplierx.isImportable()) {
				converterServices.add(new ConverterAdapterServiceTSD(supplierx, typeTSD));
			}
		}
		//
		return converterServices;
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
	public IProcessingInfo<IChromatogramTSD> convert(File file, IProgressMonitor monitor) {

		for(IConverterServiceTSD converterServiceTSD : getConverterServicesTSD()) {
			String name = file.getName().toLowerCase();
			String extension = converterServiceTSD.getFileExtension().toLowerCase();
			if(name.endsWith(extension)) {
				IImportConverterTSD importConverterTSD = converterServiceTSD.getImportConverter();
				return importConverterTSD.convert(file, monitor);
			}
		}
		//
		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramTSD> convert(File file, String converterId, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public IProcessingInfo<IChromatogramTSD> getChromatogram(File file, boolean overview, IProgressMonitor monitor) {

		return null;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo<IChromatogramTSD> processingInfo, IProgressMonitor monitor) {

	}

	@Override
	public IProcessingInfo<File> convert(File file, IChromatogramTSD chromatogram, String converterId, IProgressMonitor monitor) {

		return null;
	}
}