package de.endrullis.idea.postfixtemplates.settings;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public enum CptFileType {

	LocalInPluginDir, LocalInFs, Web;

	@Override
	public String toString() {
		return switch (this) {
			case LocalInPluginDir -> "User template file (local file in plugin directory)";
			case LocalInFs -> "Local file in filesystem";
			case Web -> "Web template file (URL)";
		};

	}
	
}
