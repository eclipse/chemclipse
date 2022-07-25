/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.io.IChromatogramReader;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramWSDReader extends IChromatogramReader {

	/**
	 * Reads an chromatogram.<br/>
	 * If the chromatogram can not be parsed, null will be returned.
	 * 
	 * @param file
	 * @return IChromatogram
	 * @throws IOException
	 */
	IChromatogramWSD read(File file, IProgressMonitor monitor) throws IOException;
}
