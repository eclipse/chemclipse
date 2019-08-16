/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

public interface IRetentionTimeWindow extends IIdentificationWindow {

	int getRetentionTime();

	void setRetentionTime(int retentionTime);

	boolean isUseMilliseconds();

	void setUseMilliseconds(boolean useMilliseconds);

	boolean isRetentionTimeInWindow(int retentionTime);
}
