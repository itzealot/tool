package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * To join lines to one line
 * 
 * @author zealot
 *
 */
public class LineJoinCli implements CliRunner {

	private static final String SRC_DIR = "sd";
	private static final String DST_DIR = "dd";
	private static final String LINE_COUNTS = "lc";
	private static final String SPLITER = "sp";

	public static void main(String[] args) {
		AdvCli.initRunner(args, "LineJoinCli", new LineJoinCli());
	}

	@Override
	public Options initOptions() {
		Options options = new Options();

		options.addOption(SRC_DIR, true, "src dir can't be null");
		options.addOption(DST_DIR, true, "dst dir can't be null");
		options.addOption(LINE_COUNTS, true, "join line counts can't be null");
		options.addOption(SPLITER, true, "join with spliter char can't be null");

		return options;
	}

	@Override
	public boolean validateOptions(CommandLine commandCli) {
		return commandCli.hasOption(SRC_DIR) && commandCli.hasOption(DST_DIR) && commandCli.hasOption(LINE_COUNTS)
				&& commandCli.hasOption(SPLITER);
	}

	@Override
	public void start(CommandLine commandCli) {
		String src = FileUtil.trim(commandCli.getOptionValue(SRC_DIR));
		String dst = FileUtil.trim(commandCli.getOptionValue(DST_DIR));
		String lineCounts = FileUtil.trim(commandCli.getOptionValue(LINE_COUNTS));
		String spliter = FileUtil.trim(commandCli.getOptionValue(SPLITER));

		System.out.println("Start to join lines to one line, src dir: " + src);

		List<File> files = FileUtil.getSourceFiles(src);
		new File(dst).mkdirs();

		for (File file : files)
			try {
				if (!file.isDirectory()) {
					File dstFile = new File(dst + "/" + file.getName() + "_join" + FileUtil.suffix(file.getName()));
					FileUtil.read(file, dstFile, Integer.parseInt(lineCounts), getSpliter(spliter));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * '\u0009' <==> '\t'
	 * 
	 * @param src
	 * @return
	 */
	private char getSpliter(String src) {
		int len = src.length();
		if (len == 0) {
			return '|';
		}
		int index = src.indexOf('\\');
		return index != -1 ? '\t' : src.charAt(0);
	}
}
