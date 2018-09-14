/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import org.eclipse.chemclipse.model.core.IMarkedSignal;

public interface IMarkedWavelength extends IMarkedSignal {

	double getWavelength();

	void setWavelength(double wavelength);

	int getMagnification();

	void setMagnification(int magnification);
}
