/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.support;

import java.io.IOException;
import java.io.ObjectInput;

import org.eclipse.chemclipse.nmr.model.core.MeasurementNMR;

public class StreamObjectReader {

	public static MeasurementNMR readObject(ObjectInput objectInput) throws ClassNotFoundException, IOException {

		return (MeasurementNMR)objectInput.readObject();
	}
}
