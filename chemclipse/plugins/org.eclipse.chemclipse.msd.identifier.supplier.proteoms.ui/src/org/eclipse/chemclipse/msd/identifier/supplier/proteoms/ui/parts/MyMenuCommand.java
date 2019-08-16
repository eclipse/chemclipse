package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.parts;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.core.di.annotations.Execute;

public class MyMenuCommand {

	private static final Logger log = Logger.getLogger(MyMenuCommand.class);

	@Execute
	public void run() {

		log.debug("Run command");
	}
}
