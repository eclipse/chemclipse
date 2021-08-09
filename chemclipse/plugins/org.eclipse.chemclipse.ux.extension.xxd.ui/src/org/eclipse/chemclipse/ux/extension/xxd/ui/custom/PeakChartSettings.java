/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.custom;

public class PeakChartSettings {

	private boolean showChromatogramTIC = true;
	private boolean showChromatogramTraces = true;
	private boolean showBaseline = true;

	public boolean isShowChromatogramTIC() {

		return showChromatogramTIC;
	}

	public void setShowChromatogramTIC(boolean showChromatogramTIC) {

		this.showChromatogramTIC = showChromatogramTIC;
	}

	public boolean isShowChromatogramTraces() {

		return showChromatogramTraces;
	}

	public void setShowChromatogramTraces(boolean showChromatogramTraces) {

		this.showChromatogramTraces = showChromatogramTraces;
	}

	public boolean isShowBaseline() {

		return showBaseline;
	}

	public void setShowBaseline(boolean showBaseline) {

		this.showBaseline = showBaseline;
	}
}
