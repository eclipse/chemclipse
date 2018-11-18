/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class ChromatogramTypeSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramTypeSupport.class);
	private static final String DESCRIPTION = "Chromatogram Type Support";
	//
	private List<ISupplierEditorSupport> supplierEditorSupportList = new ArrayList<>();

	public ChromatogramTypeSupport() {
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.MSD).getInstanceEditorSupport());
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.CSD).getInstanceEditorSupport());
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.WSD).getInstanceEditorSupport());
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo getChromatogramSelection(String pathChromatogram, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		File file = new File(pathChromatogram);
		DataType dataType = detectDataType(file);
		if(dataType != null) {
			IChromatogramSelection chromatogramSelection = null;
			boolean fireUpdate = false;
			//
			switch(dataType) {
				case MSD_NOMINAL:
				case MSD_TANDEM:
				case MSD_HIGHRES:
				case MSD:
					IProcessingInfo processingInfoMSD = ChromatogramConverterMSD.convert(file, monitor);
					IChromatogramMSD chromatogramMSD = processingInfoMSD.getProcessingResult(IChromatogramMSD.class);
					chromatogramSelection = new ChromatogramSelectionMSD(chromatogramMSD, fireUpdate);
					break;
				case CSD:
					IProcessingInfo processingInfoCSD = ChromatogramConverterCSD.convert(file, monitor);
					IChromatogramCSD chromatogramCSD = processingInfoCSD.getProcessingResult(IChromatogramCSD.class);
					chromatogramSelection = new ChromatogramSelectionCSD(chromatogramCSD, fireUpdate);
					break;
				case WSD:
					IProcessingInfo processingInfoWSD = ChromatogramConverterWSD.convert(file, monitor);
					IChromatogramWSD chromatogramWSD = processingInfoWSD.getProcessingResult(IChromatogramWSD.class);
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

	public void openFiles(List<File> files) throws Exception {

		Display display = DisplayUtils.getDisplay();
		//
		if(display != null) {
			for(File file : files) {
				if(file.exists()) {
					exitloop:
					for(ISupplierEditorSupport supplierEditorSupport : supplierEditorSupportList) {
						if(isSupplierFile(supplierEditorSupport, file)) {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {

									supplierEditorSupport.openEditor(file);
								}
							});
							break exitloop;
						}
					}
				} else {
					throw new Exception();
				}
			}
		}
	}

	private DataType detectDataType(File file) {

		String type = "";
		if(file.exists()) {
			exitloop:
			for(ISupplierEditorSupport supplierEditorSupport : supplierEditorSupportList) {
				if(isSupplierFile(supplierEditorSupport, file)) {
					type = supplierEditorSupport.getType();
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

	private boolean isSupplierFile(ISupplierEditorSupport supplierEditorSupport, File file) {

		if(file.isDirectory()) {
			if(supplierEditorSupport.isSupplierFileDirectory(file)) {
				return true;
			}
		} else {
			if(supplierEditorSupport.isSupplierFile(file)) {
				return true;
			}
		}
		return false;
	}
}
