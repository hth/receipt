/**
 * 
 */
package com.tholix.utils;

import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.abbyy.ocrsdk.Client;
import com.abbyy.ocrsdk.ProcessingSettings;
import com.abbyy.ocrsdk.Task;

/**
 * @author hitender
 * @when Jan 5, 2013 11:03:36 AM
 * 
 */
public class ABBYYCloudService {

	private static Client restClient;
	private static ABBYYCloudService cs;

	public static ABBYYCloudService instance() {
		if (restClient == null) {
			restClient = new Client();
			restClient.applicationId = "JustRead";
			restClient.password = "ONdWIM+cpXf75ZT/155dd+DZ";
		}
		if (cs == null) {
			cs = new ABBYYCloudService();
		}
		return cs;
	}

	/**
	 * Parse command line and recognize one or more documents.
	 */
	public String performRecognition(byte[] fileContents) throws Exception {
		String language = "English";
		ProcessingSettings.OutputFormat outputFormat = ProcessingSettings.OutputFormat.xml;

		ProcessingSettings settings = new ProcessingSettings();
		settings.setLanguage(language);
		settings.setOutputFormat(outputFormat);

		System.out.println("Uploading file..");
		Task task = restClient.processImage(fileContents, settings);

		return waitAndDownloadResult(task);
	}

	/**
	 * Wait until task processing finishes and download result.
	 */
	private String waitAndDownloadResult(Task task) throws Exception {
		while (task.isTaskActive()) {
			Thread.sleep(2000);

			System.out.println("Waiting..");
			task = restClient.getTaskStatus(task.Id);
		}

		String result = null;
		if (task.Status == Task.TaskStatus.Completed) {
			System.out.println("Downloading..");
			result = cs.downloadResult(task);
			System.out.println("Ready");
		} else if (task.Status == Task.TaskStatus.NotEnoughCredits) {
			System.out.println("Not enough credits to process document. " + "Please add more pages to your application's account.");
		} else {
			System.out.println("Task failed");
		}
		return result;
	}

	private String downloadResult(Task task) throws Exception {
		if (task.Status != Task.TaskStatus.Completed) {
			throw new IllegalArgumentException("Invalid task status");
		}

		if (task.DownloadUrl == null) {
			throw new IllegalArgumentException("Cannot download result without url");
		}

		URL url = new URL(task.DownloadUrl);
		URLConnection connection = url.openConnection(); // do not use authenticated connection
		return IOUtils.toString(connection.getInputStream(), "UTF-8");
	}

}
