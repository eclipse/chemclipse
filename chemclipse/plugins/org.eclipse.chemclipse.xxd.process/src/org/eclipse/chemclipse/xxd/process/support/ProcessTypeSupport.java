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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ClassifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.CombinedIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakQuantitationTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessTypeSupport {

	private static final Logger logger = Logger.getLogger(ProcessTypeSupport.class);
	//
	private List<IProcessTypeSupplier> processTypeSuppliers = new ArrayList<IProcessTypeSupplier>();
	private Map<String, IProcessTypeSupplier> processSupplierMap = new HashMap<>();

	public ProcessTypeSupport() {
		/*
		 * Add all available process supplier here.
		 */
		addProcessSupplier(new BaselineDetectorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramIdentifierTypeSupplier());
		addProcessSupplier(new ChromatogramIntegratorTypeSupplier()); // OK
		addProcessSupplier(new ClassifierTypeSupplier());
		addProcessSupplier(new CombinedIntegratorTypeSupplier());
		addProcessSupplier(new ChromatogramFilterTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramFilterTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakFilterTypeSupplier());
		addProcessSupplier(new PeakDetectorTypeSupplier());
		addProcessSupplier(new PeakIdentifierTypeSupplier());
		addProcessSupplier(new PeakIntegratorTypeSupplier());
		addProcessSupplier(new PeakQuantitationTypeSupplier());
		addProcessSupplier(new ChromatogramCalculatorTypeSupplier());
		// File Export
		// Report
	}

	private void addProcessSupplier(IProcessTypeSupplier processTypeSupplier) {

		/*
		 * Legacy
		 * Remove list when IChromatogramProcessEntry is removed.
		 */
		processTypeSuppliers.add(processTypeSupplier);
		/*
		 * This map stores the process type supplier to a given processor id.
		 */
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

	/**
	 * Returns an array of the processor names.
	 * 
	 * @param processCategory
	 * @param processorIds
	 * @return String[]
	 */
	public String[] getProcessorNames(String processCategory, String[] processorIds) {

		int size = processorIds.length;
		String[] processorNames = new String[size];
		for(int index = 0; index < size; index++) {
			processorNames[index] = getProcessorName(processCategory, processorIds[index]);
		}
		return processorNames;
	}

	/**
	 * Returns the processor name.
	 * 
	 * @param entry
	 * @return
	 */
	public String getProcessorName(IChromatogramProcessEntry entry) {

		return getProcessorName(entry.getProcessCategory(), entry.getProcessorId());
	}

	/**
	 * Returns the appropriate processor name.
	 * 
	 * @param processType
	 * @param processorId
	 * @return String
	 */
	public String getProcessorName(String processCategory, String processorId) {

		String processorName = IProcessTypeSupplier.NOT_AVAILABLE;
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			/*
			 * Check each registered process supplier.
			 */
			if(processTypeSupplier.getCategory().equals(processCategory)) {
				try {
					processorName = processTypeSupplier.getProcessorName(processorId);
					return processorName;
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		return processorName;
	}

	public List<IProcessTypeSupplier> getProcessorTypeSuppliers() {

		return Collections.unmodifiableList(processTypeSuppliers);
	}

	public String[] getProcessorCategories() {

		String[] processorNames = new String[processTypeSuppliers.size()];
		int index = 0;
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			processorNames[index] = processTypeSupplier.getCategory();
			index++;
		}
		return processorNames;
	}

	/**
	 * Returns the available plugin names.
	 * 
	 * @param processCategory
	 * @return
	 */
	public String[] getPluginIds(String processCategory) {

		String[] pluginIds = {IProcessTypeSupplier.NOT_AVAILABLE};
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			/*
			 * Check each registered process supplier.
			 */
			if(processTypeSupplier.getCategory().equals(processCategory)) {
				try {
					pluginIds = processTypeSupplier.getProcessorIds().toArray(new String[]{});
					return pluginIds;
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		return pluginIds;
	}

	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, IChromatogramProcessEntry processEntry, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			/*
			 * Check each registered process supplier.
			 */
			if(processTypeSupplier.getCategory().equals(processEntry.getProcessCategory())) {
				return processTypeSupplier.applyProcessor(chromatogramSelection, processEntry.getProcessorId(), null, monitor);
			}
		}
		processingInfo.addErrorMessage("Process Type Support", "There was now supplier to process the chromatogram selection.");
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, ProcessMethod processMethod, IProgressMonitor monitor) {

		List<IChromatogramSelection> chromatogramSelections = new ArrayList<>();
		chromatogramSelections.add(chromatogramSelection);
		return applyProcessor(chromatogramSelections, processMethod, monitor);
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo applyProcessor(List<IChromatogramSelection> chromatogramSelections, ProcessMethod processMethod, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			for(IProcessEntry processEntry : processMethod) {
				String processorId = processEntry.getProcessorId();
				IProcessTypeSupplier processTypeSupplier = processSupplierMap.get(processorId);
				if(processTypeSupplier != null) {
					IProcessSettings processSettings = getProcessSettings(processEntry);
					processTypeSupplier.applyProcessor(chromatogramSelection, processorId, processSettings, monitor);
				}
			}
		}
		return processingInfo;
	}

	public IProcessSettings getProcessSettings(IProcessEntry processEntry) {

		IProcessSettings processSettings = null;
		Class<? extends IProcessSettings> clazz = processEntry.getProcessSettingsClass();
		//
		if(clazz != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			try {
				String content = processEntry.getJsonSettings();
				processSettings = objectMapper.readValue(content, clazz);
			} catch(JsonParseException e) {
				logger.warn(e);
			} catch(JsonMappingException e) {
				logger.warn(e);
			} catch(IOException e) {
				logger.warn(e);
			}
		}
		//
		return processSettings;
	}
}
