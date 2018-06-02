package de.endrullis.idea.postfixtemplates.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

/**
 * Small utility to load {@link WebTemplateFile WebTemplateFiles} from the web.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class WebTemplateFileLoader {

	public static WebTemplateFile[] load(File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(file, WebTemplateFile[].class);
	}

}
