/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.processing;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IExportConverterProcessingInfo extends IProcessingInfo {

	/**
	 * Returns the file.
	 * 
	 * @return file
	 * @throws TypeCastException
	 */
	File getFile() throws TypeCastException;

	/**
	 * Set the file.
	 * 
	 * @param file
	 */
	void setFile(File file);
}
