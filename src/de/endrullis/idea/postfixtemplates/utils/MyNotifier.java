package de.endrullis.idea.postfixtemplates.utils;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

/**
 * Notification utilities.
 *
 * @author Strfan Endrullis
 */
@UtilityClass
public class MyNotifier {

	public static NotificationGroup getNotificationGroup() {
		return NotificationGroupManager.getInstance().getNotificationGroup("Custom Postfix Templates");
	}

	public static void notifyError(@Nullable Project project,
	                               String content) {
		getNotificationGroup()
			.createNotification(content, NotificationType.ERROR)
			.notify(project);
	}

}
