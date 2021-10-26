/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import org.eclipse.chemclipse.model.core.IMarkedSignal;
import org.eclipse.chemclipse.support.text.ILabel;

public interface IMarkedWavelength extends IMarkedSignal {

	enum WavelengthMarkMode implements ILabel {

		/**
		 * In this mode, all wavelengths in the list are considered as an exclusion, that means apply the given function to all except the given wavelengths
		 */
		EXCLUDE("Exclude"),
		/**
		 * In this mode, all wavelengths in the list are considered as an inclusion, that means apply the given function to all wavelengths given
		 */
		INCLUDE("Include");

		private String label = "";

		private WavelengthMarkMode(String label) {

			this.label = label;
		}

		@Override
		public String label() {

			return label;
		}

		public static String[][] getOptions() {

			return ILabel.getOptions(values());
		}
	}

	double getWavelength();

	void setWavelength(double wavelength);

	int getMagnification();

	void setMagnification(int magnification);
}
