/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

public interface IRetentionTime extends IVariable {

	/*
	 * Retention time in minutes
	 */
	String TYPE = "Retention Time [min]";

	int getRetentionTime();

	double getRetentionTimeMinutes();

	void setRetentioTime(int retentionTime);
}
