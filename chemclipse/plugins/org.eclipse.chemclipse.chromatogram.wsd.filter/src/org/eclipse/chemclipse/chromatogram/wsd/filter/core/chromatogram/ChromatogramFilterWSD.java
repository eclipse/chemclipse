/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ChromatogramFilterWSD {

	private static final Logger logger = Logger.getLogger(ChromatogramFilterWSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.wsd.filter.chromatogramFilterSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FILTER_NAME = "filterName";
	private static final String FILTER = "filter";
	private static final String FILTER_SETTINGS = "filterSettings";
	//
	private static final String PROCESSING_DESCRIPTION = "Chromatogram Filter";
	private static final String NO_CHROMATOGRAM_FILTER_AVAILABLE = "There is no chromatogram filter available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private ChromatogramFilterWSD() {

	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IChromatogramFilterSettings} on the {@link IChromatogramSelectionMSD} .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.<br/>
	 * You could think of filters that for example remove background
	 * automatically or mean normalize the chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<?> applyFilter(IChromatogramSelectionWSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo;
		IChromatogramFilterWSD chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
			chromatogramSelection.getChromatogram().setDirty(true);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	// TODO JUnit
	/**
	 * Applies the specified filter, but retrieves the IChromatogramFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param chromatogramSelection
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<?> applyFilter(IChromatogramSelectionWSD chromatogramSelection, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo;
		IChromatogramFilterWSD chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	public static IChromatogramFilterSupportWSD getChromatogramFilterSupport() {

		ChromatogramFilterSupplierWSD supplier;
		ChromatogramFilterSupportWSD filterSupport = new ChromatogramFilterSupportWSD();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramFilterSupplierWSD();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			if(element.getAttribute(FILTER_SETTINGS) != null) {
				try {
					IChromatogramFilterSettings instance = (IChromatogramFilterSettings)element.createExecutableExtension(FILTER_SETTINGS);
					supplier.setFilterSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setFilterSettingsClass(null);
				}
			}
			filterSupport.add(supplier);
		}
		return filterSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IChromatogramFilterWSD} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IChromatogramFilterWSD getChromatogramFilter(final String filterId) {

		IConfigurationElement element;
		element = getConfigurationElement(filterId);
		IChromatogramFilterWSD instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramFilterWSD)element.createExecutableExtension(FILTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramFilterWSD} instance or null if none is
	 * available.
	 * 
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if("".equals(filterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(filterId)) {
				return element;
			}
		}
		return null;
	}
	// --------------------------------------------private methods
}
