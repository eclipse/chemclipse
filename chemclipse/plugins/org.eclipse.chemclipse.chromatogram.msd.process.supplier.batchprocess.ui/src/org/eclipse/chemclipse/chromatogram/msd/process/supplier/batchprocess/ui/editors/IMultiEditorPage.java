/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.editors;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IMultiEditorPage {

	/**
	 * Returns the page index.
	 * 
	 * @return int
	 */
	int getPageIndex();

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Sets the batch process job.
	 * 
	 * @param batchProcessJob
	 */
	void setBatchProcessJob(IBatchProcessJob batchProcessJob);
}