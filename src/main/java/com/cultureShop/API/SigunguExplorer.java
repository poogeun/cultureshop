package com.cultureShop.API;

import com.cultureShop.dto.ApiDto.SigunguApiDto;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class SigunguExplorer {

    public List<SigunguApiDto> sigunguApiDatas;

    BufferedReader rd;
    StringBuilder sb = new StringBuilder();

    public SigunguExplorer() throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("https://api.vworld.kr/req/data?service=data&request=GetFeature&data=LT_C_ADSIGG_INFO&geomFilter=BOX(124,33,132,43)&columns=full_nm&size=1000&key=FF3C525F-23A2-31CD-8950-4EB55D2AF697&domain=localhost");

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "json");
        System.out.println("Response code: " + conn.getResponseCode());

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        sigunguApiDatas = new ArrayList<>();
        parseSigunguData();
    }

    public List<SigunguApiDto> parseSigunguData() throws ParseException {
        String results = sb.toString();

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(results);
        JSONObject response = (JSONObject) object.get("response");
        JSONObject result = (JSONObject) response.get("result");
        JSONObject featureCollection = (JSONObject) result.get("featureCollection");
        JSONArray features = (JSONArray) featureCollection.get("features");

        for(Object o : features) {
            JSONObject feature = (JSONObject) o;
            JSONObject properties = (JSONObject) feature.get("properties");
            String simAddr = (String) properties.get("full_nm");

            sigunguApiDatas.add(
                    SigunguApiDto.builder()
                            .simAddr(simAddr)
                            .build()
            );
        }
        return sigunguApiDatas;
    }
}
