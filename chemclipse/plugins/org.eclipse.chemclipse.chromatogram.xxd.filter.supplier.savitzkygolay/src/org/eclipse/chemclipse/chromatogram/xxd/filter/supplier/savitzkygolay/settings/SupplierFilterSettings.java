/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	private int derivative;
	private int order;
	private int width;

	@Override
	public int getDerivative() {

		return derivative;
	}

	@Override
	public void setDerivative(int derivative) {

		this.derivative = derivative;
	}

	@Override
	public int getOrder() {

		return order;
	}

	@Override
	public void setOrder(int order) {

		this.order = order;
	}

	@Override
	public int getWidth() {

		return width;
	}

	@Override
	public void setWidth(int width) {

		this.width = width;
	}
}
