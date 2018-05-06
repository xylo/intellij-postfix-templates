package de.endrullis.idea.postfixtemplates.settings;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public enum CptFileType {

	LocalInPluginDir, LocalInFs, Web;

	@Override
	public String toString() {
		switch (this) {
			case LocalInPluginDir:
				return "Local file in plugin directory";
			case LocalInFs:
				return "Local file in filesystem";
			case Web:
				return "Web URL";
		}

		return null;
	}
	
}
