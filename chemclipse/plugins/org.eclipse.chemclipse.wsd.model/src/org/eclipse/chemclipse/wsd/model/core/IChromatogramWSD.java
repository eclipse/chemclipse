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
package org.eclipse.chemclipse.wsd.model.core;

import org.eclipse.chemclipse.model.core.IChromatogram;

public interface IChromatogramWSD extends IChromatogram {

	/**
	 * Returns a supplier scan or null, if no supplier
	 * spectrum is stored.
	 * 
	 * @param scan
	 * @return {@link IScanWSD}
	 */
	IScanWSD getSupplierScan(int scan);
}
