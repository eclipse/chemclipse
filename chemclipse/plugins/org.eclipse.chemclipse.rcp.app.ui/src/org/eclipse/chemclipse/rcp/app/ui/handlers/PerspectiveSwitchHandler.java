/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.rcp.app.ui.dialogs.PerspectiveChooserDialog;
import org.eclipse.chemclipse.rcp.app.ui.dialogs.PerspectiveSwitcherDialog;
import org.eclipse.chemclipse.rcp.app.ui.internal.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.rcp.app.ui.switcher.PerspectiveSwitcher;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PerspectiveSwitchHandler {

	@Inject
	private static MApplication application;
	private static PerspectiveSwitcher perspectiveSwitcher;

	@Execute
	public void execute(MWindow window) {

		PerspectiveSwitcherDialog perspectiveSwitcherDialog = ContextInjectionFactory.make(PerspectiveSwitcherDialog.class, window.getContext());
		perspectiveSwitcherDialog.open();
	}

	public static void focusPerspectiveAndView(String perspectiveId, String viewId) {

		List<String> viewIds = new ArrayList<String>();
		viewIds.add(viewId);
		focusPerspectiveAndView(perspectiveId, viewIds);
	}

	public static void focusViews(List<String> viewIds) {

		focusPerspectiveAndView(null, viewIds);
	}

	public static void focusPerspectiveAndView(String perspectiveId, List<String> viewIds) {

		/*
		 * Try to change the perspective and activate the requested view.
		 */
		boolean changePerspectiveAutomatically = PreferenceSupplier.getChangePerspectiveAutomatically();
		if(!changePerspectiveAutomatically) {
			if(showPerspectiveDialog() != Window.OK) {
				return;
			}
		}
		if(changePerspectiveAutomatically) {
			/*
			 * Create the switcher if null.
			 */
			if(perspectiveSwitcher == null) {
				/*
				 * The application should definitively exists.
				 * But this is checked to avoid a NPE.
				 */
				if(application != null) {
					MWindow window = application.getChildren().get(0);
					if(window != null) {
						IEclipseContext context = window.getContext();
						perspectiveSwitcher = ContextInjectionFactory.make(PerspectiveSwitcher.class, context);
					}
				}
			}
			/*
			 * Change perspective and view.
			 */
			if(perspectiveId != null) {
				perspectiveSwitcher.changePerspective(perspectiveId);
			}
			//
			for(String viewId : viewIds) {
				perspectiveSwitcher.focusView(viewId);
			}
		}
	}

	public static boolean isActivePerspective(String perspectiveId) {

		String activePerspective = Activator.getDefault().getActivePerspective();
		return activePerspective.equals(perspectiveId);
	}

	/*
	 * Show a dialog if requested. The boolean values: P_SHOW_PERSPECTIVE_DIALOG
	 * P_CHANGE_PERSPECTIVE_AUTOMATICALLY can be edited by the user.
	 */
	private static int showPerspectiveDialog() {

		Shell shell = Display.getCurrent().getActiveShell();
		String title = "Change Perspective";
		String message = "The program changes the perspectives and views automatically on certain tasks. You can select whether you would like to change perspectives automatically. If not, you are responsible by your own to select the needed perspective and views.";
		PerspectiveChooserDialog dialog = new PerspectiveChooserDialog(shell, title, message);
		return dialog.open();
	}
}