/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.logging.core.Logger;

public class ChromatogramFilter {

	private static final Logger logger = Logger.getLogger(ChromatogramFilter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.filter.chromatogramFilterSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FILTER_NAME = "filterName";
	private static final String FILTER = "filter";
	//
	private static final String PROCESSING_DESCRIPTION = "Chromatogram Filter";
	private static final String NO_CHROMATOGRAM_FILTER_AVAILABLE = "There is no chromatogram filter available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private ChromatogramFilter() {

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
	 * @return {@link IChromatogramFilterProcessingInfo}
	 */
	public static IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor) {

		IChromatogramFilterProcessingInfo processingInfo;
		IChromatogramFilter chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
		} else {
			processingInfo = new ChromatogramFilterProcessingInfo();
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
	 * @return {@link IChromatogramFilterProcessingInfo}
	 */
	public static IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, String filterId, IProgressMonitor monitor) {

		IChromatogramFilterProcessingInfo processingInfo;
		IChromatogramFilter chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, monitor);
		} else {
			processingInfo = new ChromatogramFilterProcessingInfo();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	public static IChromatogramFilterSupport getChromatogramFilterSupport() {

		ChromatogramFilterSupplier supplier;
		ChromatogramFilterSupport filterSupport = new ChromatogramFilterSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramFilterSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			filterSupport.add(supplier);
		}
		return filterSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IChromatogramFilter} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IChromatogramFilter getChromatogramFilter(final String filterId) {

		IConfigurationElement element;
		element = getConfigurationElement(filterId);
		IChromatogramFilter instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramFilter)element.createExecutableExtension(FILTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramFilter} instance or null if none is
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
