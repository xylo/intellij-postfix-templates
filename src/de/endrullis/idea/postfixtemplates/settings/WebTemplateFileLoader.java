package de.endrullis.idea.postfixtemplates.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Small utility to load {@link WebTemplateFile WebTemplateFiles} from the web.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class WebTemplateFileLoader {

	public static WebTemplateFile[] load(URL url) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(new File("test/src/yaml/webTemplateFiles.yaml"), WebTemplateFile[].class);
	}

}
