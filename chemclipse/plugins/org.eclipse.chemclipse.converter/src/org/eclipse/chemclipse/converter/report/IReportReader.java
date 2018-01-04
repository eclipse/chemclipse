/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.report;

import java.io.File;

import org.eclipse.chemclipse.converter.model.IReportRowModel;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IReportReader {

	IReportRowModel read(File file, IProgressMonitor monitor);
}
