package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.TextTransferUtil;

/**
 * Xlsx or Xls to Csv tool
 * 
 * @author zt
 *
 */
public class XlsxOrXls2Csv implements CliRunner {

	private static final String SRC_DIR = "sd";

	public static void main(String[] args) {
		AdvCli.initRunner(args, "XlsxOrXls2Csv", new XlsxOrXls2Csv());
	}

	public static void printHelp() {
		System.out.println("Run help :XlsxOrXls2Csv xls|xlsx srcPath");
	}

	@Override
	public Options initOptions() {
		Options options = new Options();
		options.addOption(SRC_DIR, true, "src dir can't be null");
		return options;
	}

	@Override
	public boolean validateOptions(CommandLine commandCli) {
		return commandCli.hasOption(SRC_DIR);
	}

	@Override
	public void start(CommandLine commandCli) {
		String src = FileUtil.trim(commandCli.getOptionValue(SRC_DIR));

		List<File> files = FileUtil.getSourceFiles(src, ".xlsx");
		for (File file : files) {
			TextTransferUtil.xlsx2Csv(file, new File(file.getAbsolutePath() + ".csv"));
		}

		files = FileUtil.getSourceFiles(src, ".xls");
		for (File file : files) {
			TextTransferUtil.xls2Csv(file, new File(file.getAbsolutePath() + ".csv"));
		}
	}
}