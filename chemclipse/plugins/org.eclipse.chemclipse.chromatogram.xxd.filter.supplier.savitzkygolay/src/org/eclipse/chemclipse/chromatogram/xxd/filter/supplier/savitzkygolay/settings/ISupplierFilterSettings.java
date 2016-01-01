/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

public interface ISupplierFilterSettings extends IChromatogramFilterSettings {

	int getDerivative();

	void setDerivative(int derivative);

	int getOrder();

	void setOrder(int order);

	int getWidth();

	void setWidth(int width);
}
