/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - Ion-wise savitzky-golay on msd data
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ChromatogramFilterSettings extends AbstractChromatogramFilterSettings {

	private static final Logger logger = Logger.getLogger(ChromatogramFilterSettings.class);
	//
	@JsonProperty(value = "Order", defaultValue = "2")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_ORDER, maxValue = PreferenceSupplier.MAX_ORDER)
	private int order = 2;
	@JsonProperty(value = "Width", defaultValue = "5")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_WIDTH, maxValue = PreferenceSupplier.MAX_WIDTH)
	private int width = 5;
	@JsonProperty(value = "Filter individual ion channels", defaultValue = "true")
	@JsonPropertyDescription(value = "Per Ion Filter Calculation.")
	private boolean perIonCalculation = true;

	public int getDerivative() {

		return 0;
	}

	public void setDerivative(int derivative) {

		if(derivative != 0) {
			logger.debug("Derivative is not supported");
		}
	}

	public int getOrder() {

		return order;
	}

	public void setOrder(int order) {

		this.order = order;
	}

	public int getWidth() {

		return width;
	}

	public void setWidth(int width) {

		this.width = width;
	}

	public boolean getPerIonCalculation() {

		return this.perIonCalculation;
	}

	public void setPerIonCalculation(boolean perIonCalculation) {

		this.perIonCalculation = perIonCalculation;
	}
}
