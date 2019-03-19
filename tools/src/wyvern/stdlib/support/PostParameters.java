package wyvern.stdlib.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostParameters {
    public static final PostParameters postparam = new PostParameters();
    private static List<String> parameterNames = new ArrayList<>();
    private static List<String> parameterValues = new ArrayList<>();

    public String getPayload(InputStream is) throws IOException {
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isReader);
        StringBuilder payload = null;

        // read header of payload
        String header = "";
        String temp = br.readLine();
        while (temp != null && temp.length() > 0) {
            header += temp;
            temp = br.readLine();
        }
        System.out.println(header);
        if (header.startsWith("POST")) {
            payload = new StringBuilder();
            while (br.ready()) {
                payload.append(br.readLine());
            }
        }
        String result = payload.toString();
        return processPostBody(result);
    }

    public String processPostBody(String request) {
        List<String> paramNames = new ArrayList<>();
        List<String> paramValues = new ArrayList<>();
        Pattern p = Pattern.compile("name=\"(.+?)\"");
        Matcher m = p.matcher(request);

        while (m.find()) {
            paramNames.add(m.group(1));
        }

        p = Pattern.compile("\"(.+?)------");
        m = p.matcher(request);

        while (m.find()) {
            String unformatted = m.group(1);
            int index = unformatted.indexOf("\"");
            String val = unformatted.substring(index + 1);
            paramValues.add(val);
        }

        String paramResult = "";
        if (paramValues.size() == paramNames.size()) {
            for (int i = 0; i < paramValues.size(); i++) {
                String key = paramNames.get(i);
                String value = paramValues.get(i);
                parameterNames.add(key);
                parameterValues.add(value);
                if (i == paramValues.size() - 1) {
                    paramResult += value;
                } else {
                    paramResult += value + ",";
                }
            }
        }
        return paramResult;
    }

    public String getParamByIndex(String params, int index) {
        return Arrays.asList(params.split(",")).get(index);
    }

    public String getParamNameByIndex(int index) {
        return parameterNames.get(index);
    }

    public String getParamMappingToString() {
        String result = "";
        for (int i = 0; i < parameterNames.size(); i++) {
            result += parameterNames.get(i) + " : " + parameterValues.get(i) + "\n";
        }
        return result;
    }
}