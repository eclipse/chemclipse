/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.io;

import org.eclipse.chemclipse.converter.io.AbstractChromatogramReader;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.ChromatogramConfiguration;

public abstract class AbstractChromatogramMSDReader extends AbstractChromatogramReader implements IChromatogramMSDReader {

	private ChromatogramConfiguration chromatogramConfiguration;

	public ChromatogramConfiguration getChromatogramConfiguration() {

		return chromatogramConfiguration;
	}

	public AbstractChromatogramMSDReader setChromatogramConfiguration(ChromatogramConfiguration chromatogramConfiguration) {

		this.chromatogramConfiguration = chromatogramConfiguration;
		return this;
	}
}
