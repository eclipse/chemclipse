/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public interface ISumareaIntegrator {

	float INTEGRATION_STEPS = 100.0f; // steps of 100 milliseconds.
	String DESCRIPTION = "Integrator Trapezoid";

	/*
	 * Integrates the TIC signal.
	 */
	double integrate(IChromatogramSelectionMSD chromatogramSelection);

	/*
	 * Integrates the selected ion.
	 */
	double integrate(IChromatogramSelectionMSD chromatogramSelection, int ion);
}
