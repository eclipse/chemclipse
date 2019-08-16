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
 * CHristoph Läubrich - extend API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.editors;

import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;

public interface IScanEditorNMR extends IChemClipseEditor {

	IDataNMRSelection getScanSelection();

	String getName();
}
