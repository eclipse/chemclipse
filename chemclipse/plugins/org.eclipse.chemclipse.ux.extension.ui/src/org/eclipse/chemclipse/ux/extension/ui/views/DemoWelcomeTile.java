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
 * Christoph Läubrich - add support for loading the demo chromatogram directly
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.ISelectionHandler;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class DemoWelcomeTile implements ISelectionHandler {

	private WelcomeView welcomeView;
	private File chromatogramFile;

	public DemoWelcomeTile(WelcomeView welcomeView) {
		this.welcomeView = welcomeView;
	}

	@Override
	public void handleEvent() {

		welcomeView.switchPerspective(WelcomeView.PERSPECTIVE_DATA_ANALYSIS);
		Executors.defaultThreadFactory().newThread(new Runnable() {

			@Override
			public void run() {

				synchronized(DemoWelcomeTile.this) {
					/*
					 * Try to create the chromatogram.
					 */
					if(chromatogramFile == null) {
						try {
							File tempFile = File.createTempFile("DemoChromatogram", ".ocb");
							tempFile.deleteOnExit();
							copyChromatogram(tempFile);
							chromatogramFile = tempFile;
						} catch(Exception e) {
							e.printStackTrace();
							return;
						}
					}
					/*
					 * Open the chromatogram Editor.
					 */
					DisplayUtils.getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {

							try {
								if(chromatogramFile != null && chromatogramFile.exists()) {
									MPart part = welcomeView.createChromatogramPart();
									part.setLabel("DemoChromatogram.ocb");
									Map<String, Object> map = new HashMap<String, Object>();
									map.put(EditorSupport.MAP_FILE, chromatogramFile.getAbsolutePath());
									map.put(EditorSupport.MAP_BATCH, false);
									part.setObject(map);
									part.setTooltip("Demo Chromatogram (MSD)");
									welcomeView.showEditorPart(part);
								}
							} catch(Exception e) {
								e.printStackTrace();
								return;
							}
						}
					});
				}
			}
		}).start();
	}

	private void copyChromatogram(File file) throws IOException {

		try (InputStream stream = DemoWelcomeTile.class.getResourceAsStream("/files/DemoChromatogram.ocb")) {
			try (FileOutputStream fout = new FileOutputStream(file)) {
				byte[] buffer = new byte[1024];
				int read;
				while((read = stream.read(buffer)) > -1) {
					fout.write(buffer, 0, read);
				}
				fout.flush();
			}
		}
	}
}
