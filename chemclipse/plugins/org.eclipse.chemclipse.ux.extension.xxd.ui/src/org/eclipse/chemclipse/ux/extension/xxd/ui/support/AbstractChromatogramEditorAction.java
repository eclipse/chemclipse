/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public abstract class AbstractChromatogramEditorAction<T> implements IChromatogramEditorAction<T> {

	public IProcessingInfo<T> validate(IChromatogramSelection<?, ?> chromatogramSelection) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addMessage(new ProcessingMessage(MessageType.ERROR, "Editor Action", "The chromatogram selection is null."));
		}
		return processingInfo;
	}
}
