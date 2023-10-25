/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.core.runtime.IProgressMonitor;

public class PCRWriter {

	public void writePlate(File file, IPlate plate, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		throw new UnsupportedOperationException();
	}
}
