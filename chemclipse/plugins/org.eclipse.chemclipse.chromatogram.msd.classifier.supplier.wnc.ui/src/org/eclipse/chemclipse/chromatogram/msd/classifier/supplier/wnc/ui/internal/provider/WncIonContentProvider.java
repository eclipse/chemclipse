/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.jface.viewers.IStructuredContentProvider;

public class WncIonContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof IWncIons wncIons) {
			return wncIons.toArray();
		}
		//
		return new Object[0];
	}
}
