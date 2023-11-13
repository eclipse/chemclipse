/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - add color compensation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

public enum SampleType {
	UNKNOWN, //
	NON_TEMPLATE_CONTROL, //
	NO_AMPLICATION_CONTROL, //
	STANDARD_SAMPLE, //
	NO_TARGET_PRESENT, //
	MINUS_RT, //
	POSITIVE_CONTROL, //
	OPTICAL_CALIBRATOR;
}
