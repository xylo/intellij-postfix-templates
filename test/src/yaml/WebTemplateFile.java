package yaml;

import lombok.Data;

/**
 * Web entry.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@Data
public class WebTemplateFile {

	String id;
	String url;
	String author;
	String email;
	String website;
	String description;

}
