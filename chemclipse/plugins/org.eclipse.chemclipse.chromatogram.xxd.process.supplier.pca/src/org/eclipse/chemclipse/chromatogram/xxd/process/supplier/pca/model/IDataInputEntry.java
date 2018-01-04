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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public interface IDataInputEntry {

	/**
	 * Return the name of input file with extension
	 *
	 * @return
	 */
	String getFileName();

	String getGroupName();

	/**
	 * Returns the path to the input file.
	 *
	 * @return String
	 */
	String getInputFile();

	/**
	 * Returns the name of the input file without extension.
	 *
	 * @return String
	 */
	String getName();

	void setGroupName(String groupName);
}
