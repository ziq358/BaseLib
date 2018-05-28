package com.ziq.base.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

/**
 * @author john.
 * @since 2018/5/28.
 * Des:
 */

public class ShellUtil {

    private static final String TAG = "ShellUtil";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    public static CommandResult execCommand(String command, boolean isRoot){
        return execCommand(new String[]{command}, isRoot);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        int result = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;

        try {
            process = runtime.exec(isRoot ? "su" : "sh");

            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();


            result = process.waitFor();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result,
                successMsg == null ? "" : successMsg.toString(),
                errorMsg == null ? "" : errorMsg.toString());
    }

    /**
     * 表示命令指定结果的封装类
     */
    public static class CommandResult {
        public int result;
        public String successMsg;
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}
