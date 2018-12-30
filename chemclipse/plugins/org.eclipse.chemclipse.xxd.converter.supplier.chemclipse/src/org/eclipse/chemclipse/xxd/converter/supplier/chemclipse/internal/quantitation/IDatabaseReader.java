/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.quantitation;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDatabaseReader {

	IQuantitationDatabase convert(File file, IProgressMonitor monitor) throws IOException;
}
