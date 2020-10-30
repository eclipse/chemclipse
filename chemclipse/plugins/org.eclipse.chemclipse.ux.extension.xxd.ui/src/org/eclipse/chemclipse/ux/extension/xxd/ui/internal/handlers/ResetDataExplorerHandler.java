/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Move custom made toolbar to the native toolbar with E4Handlers
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.DataExplorerPart;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class ResetDataExplorerHandler {

	@Execute
	public void execute(MPart part) {

		Object object = part.getObject();
		if(object instanceof DataExplorerPart) {
			DataExplorerPart dataExplorerPart = (DataExplorerPart)object;
			dataExplorerPart.getDataExplorerUI().expandLastDirectoryPath();
		}
	}
}
