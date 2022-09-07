package de.endrullis.idea.postfixtemplates;

import com.intellij.diagnostic.AbstractMessage;
import com.intellij.diagnostic.IdeaReportingEvent;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.idea.IdeaLogger;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.Consumer;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Error reporter that uses Sentry.
 *
 * @author Stefan Endrullis (stefan@endrullis.de)
 * @author jansorg
 */
public class SentryErrorReporter extends ErrorReportSubmitter {

	@NotNull
	@Override
	public String getReportActionText() {
		return "Report to Author";
	}

	@Override
	public boolean submit(IdeaLoggingEvent @NotNull [] events,
	                      @Nullable String additionalInfo,
	                      @NotNull Component parentComponent,
	                      @NotNull Consumer<? super SubmittedReportInfo> consumer) {

		val context = DataManager.getInstance().getDataContext(parentComponent);
		val project = CommonDataKeys.PROJECT.getData(context);

		new Task.Backgroundable(project, "Sending Error Report") {
			@Override
			public void run(@NotNull ProgressIndicator indicator) {
				SentryEvent event = new SentryEvent();
				event.setLevel(SentryLevel.ERROR);

				if (getPluginDescriptor() instanceof IdeaPluginDescriptor) {
					event.setRelease(getPluginDescriptor().getVersion());
				}
				// set server name to empty to avoid tracking personal data
				event.setServerName("");

				// now, attach all exceptions to the message
				//List<SentryException> errors = new ArrayList<>(events.length);
				for (IdeaLoggingEvent ideaEvent : events) {
					// this is the tricky part
					// ideaEvent.throwable is a com.intellij.diagnostic.IdeaReportingEvent.TextBasedThrowable
					// This is a wrapper and is only providing the original stacktrace via 'printStackTrace(...)',
					// but not via 'getStackTrace()'.
					//
					// Sentry accesses Throwable.getStackTrace(),
					// So, we workaround this by retrieving the original exception from the data property
					if (ideaEvent instanceof IdeaReportingEvent && ideaEvent.getData() instanceof AbstractMessage) {
						Throwable ex = ((AbstractMessage) ideaEvent.getData()).getThrowable();

						event.setThrowable(ex);
						break;
					} else {
						// ignoring this ideaEvent, you might not want to do this
					}
				}
				//event.setExceptions(errors);
				// might be useful to debug the exception
				event.setExtra("last_action", IdeaLogger.ourLastActionId);

				// by default, Sentry is sending async in a background thread
				Sentry.captureEvent(event);

				ApplicationManager.getApplication().invokeLater(() -> {
					// we're a bit lazy here.
					// Alternatively, we could add a listener to the sentry client
					// to be notified if the message was successfully send
					Messages.showInfoMessage(parentComponent, "Thank you for submitting your report!", "Error Report");
					consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
				});
			}
		}.queue();

		return true;
	}

}
