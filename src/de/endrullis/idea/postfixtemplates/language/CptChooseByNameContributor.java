package de.endrullis.idea.postfixtemplates.language;

import com.intellij.navigation.*;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CptChooseByNameContributor implements ChooseByNameContributor {
	@NotNull
	@Override
	public String[] getNames(Project project, boolean includeNonProjectItems) {
		List<CptMapping> mappings = CptUtil.findMappings(project);
		List<String> names = new ArrayList<String>(mappings.size());
		for (CptMapping mapping : mappings) {
			if (mapping.getClassName() != null && mapping.getClassName().length() > 0) {
				names.add(mapping.getClassName());
			}
		}
		return names.toArray(new String[names.size()]);
	}

	@NotNull
	@Override
	public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
		// todo include non project items
		List<CptMapping> properties = CptUtil.findMappings(project, name);
		return properties.toArray(new NavigationItem[properties.size()]);
	}
}