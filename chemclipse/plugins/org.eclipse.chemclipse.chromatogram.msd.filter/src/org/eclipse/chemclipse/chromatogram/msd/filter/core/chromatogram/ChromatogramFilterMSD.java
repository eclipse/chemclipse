/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ChromatogramFilterMSD {

	private static final Logger logger = Logger.getLogger(ChromatogramFilterMSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.filter.chromatogramFilterSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FILTER_NAME = "filterName";
	private static final String FILTER = "filter";
	private static final String FILTER_SETTINGS = "filterSettings";
	//
	private static final String PROCESSING_DESCRIPTION = "Chromatogram Filter MSD";
	private static final String NO_CHROMATOGRAM_FILTER_AVAILABLE = "There is no chromatogram filter available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private ChromatogramFilterMSD() {
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
	public static IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IChromatogramFilterMSD chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
		} else {
			processingInfo = new ProcessingInfo();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	// TODO JUnit
	/**
	 * Applies the specified filter, but retrieves the IChromatogramFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param chromatogramSelection
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, String filterId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IChromatogramFilterMSD chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, monitor);
		} else {
			processingInfo = new ProcessingInfo();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	public static IChromatogramFilterSupportMSD getChromatogramFilterSupport() {

		ChromatogramFilterSupplierMSD supplier;
		ChromatogramFilterSupportMSD filterSupport = new ChromatogramFilterSupportMSD();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramFilterSupplierMSD();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			if(element.getAttribute(FILTER_SETTINGS) != null) {
				try {
					IChromatogramFilterSettings instance = (IChromatogramFilterSettings)element.createExecutableExtension(FILTER_SETTINGS);
					supplier.setSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setSettingsClass(null);
				}
			}
			filterSupport.add(supplier);
		}
		return filterSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IChromatogramFilterMSD} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IChromatogramFilterMSD getChromatogramFilter(final String filterId) {

		IConfigurationElement element;
		element = getConfigurationElement(filterId);
		IChromatogramFilterMSD instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramFilterMSD)element.createExecutableExtension(FILTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramFilterMSD} instance or null if none is
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
