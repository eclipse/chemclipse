/*******************************************************************************
 * Copyright (c) 2012, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ArrayReaderTestImplementation extends AbstractArrayReader implements IArrayReader {

	public ArrayReaderTestImplementation(byte[] data) {

		super(data);
	}

	public ArrayReaderTestImplementation(File file) throws FileNotFoundException, IOException {

		super(file);
	}
}
