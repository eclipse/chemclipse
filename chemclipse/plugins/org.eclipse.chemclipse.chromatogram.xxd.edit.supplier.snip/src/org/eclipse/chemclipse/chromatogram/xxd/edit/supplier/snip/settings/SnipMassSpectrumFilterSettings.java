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

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;

public class SnipMassSpectrumFilterSettings extends AbstractMassSpectrumFilterSettings implements ISnipMassSpectrumFilterSettings {

	private int iterations;
	private double magnificationFactor;
	private int transitions;

	@Override
	public int getIterations() {

		return iterations;
	}

	@Override
	public void setIterations(int iterations) {

		this.iterations = iterations;
	}

	@Override
	public double getMagnificationFactor() {

		return magnificationFactor;
	}

	@Override
	public void setMagnificationFactor(double magnificationFactor) {

		this.magnificationFactor = magnificationFactor;
	}

	@Override
	public int getTransitions() {

		return transitions;
	}

	@Override
	public void setTransitions(int transitions) {

		this.transitions = transitions;
	}
}
