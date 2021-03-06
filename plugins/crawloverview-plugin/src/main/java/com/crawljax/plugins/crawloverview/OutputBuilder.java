package com.crawljax.plugins.crawloverview;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.core.plugin.Plugin;
import com.crawljax.plugins.crawloverview.model.OutPutModel;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

class OutputBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(OutputBuilder.class);

	static final String SCREENSHOT_FOLDER_NAME = "screenshots";
	static final String STATES_FOLDER_NAME = "states";
	static final String JSON_OUTPUT_NAME = "result.json";
	static final String DOMS_OUTPUT_NAME = "doms";

	private final File outputDir;
	private final File states;
	private final File screenshots;
	private final File indexFile;
	private final File doms;
	private final VelocityEngine ve;

	/**
	 * @param outputDir
	 *            target for the output directory. Folder must not exist or be empty.
	 */
	OutputBuilder(File outputDir) {
		this.outputDir = outputDir;
		checkPermissions();
		copySkeleton();

		states = new File(outputDir, STATES_FOLDER_NAME);
		states.mkdir();
		screenshots = new File(outputDir, SCREENSHOT_FOLDER_NAME);
		screenshots.mkdir();
		doms = new File(outputDir, DOMS_OUTPUT_NAME);
		doms.mkdir();

		indexFile = new File(outputDir, "index.html");
		ve = new VelocityEngine();
		configureVelocity();

		copyGitProperties();
	}

	private void configureVelocity() {
		ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
		        "org.apache.velocity.runtime.log.NullLogChute");
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	}

	private void checkPermissions() {
		if (outputDir.exists()) {
			checkArgument(outputDir.isDirectory(), outputDir + " is not a directory");
			checkArgument(outputDir.list().length == 0, "Directory must be empty");
			checkArgument(outputDir.canWrite(), "Output dir not writable");
		} else {
			outputDir.mkdir();
		}
	}

	private void copySkeleton() {
		URL skeleton = OutputBuilder.class.getResource("/skeleton");
		if (skeleton.toExternalForm().contains("jar!")) {
			copySkeletonFromJar(skeleton);
		} else {
			LOG.debug("Loading skeleton as file");
			try {
				FileUtils.copyDirectory(new File(skeleton.toURI()), outputDir);
			} catch (IOException | URISyntaxException e) {
				throw new RuntimeException(
				        "Could not copy required resources: " + e.getMessage(), e);
			}
		}

	}

	private void copySkeletonFromJar(URL skeleton) {
		LOG.debug("Loading skeleton as JAR entry {}", skeleton);
		File jar = getJar(skeleton);
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jar))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().startsWith("skeleton") && !entry.isDirectory()) {
					String filename = entry.getName().substring("skeleton/".length());
					File newFile = new File(outputDir, filename);
					new File(newFile.getParent()).mkdirs();
					FileOutputStream out = new FileOutputStream(newFile);
					ByteStreams.copy(zis, out);
					out.close();
				}
			}
		} catch (IOException e1) {
			throw new RuntimeException("Could not copy required resources: " + e1.getMessage(),
			        e1);
		}
	}

	private File getJar(URL skeleton) {
		String path;
		try {
			path = URLDecoder.decode(skeleton.getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Could not process the path of the Overview skeleton "
			        + skeleton, e);
		}
		String jarpath = path.substring("file:".length(), path.indexOf("jar!") + "jar".length());
		File jar = new File(jarpath);
		LOG.debug("Jar file {} from path {}", jar, path);
		return jar;
	}

	File newScreenShotFile(String name) {
		return new File(screenshots, name + ".png");
	}

	private void copyGitProperties() {
		File outFile = new File(outputDir, "git.properties");
		try (InputStream in = OutputBuilder.class.getResourceAsStream("/git.properties");
		        FileOutputStream out = new FileOutputStream(outFile)) {
			ByteStreams.copy(in, out);
		} catch (IOException e) {
			LOG.warn("Could not copy git.properties file", e);
		}
	}

	void makeThumbNail(File screenShot, String name) {
		try {
			// scale image on disk
			BufferedImage originalImage = ImageIO.read(screenShot);
			BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);

			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, 200, 200, Color.WHITE, null);
			g.dispose();
			ImageIO.write(resizedImage, "jpg", new File(screenshots, name + "_small.jpg"));
		} catch (IOException e) {
			throw new CrawlOverviewException("Could not create thumbnail");
		}
	}

	void write(OutPutModel outModel) {
		try {
			writeIndexFile(outModel);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.info("Overview report generated");
	}

	private void writeIndexFile(OutPutModel model) {
		LOG.debug("Writing index file");
		VelocityContext context = new VelocityContext();
		writeJsonToOutDir(toJson(model));
		context.put("states", toJson(model.getStates()));
		context.put("edges", toJson(model.getEdges()));
		context.put("config", BeanToReadableMap.toMap(model.getConfiguration()));

		context.put("stats", model.getStatistics());

		LOG.debug("Writing urls report");
		context.put("urls", model.getStatistics().getStateStats().getUrls());

		writeFile(context, indexFile, "index.html");
	}

	private void writeJsonToOutDir(String outModelJson) {
		try {
			Files.write(outModelJson, new File(this.outputDir, JSON_OUTPUT_NAME), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.warn("Could not write JSON model to output dir. " + e.getMessage());
		}
	}

	private void writeFile(VelocityContext context, File outFile, String template) {
		try {
			Template templatee = ve.getTemplate(template);
			FileWriter writer = new FileWriter(outFile);
			templatee.merge(context, writer);
			// ve.evaluate(context, writer, name, template);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new CrawlOverviewException("Could not write output state");
		}
	}

	void writeState(VelocityContext context, String stateName) {
		File file = new File(states, stateName + ".html");
		writeFile(context, file, "state.html");
	}

	/**
	 * Save the dom to disk.
	 * 
	 * @param name
	 *            statename
	 * @param dom
	 *            the DOM as string
	 */
	void persistDom(String name, @Nullable String dom) {
		try {
			Files.write(Strings.nullToEmpty(dom), new File(doms, name + ".html"), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.warn("Could not save dom state for {}", name);
			LOG.debug("Could not save dom state", e);
		}
	}

	String getDom(String name) {
		try {
			return Files.toString(new File(doms, name + ".html"), Charsets.UTF_8);
		} catch (IOException e) {
			return "Could not load DOM: " + e.getLocalizedMessage();
		}
	}

	static String toJson(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			SimpleModule testModule = new SimpleModule("Plugin serialiezr");
			testModule.addSerializer(new JsonSerializer<Plugin>() {

				@Override
				public void serialize(Plugin plugin, JsonGenerator jgen,
				        SerializerProvider provider) throws IOException, JsonProcessingException {
					jgen.writeString(plugin.getClass().getSimpleName());
				}

				@Override
				public Class<Plugin> handledType() {
					return Plugin.class;
				}
			});
			mapper.registerModule(testModule);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			LOG.error(
			        "Could not serialize the object. This will be ignored and the error will be written instead. Object was {}",
			        o, e);
			return "\"" + e.getMessage() + "\"";
		}
	}

}
