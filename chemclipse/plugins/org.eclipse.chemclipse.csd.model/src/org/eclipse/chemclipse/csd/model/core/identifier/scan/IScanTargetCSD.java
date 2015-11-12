/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.identifier.scan;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public interface IScanTargetCSD extends IIdentificationTarget {

	/**
	 * Returns the parent scan.<br/>
	 * The value could be also null.
	 * 
	 * @return {@link IScanCSD}
	 */
	IScanCSD getParentScan();

	/**
	 * Sets the parent scan.
	 * 
	 * @param parentScan
	 */
	void setParentScan(IScanCSD parentScan);
}
