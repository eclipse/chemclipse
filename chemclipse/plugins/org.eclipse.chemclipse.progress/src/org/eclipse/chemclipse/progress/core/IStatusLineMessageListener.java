/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.progress.core;

public interface IStatusLineMessageListener {

	/**
	 * Sets the message given by message and {@link InfoType}.
	 * 
	 * @param infoType
	 * @param message
	 */
	void setInfo(InfoType infoType, String message);
}
