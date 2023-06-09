package utils;

import entity.Release;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static utils.JSONUtils.readJsonFromUrl;

public class ManageRelease {
    public static List<Release> retrieveReleases(String projName) throws JSONException, IOException, ParseException {

        //The HashMap is a support to instantiate Release objects
        Map<Date, String> unsortedReleasesMap = new HashMap<>();    //Date = release date; String = release name (e.g. 1.4.2)
        List<Release> releasesList = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String url = "https://issues.apache.org/jira/rest/api/latest/project/" + projName + "/version";
        JSONObject json = readJsonFromUrl(url);
        JSONArray releases = json.getJSONArray("values");
        int total = json.getInt("total");

        for(int i=0; i<total; i++) {
            if(releases.getJSONObject(i).get("released").toString().equals("true")) {
                try {
                    String releaseDateString = releases.getJSONObject(i).get("releaseDate").toString();
                    Date releaseDate = formatter.parse(releaseDateString);
                    String releaseName = releases.getJSONObject(i).get("name").toString();

                    unsortedReleasesMap.put(releaseDate, releaseName);

                } catch(JSONException e) {
                    //There is no release date: skip this release and go on

                }

            }

        }
        Map<Date, String> releasesMap = new TreeMap<>(unsortedReleasesMap);        //TreeMap sorts unsortedReleasesMap by date
        //order the map by date


        int i=1;
        for(Map.Entry<Date, String> entry : releasesMap.entrySet()) {    //Iteration over releasesMap
            releasesList.add(new Release(i, entry.getValue(), entry.getKey())); //Add a new Release object to releasesList
            i++;
        }

        return releasesList; //Return the list of releases

    }


    public int getReleaseIndexByName(List<Release> releases, String name) {
        int index = -1;
        for(Release release : releases) {
            if(release.getReleaseName().equals(name)) {
                index = release.getId();
            }
        }
        return index;

    }

    public  int getReleaseIndexByDate(List<Release> releases, Date date) {
        int index = -1;
        for (Release release : releases) {
            //assegnare un id della lista alla release alla data più vicina a date
            if (release.getDate().after(date) || release.getDate().equals(date)) {
                return release.getId();
            }
        }
        return index;
    }

    public static List<Release> getHalfRelease(List<Release> releases) {
        List<Release> halfReleases = new ArrayList<>();
        int half = releases.size() / 2;
        for (int i = 0; i < half; i++) {
            halfReleases.add(releases.get(i));
        }
        return halfReleases;
    }
}
