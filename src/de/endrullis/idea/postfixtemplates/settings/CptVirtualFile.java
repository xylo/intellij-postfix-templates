package de.endrullis.idea.postfixtemplates.settings;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptVirtualFile {

	@Getter
	@Setter
	@Nullable
	private URL    url;
	@Getter
	@Setter
	private File   file;

	public CptVirtualFile(@Nullable URL url, @NotNull File file) {
		this.url = url;
		this.file = file;
	}

	public String getName() {
		return file.getName();
	}

	public boolean isLocal() {
		return url != null && url.getProtocol().equals("file");
	}

	public boolean isEditable() {
		return isLocal();
	}

}
