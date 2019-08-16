/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.processor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public abstract class AbstractChromatogramProcessor implements IChromatogramProcessor {

	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public AbstractChromatogramProcessor(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}
}
