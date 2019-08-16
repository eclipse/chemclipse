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

public class RetentionIndexWindow extends AbstractIdentificationWindow implements IRetentionIndexWindow {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -6789731365073867766L;
	//
	private float retentionIndex;

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		if(retentionIndex >= 0) {
			this.retentionIndex = retentionIndex;
		}
	}
}
