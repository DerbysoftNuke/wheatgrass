package com.derby.nuke.wheatgrass.rpc.log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * CommandExecutor
 *
 * @author Drizzt Yang
 */
public class CommandExecutor {
    public String execute(String command) throws RuntimeException {
        Runtime run = Runtime.getRuntime();

        InputStream inputStream = null;
        try {
            Process process;
            List<String> commands = Arrays.asList("sh", "-c", command);
            process = run.exec(commands.toArray(new String[0]));
            inputStream = process.getInputStream();
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("execute command " + command + " failed", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public List<String> execute2Lines(String command, String headPrefix) {
        return result2Lines(execute(command), headPrefix);
    }

    public List<String> result2Lines(String result, String headPrefix) {
        List<String> lines = new ArrayList<String>();
        if (StringUtils.isBlank(result)) {
            return lines;
        }

        boolean headReaded = false;
        if (headPrefix == null) {
            headReaded = true;
        }
        for (String line : result.split("\n")) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            line = line.trim();
            if (headReaded) {
                lines.add(line);
            } else if (line.startsWith(headPrefix)) {
                headReaded = true;
            }
        }

        return lines;
    }

    public List<List<String>> result2Cols(String result, String headPrefix, int size) {
        List<String> lines = result2Lines(result, headPrefix);
        List<List<String>> cols = new ArrayList<List<String>>();
        String prepend = null;
        for (String line : lines) {
            if (prepend != null) {
                line = prepend + line;
            }
            List<String> attributes = Arrays.asList(line.split("\\s+"));
            if (attributes.size() < size) {
                prepend = line;
                continue;
            } else {
                prepend = null;
            }
            cols.add(attributes);
        }
        return cols;
    }

    public List<String> resultLine2Values(String result, String headPrefix) {
        List<String> values = new ArrayList<String>();
        if (StringUtils.isBlank(result)) {
            return values;
        }

        for (String line : result.split("\n")) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            line = line.trim();
            if (line.startsWith(headPrefix)) {
                for (String value : line.replaceAll("[a-zA-Z%:()]", "").split(",")) {
                    values.add(value.trim());
                }
                break;
            }
        }

        return values;
    }

}
