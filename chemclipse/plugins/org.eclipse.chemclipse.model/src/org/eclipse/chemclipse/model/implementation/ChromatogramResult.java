/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractChromatogramResult;
import org.eclipse.chemclipse.model.core.IChromatogramResult;

public class ChromatogramResult extends AbstractChromatogramResult implements IChromatogramResult {

	public ChromatogramResult(String identifier, String description, Object result) {

		super(identifier, description, result);
	}
}
