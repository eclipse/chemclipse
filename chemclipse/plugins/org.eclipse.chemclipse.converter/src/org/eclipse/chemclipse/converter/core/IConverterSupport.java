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
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;

public interface IConverterSupport {

	/*
	 * E.g. ".r##" as a wildcard for ".r00" and ".r01"
	 */
	String WILDCARD_NUMBER = "#";

	/**
	 * Returns the filter extension which are actually registered at the
	 * chromatogram converter extension point.<br/>
	 * The filter extension are the specific chromatogram file extensions.
	 * Agilent has for example an filter extension (.D) which represents a
	 * chromatogram.
	 * 
	 * @return String[]
	 * @throws NoChromatogramConverterAvailableException
	 */
	String[] getFilterExtensions() throws NoConverterAvailableException;

	// TODO JUnit
	/**
	 * Returns the same as getFilterExtensions() but with filter extensions
	 * (converter) that are exportable.<br/>
	 * The directory and/or file names will be exported in lower and upper case
	 * letters.<br/>
	 * They match the style of the FileDialog filter extension format (e.g.
	 * "*.cdf;*.CDF").
	 * 
	 * @return
	 * @throws NoConverterAvailableException
	 */
	String[] getExportableFilterExtensions() throws NoConverterAvailableException;

	/**
	 * Returns the filter names which are actually registered at the
	 * chromatogram converter extension point.<br/>
	 * The filter names are the specific chromatogram file names to be displayed
	 * for example in the SWT FileDialog. Agilent has for example an filter name
	 * "Agilent Chromatogram (.D)".
	 * 
	 * @return String[]
	 * @throws NoChromatogramConverterAvailableException
	 */
	String[] getFilterNames() throws NoConverterAvailableException;

	// TODO JUnit
	/**
	 * Returns the same as getFilterNames() but with filter names (converter)
	 * that are exportable.
	 * 
	 * @return String[]
	 * @throws NoConverterAvailableException
	 */
	String[] getExportableFilterNames() throws NoConverterAvailableException;

	/**
	 * Returns the id of the selected filter name.<br/>
	 * The id of the selected filter is used to determine which converter should
	 * be used to import or export the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoConverterAvailableException
	 */
	String getConverterId(int index) throws NoConverterAvailableException;

	/**
	 * Returns the converter id "org.eclipse.chemclipse.msd.converter.supplier.agilent" available in the list defined by its name, e.g. "Agilent Chromatogram (*.D/DATA.MS)".
	 * If more converter with the given name "Agilent Chromatogram (*.D/DATA.MS)" are stored, the first match will be returned. If exportConverterOnly is true, only a converter
	 * that is able to export the file will be returned.
	 * 
	 * @param name
	 * @param exportConverterOnly
	 * @return String
	 * @throws NoConverterAvailableException
	 */
	String getConverterId(String name, boolean exportConverterOnly) throws NoConverterAvailableException;

	/**
	 * Returns an ArrayList with all available converter ids for the given file.<br/>
	 * If the file ends with "*.D" all converter ids which can convert
	 * directories ending with "*.D" will be returned.<br/>
	 * The same thing if the file is a file and not a directory.<br/>
	 * The header of {@link MethodConverter} lists some file format
	 * endings.
	 * 
	 * @param file
	 * @return List<String>
	 * @throws NoConverterAvailableException
	 */
	List<String> getAvailableConverterIds(File file) throws NoConverterAvailableException;

	/**
	 * Returns the list of all available suppliers including those which do not offer an export function.<br/>
	 * RATHER USE OTHER METHODS THAN THIS!
	 * 
	 * @return List<ISupplier>
	 */
	List<ISupplier> getSupplier();

	/**
	 * Returns the supplier with the given id.<br/>
	 * If no supplier with the given id is available, throw an exception.
	 * 
	 * @param id
	 * @throws NoConverterAvailableException
	 * @return supplier
	 */
	ISupplier getSupplier(String id) throws NoConverterAvailableException;

	/**
	 * Returns the list of all available export suppliers.
	 * 
	 * @return List<ISupplier>
	 */
	List<ISupplier> getExportSupplier();
}
