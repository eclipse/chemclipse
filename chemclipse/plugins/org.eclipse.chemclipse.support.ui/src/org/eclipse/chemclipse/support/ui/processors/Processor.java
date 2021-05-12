/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;

public class Processor {

	private IProcessSupplier<?> processSupplier;
	private boolean active = false;
	private String imageFileName = IApplicationImage.IMAGE_EXECUTE_EXTENSION;

	public Processor(IProcessSupplier<?> processSupplier) {

		this.processSupplier = processSupplier;
	}

	public boolean isActive() {

		return active;
	}

	public void setActive(boolean active) {

		this.active = active;
	}

	public String getImageFileName() {

		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {

		if(imageFileName == null || imageFileName.isEmpty()) {
			this.imageFileName = IApplicationImage.IMAGE_EXECUTE_EXTENSION;
		} else {
			this.imageFileName = imageFileName;
		}
	}

	public IProcessSupplier<?> getProcessSupplier() {

		return processSupplier;
	}
}