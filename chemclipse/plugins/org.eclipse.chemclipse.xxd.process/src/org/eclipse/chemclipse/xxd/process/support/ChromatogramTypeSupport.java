/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramTypeSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramTypeSupport.class);
	private static final String DESCRIPTION = "Chromatogram Type Support";
	//
	private final List<ISupplierFileIdentifier> supplierEditorSupportList = new ArrayList<>();

	public ChromatogramTypeSupport(DataType[] dataTypes) {

		for(DataType dataType : dataTypes) {
			supplierEditorSupportList.add(new SupplierFileIdentifier(dataType));
		}
	}

	public IProcessingInfo<IChromatogramSelection<?, ?>> getChromatogramSelection(String pathChromatogram, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramSelection<?, ?>> processingInfo = new ProcessingInfo<>();
		File file = new File(pathChromatogram);
		DataType dataType = detectDataType(file);
		if(dataType != null) {
			IChromatogramSelection<? extends IChromatogramPeak, ?> chromatogramSelection = null;
			boolean fireUpdate = false;
			// TODO chemclipse/#97
			switch(dataType) {
				case MSD_NOMINAL:
				case MSD_TANDEM:
				case MSD_HIGHRES:
				case MSD:
					IProcessingInfo<IChromatogramMSD> processingInfoMSD = ChromatogramConverterMSD.getInstance().convert(file, monitor);
					IChromatogramMSD chromatogramMSD = processingInfoMSD.getProcessingResult();
					chromatogramSelection = new ChromatogramSelectionMSD(chromatogramMSD, fireUpdate);
					break;
				case CSD:
					IProcessingInfo<IChromatogramCSD> processingInfoCSD = ChromatogramConverterCSD.getInstance().convert(file, monitor);
					IChromatogramCSD chromatogramCSD = processingInfoCSD.getProcessingResult();
					chromatogramSelection = new ChromatogramSelectionCSD(chromatogramCSD, fireUpdate);
					break;
				case WSD:
					IProcessingInfo<IChromatogramWSD> processingInfoWSD = ChromatogramConverterWSD.getInstance().convert(file, monitor);
					IChromatogramWSD chromatogramWSD = processingInfoWSD.getProcessingResult();
					chromatogramSelection = new ChromatogramSelectionWSD(chromatogramWSD, fireUpdate);
					break;
				default:
					// No action
			}
			//
			if(chromatogramSelection != null) {
				processingInfo.setProcessingResult(chromatogramSelection);
			} else {
				String message = "Chromatogram Selection is null: " + file + " " + dataType;
				processingInfo.addErrorMessage(DESCRIPTION, message);
				logger.warn(message);
			}
		} else {
			String message = "Could not detect data type of file: " + file;
			processingInfo.addErrorMessage(DESCRIPTION, message);
			logger.warn(message);
		}
		//
		return processingInfo;
	}

	public DataType detectDataType(File file) {

		String type = "";
		if(file.exists()) {
			exitloop:
			for(ISupplierFileIdentifier supplierFileIdentifier : supplierEditorSupportList) {
				if(isSupplierFile(supplierFileIdentifier, file)) {
					type = supplierFileIdentifier.getType();
					break exitloop;
				}
			}
		}
		//
		DataType dataType;
		switch(type) {
			case ISupplierFileIdentifier.TYPE_MSD:
				dataType = DataType.MSD;
				break;
			case ISupplierFileIdentifier.TYPE_CSD:
				dataType = DataType.CSD;
				break;
			case ISupplierFileIdentifier.TYPE_WSD:
				dataType = DataType.WSD;
				break;
			default:
				dataType = null;
				break;
		}
		//
		return dataType;
	}

	public boolean isSupplierFile(ISupplierFileIdentifier supplierFileIdentifier, File file) {

		if(supplierFileIdentifier.isSupplierFile(file) && supplierFileIdentifier.isMatchMagicNumber(file)) {
			return true;
		}
		return false;
	}
}
