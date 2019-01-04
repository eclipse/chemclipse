/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplierCSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplierWSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramReportTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ClassifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.CombinedIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplierCSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplierCSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakQuantitationTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessTypeSupport {

	private static final Logger logger = Logger.getLogger(ProcessTypeSupport.class);
	//
	private Map<String, IProcessTypeSupplier> processSupplierMap = new HashMap<>();

	public ProcessTypeSupport() {
		/*
		 * Add all available process supplier here.
		 * TODO: Test native settings composite via extension point resolution
		 */
		addProcessSupplier(new BaselineDetectorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramIdentifierTypeSupplier()); // OK - Improve settings
		addProcessSupplier(new ChromatogramIntegratorTypeSupplier()); // OK
		addProcessSupplier(new ClassifierTypeSupplier()); // OK - Improve settings
		addProcessSupplier(new CombinedIntegratorTypeSupplier()); // OK - Nested Settings
		addProcessSupplier(new ChromatogramFilterTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramFilterTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakFilterTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakDetectorTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakIdentifierTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakDetectorTypeSupplierCSD()); // OK
		addProcessSupplier(new PeakIdentifierTypeSupplierCSD()); // OK
		addProcessSupplier(new PeakIntegratorTypeSupplier()); // OK - Improve settings
		addProcessSupplier(new PeakQuantitationTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramCalculatorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramReportTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramExportTypeSupplierMSD()); // OK - Improve settings
		addProcessSupplier(new ChromatogramExportTypeSupplierCSD()); // OK - Improve settings
		addProcessSupplier(new ChromatogramExportTypeSupplierWSD()); // OK - Improve settings
		// MassSpectrumFilter?
		// NoiseCalculator?
	}

	private void addProcessSupplier(IProcessTypeSupplier processTypeSupplier) {

		try {
			for(String processorId : processTypeSupplier.getProcessorIds()) {
				if(processSupplierMap.containsKey(processorId)) {
					logger.warn("The following processor id is contained twice: " + processorId);
				} else {
					processSupplierMap.put(processorId, processTypeSupplier);
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	public List<IProcessTypeSupplier> getProcessorTypeSuppliers(List<DataType> dataTypes) {

		List<IProcessTypeSupplier> supplier = new ArrayList<>();
		for(IProcessTypeSupplier processTypeSupplier : processSupplierMap.values()) {
			exitloop:
			for(DataType dataType : dataTypes) {
				if(processTypeSupplier.getSupportedDataTypes().contains(dataType)) {
					if(!supplier.contains(processTypeSupplier)) {
						supplier.add(processTypeSupplier);
						break exitloop;
					}
				}
			}
		}
		return supplier;
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, IProcessMethod processMethod, IProgressMonitor monitor) {

		List<IChromatogramSelection> chromatogramSelections = new ArrayList<>();
		if(chromatogramSelection != null) {
			chromatogramSelections.add(chromatogramSelection);
		}
		return applyProcessor(chromatogramSelections, processMethod, monitor);
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo applyProcessor(List<IChromatogramSelection> chromatogramSelections, IProcessMethod processMethod, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			/*
			 * Process referenced chromatograms?
			 * Will make things more complex. Discussion needed.
			 * Think of RI calculation. Are the RIs of the master taken or the RIs of the selected reference?
			 * What about the column?
			 */
			for(IProcessEntry processEntry : processMethod) {
				String processorId = processEntry.getProcessorId();
				IProcessTypeSupplier processTypeSupplier = processSupplierMap.get(processorId);
				if(processTypeSupplier != null) {
					/*
					 * If processEntry.getJsonSettings() == {} (IProcessEntry.EMPTY_JSON_SETTINGS),
					 * the processSettings class will be null.
					 * The applyProcessor method manages how to handle this situation.
					 * By default, the system settings of the plugin shall be used instead.
					 */
					IProcessSettings processSettings = getProcessSettings(processEntry);
					processTypeSupplier.applyProcessor(chromatogramSelection, processorId, processSettings, monitor);
				}
			}
		}
		return processingInfo;
	}

	public IProcessSettings getProcessSettings(IProcessEntry processEntry) {

		IProcessSettings processSettings = null;
		if(processEntry != null) {
			Class<? extends IProcessSettings> clazz = processEntry.getProcessSettingsClass();
			if(clazz != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				try {
					String content = processEntry.getJsonSettings();
					if(IProcessEntry.EMPTY_JSON_SETTINGS.equals(content)) {
						logger.info("Process settings are empty. Default system settings are used instead.");
					} else {
						processSettings = objectMapper.readValue(content, clazz);
					}
				} catch(JsonParseException e) {
					logger.warn(e);
				} catch(JsonMappingException e) {
					logger.warn(e);
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
		//
		return processSettings;
	}

	public int validate(IProcessEntry processEntry) {

		if(processEntry != null) {
			if(processSupplierMap.containsKey(processEntry.getProcessorId())) {
				if(processEntry.getJsonSettings().equals(IProcessEntry.EMPTY_JSON_SETTINGS)) {
					return IStatus.INFO;
				} else {
					if(processEntry.getProcessSettingsClass() == null) {
						return IStatus.WARNING;
					} else {
						return IStatus.OK;
					}
				}
			} else {
				return IStatus.ERROR;
			}
		}
		/*
		 * Error if other checks failed.
		 */
		return IStatus.ERROR;
	}
}
