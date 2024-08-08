/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public abstract class AbstractFirstDerivativePreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final float MIN_SN_RATIO_MIN = 0.0f; // 0 = all peaks will be added
	public static final float MIN_SN_RATIO_MAX = Float.MAX_VALUE; // 0 = all peaks will be added
	public static final int MIN_WINDOW_SIZE = 0;
	public static final int MAX_WINDOW_SIZE = 45;
}