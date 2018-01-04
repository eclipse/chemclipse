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
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

public interface IRetentionIndicesSupplier {

	/**
	 * The id of the extension point: e.g.
	 * (org.eclipse.chemclipse.chromatogram.alignment.supplier.amdis)
	 * 
	 * @return String
	 */
	String getId();

	/**
	 * A short description of the functionality of the extension point.
	 * 
	 * @return String
	 */
	String getDescription();

	/**
	 * The filter that will be shown in the FileDialog.
	 * 
	 * @return String
	 */
	String getFilterName();

	/**
	 * The file extension, e.g. AMDIS (.cal) will be returned.<br/>
	 * If the file extension has a value, it starts in every case with a point.
	 * 
	 * @return String
	 */
	String getFileExtension();

	/**
	 * Describes whether the chromatogram is exportable or not.
	 * 
	 * @return boolean
	 */
	boolean isExportable();;

	/**
	 * Describes whether the chromatogram is importable or not.
	 * 
	 * @return boolean
	 */
	boolean isImportable();
}
