package com.fullship.hBAF.util;

import com.uber.h3core.H3Core;
import com.uber.h3core.exceptions.LineUndefinedException;
import com.uber.h3core.util.GeoCoord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class H3 {

    public static double xMax, yMax, xMin, yMin;
    public static Set<Long> daejeonH3Index = new HashSet<>();

    public static void setH3Index() throws IOException, LineUndefinedException {

        H3Core h3 = H3Core.newInstance();

        GeoCoord[] daejeonGeo = {
                new GeoCoord(36.48679951624792, 127.40150573442256),
                new GeoCoord(36.486049226720844, 127.40237162713332),
                new GeoCoord(36.45493887822377, 127.40583116302797),
                new GeoCoord(36.454913086274914, 127.40629396019843),
                new GeoCoord(36.45662159419439, 127.43213116720865),
                new GeoCoord(36.45653731526793, 127.43378989155006),
                new GeoCoord(36.45509361223148, 127.46155957004515),
                new GeoCoord(36.455337148641355, 127.4617928230305),
                new GeoCoord(36.477063983032316, 127.47987139318172),
                new GeoCoord(36.47720999255097, 127.48061100717518),
                new GeoCoord(36.475797777779924, 127.48423168252953),
                new GeoCoord(36.4756241273186, 127.48427610887815),
                new GeoCoord(36.454788546508105, 127.4960880025384),
                new GeoCoord(36.45490693805522, 127.49646645707624),
                new GeoCoord(36.454045572323665, 127.5035961999129),
                new GeoCoord(36.45370091965119, 127.5037888450623),
                new GeoCoord(36.42524291590289, 127.49376705635758),
                new GeoCoord(36.42506434044689, 127.49391176970472),
                new GeoCoord(36.420317939994945, 127.53743516505264),
                new GeoCoord(36.42012291038361, 127.53778279146553),
                new GeoCoord(36.41927919632655, 127.54228073457993),
                new GeoCoord(36.41585756869269, 127.54185998818646),
                new GeoCoord(36.40845961594542, 127.5470727467962),
                new GeoCoord(36.408454519716564, 127.54707951164634),
                new GeoCoord(36.399675947569705, 127.55816517314781),
                new GeoCoord(36.399856150629894, 127.55878010175535),
                new GeoCoord(36.39914654004481, 127.55919218532583),
                new GeoCoord(36.39821888953578, 127.55967899075192),
                new GeoCoord(36.39518678315378, 127.55508024348465),
                new GeoCoord(36.39514713936509, 127.55494637061842),
                new GeoCoord(36.38380225513675, 127.52480467423321),
                new GeoCoord(36.35035287645941, 127.5193690338617),
                new GeoCoord(36.34006990064799, 127.50134180998748),
                new GeoCoord(36.23795395068193, 127.49257720191125),
                new GeoCoord(36.19672289194393, 127.44871641082584),
                new GeoCoord(36.212892945867274, 127.40799929928723),
                new GeoCoord(36.2622709500042, 127.39025797769695),
                new GeoCoord(36.262586342451286, 127.35950194982368),
                new GeoCoord(36.21890129271374, 127.36419718015068),
                new GeoCoord(36.203158372814976, 127.32394583489626),
                new GeoCoord(36.22081536998303, 127.31564105913714),
                new GeoCoord(36.23529359092193, 127.2831547927397),
                new GeoCoord(36.26495018611541, 127.28650666227946),
                new GeoCoord(36.264895312282356, 127.28624581898431),
                new GeoCoord(36.2760505815688, 127.25877186484053),
                new GeoCoord(36.32724953025797, 127.25975274393714),
                new GeoCoord(36.3448265782094, 127.27912600112028),
                new GeoCoord(36.414603964631645, 127.28212428234082),
                new GeoCoord(36.421915039124464, 127.29426961581308),
                new GeoCoord(36.42219502183513, 127.29425168764308),
                new GeoCoord(36.422208976417835, 127.3263167270464),
                new GeoCoord(36.450273038129, 127.35579857644919),
                new GeoCoord(36.499215976257155, 127.38008138312885),
                new GeoCoord(36.49953929874922, 127.38033654837668),
                new GeoCoord(36.499660645040414, 127.38096688868224),
                new GeoCoord(36.49992444824916, 127.38230967353526),
                new GeoCoord(36.500230690026456, 127.38385497104343),
                new GeoCoord(36.499444883432616, 127.38538373894964),
                new GeoCoord(36.498985556577466, 127.38626339999793),
                new GeoCoord(36.49195602619904, 127.39575988535204),
                new GeoCoord(36.491708392279, 127.39614094465608),
                new GeoCoord(36.48679951624792, 127.40150573442256)
        };

        xMax = 0;
        yMax = 0;
        xMin = 0;
        yMin = 0;
        List<Long> list = new ArrayList<>();
        if(daejeonGeo.length>0) {
            GeoCoord preCoord = daejeonGeo[0];
            xMax = daejeonGeo[0].lat;
            yMax = daejeonGeo[0].lng;
            xMin = daejeonGeo[0].lat;
            yMin = daejeonGeo[0].lng;

            for (int i = 1; i < daejeonGeo.length; i++) {
                GeoCoord curCoord = daejeonGeo[i];
                long preIndex = h3.geoToH3(preCoord.lat, preCoord.lng, 8);
                long curIndex = h3.geoToH3(curCoord.lat, curCoord.lng, 8);

                xMax = Math.max(xMax,curCoord.lat);
                yMax = Math.max(yMax,curCoord.lng);
                xMin = Math.min(xMin,curCoord.lat);
                yMin = Math.min(yMin,curCoord.lng);

                list = h3.h3Line(preIndex,curIndex);

                for(int j = 0; j<list.size(); j++) {
                    daejeonH3Index.add(list.get(j));
                }

                list.clear();

                preCoord = curCoord;
            }
        }

        System.out.println("대전 테두리 전체 개수 : "+daejeonH3Index.size());

        System.out.println("민맥스 : "+xMax+" "+yMax+" "+xMin+" "+yMin);

        daejeonH3Index = bfs(daejeonH3Index, 36.321655,127.378953);

        System.out.println("대전 전체 h3 인덱스 개수 : "+daejeonH3Index.size());

        JSONArray jsonArray = new JSONArray();

        int max = 0;
        int min = 987654321;
        for(Long i : daejeonH3Index){
            int res = h3.h3GetResolution(i);
            max = Math.max(max,res);
            min = Math.min(min,res);

            List<GeoCoord> geoCoordList = h3.h3ToGeoBoundary(i);
            for(GeoCoord coord : geoCoordList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lat", coord.lat);
                jsonObject.put("lng", coord.lng);
                jsonArray.add(jsonObject);
            }

        }

        try (FileWriter file = new FileWriter("geo_coord.json")) {
            file.write(jsonArray.toString());
            System.out.println("JSON 객체를 파일에 저장했습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Long> daejeonCompact = h3.compact(daejeonH3Index);

        System.out.println("대전 compact 전체 h3 인덱스 개수 : "+daejeonCompact.size());

//        System.out.println("max : "+max);
//        System.out.println("min : "+min);

    }

    public static Set<Long> bfs(Set<Long> set, double Lat, double Lng) throws IOException {

        H3Core h3 = H3Core.newInstance();

        long startIndex = h3.geoToH3(Lat,Lng,8);
        set.add(startIndex);

        Queue<Long> que = new LinkedList<>();
        que.add(startIndex);

        List<Long> list = new ArrayList<>();
        while(!que.isEmpty()){
            long index = que.poll();

            list = h3.kRing(index,1);
            for(Long i : list){
                if(set.contains(i))
                    continue;
                set.add(i);

                que.add(i);
            }
            list.clear();
        }

        return set;

    }


}