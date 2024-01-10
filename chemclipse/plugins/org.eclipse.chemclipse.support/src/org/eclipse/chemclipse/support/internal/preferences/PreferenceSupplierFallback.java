/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.internal.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

/**
 * NOTE: This class is only used for fallback purposes.
 */
public final class PreferenceSupplierFallback extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	@Override
	public String getPreferenceNode() {

		return "org.eclipse.chemclipse.support.internal.preferences.fallback";
	}

	@Override
	public void initializeDefaults() {

	}
}