package wyvern.stdlib.support;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PostParameters {
    public static final PostParameters postparam = new PostParameters();

    public Map<String,String> getPayload(InputStream is) throws IOException {
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isReader);
        
        StringBuilder payload = null;

        // read header of payload
        String temp = null, header = "";
        while((temp = br.readLine()).length() != 0){
            header += temp;
        }
		System.out.println(header);
        if(header.startsWith("POST")){
            payload = new StringBuilder();
            while(br.ready()){
                payload.append(br.readLine());
            }
        }
		String result = payload.toString();
        return processPostBody(result);
	
    }
	
	public Map<String,String> processPostBody(String request){

		Map<String, String> params = new LinkedHashMap<String, String>();
		
		List<String> paramNames = new ArrayList<String>();
		List<String> paramValues = new ArrayList<String>();
		
		Pattern p = Pattern.compile("name=\"(.+?)\"");
		Matcher m = p.matcher(request);
		
		while(m.find()){
			paramNames.add(m.group(1));
		}
		
		p = Pattern.compile("\"(.+?)------");
		m = p.matcher(request);
		
		while(m.find()){
			String unformatted = m.group(1);
			int index = unformatted.indexOf("\"");
			String val = unformatted.substring(index+1);
			paramValues.add(val);
		}
		
		String last = paramValues.get(paramValues.size() - 1);
		paramValues.remove(last);
		last =  paramNames.get(paramNames.size() - 1);
		paramNames.remove(last);
		
		if(paramValues.size() == paramNames.size()){
			for(int i = 0; i < paramValues.size(); i++){
				String key = paramNames.get(i);
				String value = paramValues.get(i);
				params.put(key, value);
				
			}
		
		}

		return params;
	
	}
	

}