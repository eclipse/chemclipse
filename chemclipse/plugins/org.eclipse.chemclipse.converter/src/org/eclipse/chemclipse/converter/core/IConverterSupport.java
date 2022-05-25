/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - simplify API, deprecate individual getters in favor to a new filter approach
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.support.FileExtensionCompiler;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.converter.ISupplier;

public interface IConverterSupport {

	public static final Predicate<ISupplier> EXPORT_SUPPLIER = supplier -> supplier.isExportable();
	public static final Predicate<ISupplier> IMPORT_SUPPLIER = supplier -> supplier.isImportable();
	public static final Predicate<ISupplier> ALL_SUPPLIER = supplier -> true;

	/**
	 * Returns the filter extension which are actually registered at the
	 * chromatogram converter extension point.<br/>
	 * The filter extension are the specific chromatogram file extensions.
	 * Agilent has for example an filter extension (.D) which represents a
	 * chromatogram.
	 * 
	 * @return String[]
	 */
	default String[] getFilterExtensions(Predicate<? super ISupplier> filter) {

		List<String> extensions = new ArrayList<>();
		for(ISupplier supplier : getSupplier(filter)) {
			if(supplier.getDirectoryExtension().equals("")) {
				FileExtensionCompiler fileExtensionCompiler = new FileExtensionCompiler(supplier.getFileExtension(), true);
				extensions.add(fileExtensionCompiler.getCompiledFileExtension());
			} else {
				/*
				 * DirectoryExtension: Directory extension will return "*."
				 * otherwise directory could not be identified.
				 */
				extensions.add("*.");
			}
		}
		return extensions.toArray(new String[extensions.size()]);
	}

	@Deprecated
	default String[] getFilterExtensions() throws NoConverterAvailableException {

		String[] extensions = getFilterExtensions(ALL_SUPPLIER);
		if(extensions.length == 0) {
			throw new NoConverterAvailableException();
		}
		return extensions;
	}

	@Deprecated
	default String[] getExportableFilterExtensions() throws NoConverterAvailableException {

		String[] extensions = getFilterExtensions(EXPORT_SUPPLIER);
		if(extensions.length == 0) {
			throw new NoConverterAvailableException();
		}
		return extensions;
	}

	/**
	 * Returns the filter names which are actually registered at the
	 * chromatogram converter extension point.<br/>
	 * The filter names are the specific chromatogram file names to be displayed
	 * for example in the SWT FileDialog. Agilent has for example an filter name
	 * "Agilent Chromatogram (.D)".
	 * 
	 * @return String[]
	 */
	default String[] getFilterNames(Predicate<? super ISupplier> filter) {

		ArrayList<String> filterNames = new ArrayList<>();
		for(ISupplier supplier : getSupplier(filter)) {
			filterNames.add(supplier.getFilterName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Deprecated
	default String[] getFilterNames() throws NoConverterAvailableException {

		return getFilterNames(ALL_SUPPLIER);
	}

	/**
	 * Returns the same as getFilterNames() but with filter names (converter)
	 * that are exportable.
	 * 
	 * @return String[]
	 * @throws NoConverterAvailableException
	 */
	@Deprecated
	default String[] getExportableFilterNames() throws NoConverterAvailableException {

		String[] names = getFilterNames(EXPORT_SUPPLIER);
		if(names.length == 0) {
			throw new NoConverterAvailableException();
		}
		return names;
	}

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
	@Deprecated
	default String getConverterId(int index) throws NoConverterAvailableException {

		try {
			return getSupplier().get(index).getId();
		} catch(IndexOutOfBoundsException e) {
			throw new NoConverterAvailableException();
		}
	}

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
	default String getConverterId(String name, boolean exportConverterOnly) throws NoConverterAvailableException {

		Collection<? extends ISupplier> supplier = getSupplier(new Predicate<ISupplier>() {

			@Override
			public boolean test(ISupplier supplier) {

				return supplier.getFilterName().equals(name) && (!exportConverterOnly || supplier.isExportable());
			}
		});
		if(supplier.isEmpty()) {
			throw new NoConverterAvailableException();
		} else {
			return supplier.iterator().next().getId();
		}
	}

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
	default List<String> getAvailableConverterIds(File file) throws NoConverterAvailableException {

		List<ISupplier> suppliers = Converter.getSupplierForFile(file, getSupplier());
		if(suppliers.isEmpty()) {
			throw new NoConverterAvailableException();
		}
		//
		ArrayList<String> list = new ArrayList<>();
		for(ISupplier supplier : suppliers) {
			list.add(supplier.getId());
		}
		//
		return list;
	}

	/**
	 * Returns the list of all available suppliers
	 * 
	 * @return List<ISupplier>
	 */
	default Collection<? extends ISupplier> getSupplier(Predicate<? super ISupplier> filter) {

		List<ISupplier> list = new ArrayList<>();
		for(ISupplier supplier : getSupplier()) {
			if(filter.test(supplier)) {
				list.add(supplier);
			}
		}
		return list;
	}

	List<ISupplier> getSupplier();

	/**
	 * Returns the supplier with the given id.<br/>
	 * If no supplier with the given id is available, throw an exception.
	 * 
	 * @param id
	 * @throws NoConverterAvailableException
	 * @return supplier
	 */
	default ISupplier getSupplier(String id) throws NoConverterAvailableException {

		Collection<? extends ISupplier> collection = getSupplier(supplier -> supplier.getId().equals(id));
		if(collection.isEmpty()) {
			throw new NoConverterAvailableException();
		} else {
			return collection.iterator().next();
		}
	}

	/**
	 * Returns the list of all available export suppliers.
	 * 
	 * @return List<ISupplier>
	 */
	@Deprecated
	default List<ISupplier> getExportSupplier() {

		return new ArrayList<>(getSupplier(EXPORT_SUPPLIER));
	}

	DataCategory getDataCategory();

	/**
	 * 
	 * @return a name of this converter so a user can identify this among others
	 */
	String getName();

	/**
	 * 
	 * @return an id that can be used to store a reference to this converter support
	 */
	default String getID() {

		return "ConverterSupport:" + getClass().getName();
	}
}