/*******************************************************************************
 * Copyright (c) 2013, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public interface IQuantitationPeakMSD {

	double getConcentration();

	void setConcentration(double concentration);

	String getConcentrationUnit();

	IPeakMSD getReferencePeakMSD();
}
