/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.identifier.scan;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public interface IScanTargetWSD extends IIdentificationTarget {

	/**
	 * Returns the parent scan.<br/>
	 * The value could be also null.
	 * 
	 * @return {@link IScanWSD}
	 */
	IScanWSD getParentScan();

	/**
	 * Sets the parent scan.
	 * 
	 * @param parentScan
	 */
	void setParentScan(IScanWSD parentScan);
}
