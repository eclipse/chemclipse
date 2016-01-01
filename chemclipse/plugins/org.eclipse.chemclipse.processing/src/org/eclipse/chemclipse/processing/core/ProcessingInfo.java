/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

public class ProcessingInfo extends AbstractProcessingInfo implements IProcessingInfo {

	public ProcessingInfo() {
		super();
	}

	public ProcessingInfo(IProcessingInfo processingInfo) {
		super(processingInfo);
	}
}
