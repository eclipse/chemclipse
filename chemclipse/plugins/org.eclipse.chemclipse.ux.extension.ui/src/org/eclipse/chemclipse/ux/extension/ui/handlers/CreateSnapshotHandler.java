/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.handlers;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

public class CreateSnapshotHandler {

	@Execute
	public void execute(Composite composite) {

		copyCompositeToClipboard(composite);
	}

	private void copyCompositeToClipboard(Composite composite) {

		Image image = getImage(composite);
		if(image == null) {
			openMessageBox("The focus of the selected view/editor couldn't be retrieved.");
		} else {
			/*
			 * There is a bug on Linux x86 and x86_64.
			 */
			if(OperatingSystemUtils.isLinux()) {
				/*
				 * Save the image to a file.
				 */
				FileDialog fileDialog = new FileDialog(DisplayUtils.getShell(), SWT.SAVE);
				fileDialog.setText("Save Clipboard To File");
				fileDialog.setFileName("Clipboard.png");
				fileDialog.setFilterExtensions(new String[]{"*.png"});
				fileDialog.setOverwrite(true);
				fileDialog.setFilterNames(new String[]{" PNG (*.png)"});
				String file = fileDialog.open();
				if(file != null && !file.equals("")) {
					ImageLoader imageLoader = new ImageLoader();
					imageLoader.data = new ImageData[]{image.getImageData()};
					imageLoader.save(file, SWT.IMAGE_PNG);
				}
			} else {
				/*
				 * Copy the image to the clipboard
				 */
				ImageTransfer imageTransfer = ImageTransfer.getInstance();
				Object[] data = new Object[]{image.getImageData()};
				Transfer[] dataTypes = new Transfer[]{imageTransfer};
				Clipboard clipboard = new Clipboard(DisplayUtils.getDisplay());
				clipboard.setContents(data, dataTypes);
				clipboard.dispose();
				openMessageBox("The selected view/editor has been copied to clipboard.");
			}
		}
	}

	private Image getImage(Composite composite) {

		Image image = null;
		//
		if(composite != null && composite.getParent() != null) {
			Composite compositeParent = composite.getParent();
			GC gc = null;
			try {
				gc = new GC(compositeParent);
				image = new Image(DisplayUtils.getDisplay(), compositeParent.getBounds());
				gc.copyArea(image, 0, 0);
			} finally {
				gc.dispose();
			}
		}
		return image;
	}

	private void openMessageBox(String message) {

		String text = "Copy Selection To Clipboard";
		MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.NONE);
		messageBox.setText(text);
		messageBox.setMessage(message);
		messageBox.open();
	}
}
