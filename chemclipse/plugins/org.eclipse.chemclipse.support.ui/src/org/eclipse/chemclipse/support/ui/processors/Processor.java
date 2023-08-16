/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - add default icons
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImage;

public class Processor {

	/*
	 * Backward compatibility with -1.
	 * Set selected processors at the end of the list (MAX_VALUE).
	 */
	public static final int INDEX_NONE = -1;
	public static final int INDEX_MAX = Integer.MAX_VALUE;
	//
	private IProcessSupplier<?> processSupplier;
	private boolean active = false;
	private int index = INDEX_NONE; // position in list
	private String imageFileName;

	public Processor(IProcessSupplier<?> processSupplier) {

		this.processSupplier = processSupplier;
		setImageFileName(ProcessorSupport.getDefaultIcon(processSupplier));
	}

	public IProcessSupplier<?> getProcessSupplier() {

		return processSupplier;
	}

	public boolean isActive() {

		return active;
	}

	public void setActive(boolean active) {

		this.active = active;
	}

	public int getIndex() {

		return index;
	}

	public void setIndex(int index) {

		this.index = index;
	}

	public String getImageFileName() {

		return ApplicationImage.adjustLegacyPath(imageFileName);
	}

	public void setImageFileName(String imageFileName) {

		if(imageFileName != null && !imageFileName.isEmpty()) {
			this.imageFileName = imageFileName;
		} else {
			imageFileName = ProcessorSupport.PROCESSOR_IMAGE_DEFAULT;
		}
	}
}