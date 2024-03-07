/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;
import org.eclipse.chemclipse.wsd.model.core.ISignalWSD;
import org.eclipse.chemclipse.wsd.model.core.ISpectrumWSD;

public class SpectrumWSD extends AbstractMeasurementInfo implements ISpectrumWSD {

	private static final long serialVersionUID = 1112263452696348714L;
	//
	private TreeSet<ISignalWSD> signals = new TreeSet<>();

	@Override
	public TreeSet<ISignalWSD> getSignals() {

		return signals;
	}
}