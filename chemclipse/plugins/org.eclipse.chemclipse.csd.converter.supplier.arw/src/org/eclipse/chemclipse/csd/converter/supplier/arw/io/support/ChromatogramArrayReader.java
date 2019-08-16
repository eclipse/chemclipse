/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.arw.io.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.io.support.AbstractArrayReader;

public class ChromatogramArrayReader extends AbstractArrayReader implements IChromatogramArrayReader {

	public ChromatogramArrayReader(File file) throws FileNotFoundException, IOException {
		super(file);
	}

	@Override
	public long readFileFormat() {

		return read4BULongBE();
	}

	@Override
	public int readStartRetentionTime() {

		return (int)read4BFloatBE();
	}

	@Override
	public int readStopRetentionTime() {

		return (int)read4BFloatBE();
	}
}
