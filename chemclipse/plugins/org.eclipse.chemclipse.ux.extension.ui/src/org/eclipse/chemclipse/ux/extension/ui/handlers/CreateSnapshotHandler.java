/*******************************************************************************
 * Copyright (c) 2011, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.handlers;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class CreateSnapshotHandler {

	@Execute
	public void execute(Composite composite) {

		copyCompositeToClipboard(composite);
	}

	private void copyCompositeToClipboard(Composite composite) {

		Image image = getImage(composite);
		if(image == null) {
			openMessageBox(composite.getShell(), "The focus of the selected view/editor couldn't be retrieved.");
		} else {
			/*
			 * There is a bug on Linux x86 and x86_64.
			 */
			if(OperatingSystemUtils.isLinux()) {
				/*
				 * Save the image to a file.
				 */
				FileDialog fileDialog = new FileDialog(composite.getShell(), SWT.SAVE);
				fileDialog.setText("Save Clipboard To File");
				fileDialog.setFileName("Clipboard.png");
				fileDialog.setFilterExtensions("*.png");
				fileDialog.setOverwrite(true);
				fileDialog.setFilterNames(" PNG (*.png)");
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
				Clipboard clipboard = new Clipboard(composite.getDisplay());
				clipboard.setContents(data, dataTypes);
				clipboard.dispose();
				openMessageBox(composite.getShell(), "The selected view/editor has been copied to clipboard.");
			}
			image.dispose();
		}
	}

	private Image getImage(Composite composite) {

		Image image = null;

		if(composite != null && composite.getParent() != null) {
			Composite compositeParent = composite.getParent();
			GC gc = null;
			try {
				gc = new GC(compositeParent);
				Rectangle bounds = compositeParent.getBounds();
				image = new Image(compositeParent.getDisplay(), bounds.width, bounds.height);
				compositeParent.print(gc);
				gc.copyArea(image, 0, 0);
			} finally {
				gc.dispose();
			}
		}
		return image;
	}

	private void openMessageBox(Shell shell, String message) {

		String text = "Copy Selection To Clipboard";
		MessageBox messageBox = new MessageBox(shell, SWT.NONE);
		messageBox.setText(text);
		messageBox.setMessage(message);
		messageBox.open();
	}
}
