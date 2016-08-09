import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

// reference: http://www.studytrails.com/java/json/java-org-json.jsp

class Parser {
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException {
        try {
            JSONObject jsonObj = new JSONObject(weatherJsonStr);
            JSONArray list = (JSONArray) jsonObj.get("list");
            JSONObject item = (JSONObject) list.get(dayIndex);
            JSONObject temp = (JSONObject) item.get("temp");

            return (double) temp.get("max");
        } catch (Exception e) {
            throw new JSONException(e);
        }
    }


    public static void main(String[] args) {
        final int dayIndex = 2;
        final String weatherJsonStr = "{\"city\":{\"id\":5375480,\"name\":\"Mountain View\",\"coord\":{\"lon\":-122.083847,\"lat\":37.386051},\"country\":\"US\",\"population\":0},\"cod\":\"200\",\"message\":0.0207,\"cnt\":7,\"list\":[{\"dt\":1470513600,\"temp\":{\"day\":287.82,\"min\":279.67,\"max\":287.82,\"night\":279.67,\"eve\":287.82,\"morn\":287.82},\"pressure\":986.86,\"humidity\":77,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"speed\":0.71,\"deg\":213,\"clouds\":0},{\"dt\":1470600000,\"temp\":{\"day\":292.66,\"min\":280.62,\"max\":294.01,\"night\":280.62,\"eve\":290.07,\"morn\":282.37},\"pressure\":988.43,\"humidity\":74,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.41,\"deg\":238,\"clouds\":0},{\"dt\":1470686400,\"temp\":{\"day\":295.46,\"min\":282.2,\"max\":296.25,\"night\":282.2,\"eve\":291.39,\"morn\":283.44},\"pressure\":987.7,\"humidity\":71,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.37,\"deg\":246,\"clouds\":0},{\"dt\":1470772800,\"temp\":{\"day\":295.15,\"min\":282.05,\"max\":295.6,\"night\":282.05,\"eve\":290.93,\"morn\":284.33},\"pressure\":987.01,\"humidity\":71,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.47,\"deg\":217,\"clouds\":0},{\"dt\":1470859200,\"temp\":{\"day\":292.18,\"min\":284.66,\"max\":294.98,\"night\":288.01,\"eve\":294.98,\"morn\":284.66},\"pressure\":1007.15,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":1.92,\"deg\":299,\"clouds\":0},{\"dt\":1470945600,\"temp\":{\"day\":290.65,\"min\":284.82,\"max\":293.26,\"night\":286.53,\"eve\":293.26,\"morn\":284.82},\"pressure\":1009.09,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.82,\"deg\":289,\"clouds\":30},{\"dt\":1471032000,\"temp\":{\"day\":290.9,\"min\":284.28,\"max\":293.74,\"night\":286.9,\"eve\":293.74,\"morn\":284.28},\"pressure\":1010.71,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.81,\"deg\":177,\"clouds\":8}]}";

        double max = getMaxTemperatureForDay(weatherJsonStr, dayIndex);
        System.out.println("Max: " + max);
    }
}
