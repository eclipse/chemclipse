/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;

public interface ISnipPeakFilterSettings extends IPeakFilterSettings {

	int getIterations();

	void setIterations(int iterations);

	double getMagnificationFactor();

	void setMagnificationFactor(double magnificationFactor);

	int getTransitions();

	void setTransitions(int transitions);
}
