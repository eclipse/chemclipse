/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.processing.peak;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IPeakImportConverterProcessingInfo extends IProcessingInfo {

	/**
	 * Returns an IPeaks instance.
	 * 
	 * @return IPeaks
	 * @throws TypeCastException
	 */
	IPeaks getPeaks() throws TypeCastException;

	/**
	 * Sets the IPeaks instance.
	 * 
	 * @param peaks
	 */
	void setPeaks(IPeaks peaks);
}
