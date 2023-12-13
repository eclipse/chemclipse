/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractPeaks;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;

public class ChromatogramPeaks extends AbstractPeaks<IChromatogramPeak> {

	public ChromatogramPeaks() {

		super(IChromatogramPeak.class);
	}
}
