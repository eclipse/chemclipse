/*******************************************************************************
 * Copyright (c) 2010, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.massspectrum;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public interface IMassSpectrumTarget extends IIdentificationTarget {

	/**
	 * Returns the parent mass spectrum of the actual target.<br/>
	 * The value could be also null.
	 * 
	 * @return {@link IScanMSD}
	 */
	IScanMSD getParentMassSpectrum();

	/**
	 * Sets the parent mass spectrum.
	 * 
	 * @param parentPeak
	 */
	void setParentMassSpectrum(IScanMSD massSpectrum);
}
