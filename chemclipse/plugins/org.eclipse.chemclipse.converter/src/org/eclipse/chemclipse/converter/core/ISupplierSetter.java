/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import org.eclipse.chemclipse.processing.converter.ISupplier;

public interface ISupplierSetter extends ISupplier {

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.msd.supplier.agilent".
	 * 
	 * @param id
	 */
	void setId(final String id);

	/**
	 * Sets the description of the chromatogram supplier.
	 * 
	 * @param description
	 */
	void setDescription(final String description);

	/**
	 * Set the filter name to be shown in the FileDialog.
	 * 
	 * @param filterName
	 */
	void setFilterName(final String filterName);

	/**
	 * Sets the file extension, e.g. Agilent (.MS). If for example only (MS)
	 * will be stored, a leading point will automatically be added (.MS).
	 * 
	 * @param fileExtension
	 */
	void setFileExtension(final String fileExtension);

	/**
	 * Sets the default file name, e.g. Agilent (DATA).
	 * 
	 * @param fileName
	 */
	void setFileName(final String fileName);

	/**
	 * Sets the chromatogram directory extension if exists, e.g. Agilent (.D).
	 * If for example only (D) will be stored, a leading point will
	 * automatically be added (.D).
	 * 
	 * @param directoryExtension
	 */
	void setDirectoryExtension(final String directoryExtension);

	/**
	 * Sets whether the chromatogram is exportable or not.
	 * 
	 * @param isExportable
	 */
	void setExportable(final boolean isExportable);

	/**
	 * Sets whether the chromatogram is importable or not.
	 * 
	 * @param isImportable
	 */
	public void setImportable(final boolean isImportable);

	/**
	 * Sets the magic number matcher.
	 * 
	 * @param magicNumberMatcher
	 */
	public void setMagicNumberMatcher(final IMagicNumberMatcher magicNumberMatcher);

	/**
	 * Sets the file content matcher.
	 * 
	 * @param fileContentMatcher
	 */
	public void setFileContentMatcher(final IFileContentMatcher fileContentMatcher);
}