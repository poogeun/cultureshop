package com.cultureShop.API;

import com.cultureShop.dto.ApiDto.MusArtApiDto;
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
public class MusArtExplorer {
/*
    public List<MusArtApiDto> musArtApiDatas;

    BufferedReader rd;
    StringBuilder sb = new StringBuilder();

    public MusArtExplorer() throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("https://api.vworld.kr/req/data?service=data&request=GetFeature&data=LT_P_DGMUSEUMART&geomFilter=BOX(126.5,37.5,127.5,37.6)&size=100&page=2&key=FF3C525F-23A2-31CD-8950-4EB55D2AF697&domain=localhost");

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

        musArtApiDatas = new ArrayList<>();
        parseMusArtData();
    }

    public List<MusArtApiDto> parseMusArtData() throws ParseException {
        String results = sb.toString();

        // json 파싱
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(results);
        JSONObject response = (JSONObject) object.get("response");
        JSONObject result = (JSONObject) response.get("result");
        JSONObject featureCollection = (JSONObject) result.get("featureCollection");
        JSONArray features = (JSONArray) featureCollection.get("features");

        for (Object o : features) {
            JSONObject feature = (JSONObject) o;
            JSONObject geometry = (JSONObject) feature.get("geometry");
            JSONArray coordinates = (JSONArray) geometry.get("coordinates");
            double geoX = (double) coordinates.get(1);
            double geoY = (double) coordinates.get(0);

            JSONObject properties = (JSONObject) feature.get("properties");
            String name = (String) properties.get("mus_nam");
            String address = (String) properties.get("new_adr");
            String tel = (String) properties.get("opr_tel");
            String openTime = (String) properties.get("wds_tme");
            String closeTime = (String) properties.get("wde_tme");


            musArtApiDatas.add(
                    MusArtApiDto.builder()
                            .name(name)
                            .address(address)
                            .tel(tel)
                            .openTime(openTime)
                            .closeTime(closeTime)
                            .geoX(geoX)
                            .geoY(geoY)
                            .build()
            );
        }
        System.out.println(musArtApiDatas.size());

        return musArtApiDatas;

    }

 */

}
