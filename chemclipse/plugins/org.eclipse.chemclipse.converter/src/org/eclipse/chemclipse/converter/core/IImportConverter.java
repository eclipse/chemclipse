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
package org.eclipse.chemclipse.converter.core;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public interface IImportConverter {

	/**
	 * This method validates the file which contains the information to be
	 * imported.
	 * 
	 * @param chromatogram
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(File file);
}
