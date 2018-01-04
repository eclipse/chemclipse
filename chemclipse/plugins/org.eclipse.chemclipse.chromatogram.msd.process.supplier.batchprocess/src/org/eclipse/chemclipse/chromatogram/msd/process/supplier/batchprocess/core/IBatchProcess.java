/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface IBatchProcess {

	/**
	 * Executes the batch process file and returns a batch process report.
	 * 
	 * @param batchProcessJob
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo execute(IBatchProcessJob batchProcessJob, IProgressMonitor monitor);
}
