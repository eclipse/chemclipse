/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractMeasurementResult;
import org.eclipse.chemclipse.model.core.IMeasurementResult;

public class MeasurementResult extends AbstractMeasurementResult implements IMeasurementResult {

	public MeasurementResult(String name, String identifier, String description, Object result) {
		super(name, identifier, description, result);
	}
}
