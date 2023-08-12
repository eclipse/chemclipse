/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIons;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.settings.AbstractChromatogramClassifierSettings;

public class ClassifierSettings extends AbstractChromatogramClassifierSettings {

	private IWncIons wncIons;

	public ClassifierSettings() {

		wncIons = new WncIons();
	}

	public IWncIons getWNCIons() {

		return wncIons;
	}
}