/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.AbstractBaselineDetectorSettings;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

public class SnipBaselineDetectorSettings extends AbstractBaselineDetectorSettings implements ISnipBaselineDetectorSettings {

	private int iterations;
	private WindowSize windowSize;

	public SnipBaselineDetectorSettings() {
		windowSize = WindowSize.SCANS_5;
	}

	@Override
	public WindowSize getWindowSize() {

		return windowSize;
	}

	@Override
	public void setWindowSize(WindowSize windowSize) {

		this.windowSize = windowSize;
	}

	@Override
	public int getIterations() {

		return iterations;
	}

	@Override
	public void setIterations(int iterations) {

		this.iterations = iterations;
	}
}
