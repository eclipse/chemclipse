/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.core;

public interface ISupportSetter extends ISupport {

	/**
	 * Adds a ({@link ISupplier}) to the IdentifierSupport.
	 * 
	 * @param supplier
	 */
	void add(final ISupplier supplier);
}
