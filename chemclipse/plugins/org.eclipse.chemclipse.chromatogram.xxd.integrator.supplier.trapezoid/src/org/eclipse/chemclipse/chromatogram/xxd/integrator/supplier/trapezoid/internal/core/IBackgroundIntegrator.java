/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IBackgroundIntegrator {

	/**
	 * Integrates the chromatogram selection background.
	 * 
	 * @param chromatogram
	 *            selection
	 * @return double
	 */
	@SuppressWarnings("rawtypes")
	double integrate(IChromatogramSelection chromatogramSelection);
}
