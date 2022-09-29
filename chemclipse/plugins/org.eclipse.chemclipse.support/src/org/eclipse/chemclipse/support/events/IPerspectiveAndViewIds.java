/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.events;

/**
 * This interface contains the perspective and view
 * ids stored in the fragment.e4xmi file.
 * It neccessary to keep both ids in sync.
 */
public interface IPerspectiveAndViewIds {

	/*
	 * Is used to show 3.x editors too:
	 * "org.eclipse.e4.primaryDataStack"
	 * Is an own area to show only 4.x parts (editors)
	 * "org.eclipse.chemclipse.rcp.app.ui.partstack.editor"
	 * Definition in Application.e4xmi
	 * org.eclipse.chemclipse.rcp.app.ui
	 */
	String EDITOR_PART_STACK_ID = "org.eclipse.e4.primaryDataStack";
	String STACK_PERSPECTIVES = "org.eclipse.chemclipse.rcp.app.ui.perspectivestack.main";
	/*
	 * Perspectives
	 */
	String PERSPECTIVE_WELCOME = "org.eclipse.chemclipse.ux.extension.ui.perspective.welcome";
	/*
	 * Parts
	 */
	String VIEW_PROCESSING_INFO = "org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart";
	String VIEW_CONSOLE = "org.eclipse.ui.console.ConsoleView";
}
