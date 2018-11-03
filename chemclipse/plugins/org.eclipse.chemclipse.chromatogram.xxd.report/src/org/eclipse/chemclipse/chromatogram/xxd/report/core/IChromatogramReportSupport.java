/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;

public interface IChromatogramReportSupport {

	/**
	 * Returns the report extensions which are actually registered at the
	 * chromatogram report extension point.<br/>
	 * The report extensions are the specific chromatogram report file extensions.
	 * 
	 * @return String[]
	 * @throws NoChromatogramConverterAvailableException
	 */
	String[] getReportExtensions() throws NoReportSupplierAvailableException;

	/**
	 * Returns the report names which are actually registered at the
	 * chromatogram report extension point.<br/>
	 * The report names are the specific chromatogram report names to be displayed
	 * for example in the SWT FileDialog.
	 * 
	 * @return String[]
	 * @throws NoChromatogramConverterAvailableException
	 */
	String[] getFilterNames() throws NoReportSupplierAvailableException;

	/**
	 * Returns the id of the selected report name.<br/>
	 * The id of the selected report is used to determine which report should
	 * be used to import or export the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoReportSupplierAvailableException
	 */
	String getReportSupplierId(int index) throws NoReportSupplierAvailableException;

	/**
	 * Returns the report id e.g. "org.eclipse.chemclipse.chromatogram.xxd.report.supplier.peaks" available in the list defined by its name, e.g. "Peak Report (*.pdf)".
	 * If more reports with the given name are stored, the first match will be returned.
	 * 
	 * @param name
	 * @return String
	 * @throws NoReportSupplierAvailableException
	 */
	String getReportSupplierId(String name) throws NoReportSupplierAvailableException;

	/**
	 * Returns the list of all available report suppliers.<br/>
	 * RATHER USE OTHER METHODS THAN THIS!
	 * 
	 * @return List<ISupplier>
	 */
	List<IChromatogramReportSupplier> getReportSupplier();

	/**
	 * Returns the supplier with the given id.<br/>
	 * If no supplier with the given id is available, throw an exception.
	 * 
	 * @param id
	 * @throws NoReportSupplierAvailableException
	 * @return supplier
	 */
	IChromatogramReportSupplier getReportSupplier(String id) throws NoReportSupplierAvailableException;

	List<String> getAvailableProcessorIds() throws NoReportSupplierAvailableException;
}
