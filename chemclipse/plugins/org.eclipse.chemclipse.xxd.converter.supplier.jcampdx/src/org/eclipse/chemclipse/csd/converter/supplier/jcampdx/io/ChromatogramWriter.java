/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.jcampdx.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramCSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

	}
}
