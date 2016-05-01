package cnu.lecture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by tchi on 2016. 4. 25..
 */
public class InGameSummonerQuerier {
    private final String apiKey;
    private final GameParticipantListener listener;

    public InGameSummonerQuerier(String apiKey, GameParticipantListener listener) {
        this.apiKey = apiKey;
        this.listener = listener;
    }

    public String queryGameKey(String summonerName) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();

        HashMap<String, SummonerInfo> entries = (HashMap)requestLogic(client, summonerName, false);
        String summonerId = getId(entries, summonerName);
                  
        InGameInfo gameInfo = (InGameInfo) requestLogic(client, summonerId, true);
        
        return getEncryptionKey(gameInfo);
    }

    public Object requestLogic(HttpClient client, String nameOrId, Boolean isInGameInfo) throws IOException {
        if(isInGameInfo) {
        	HttpUriRequest inGameRequest = buildObserverHttpRequest(nameOrId);
            HttpResponse inGameResponse = client.execute(inGameRequest);
        	return new Gson().fromJson(new JsonReader(new InputStreamReader(inGameResponse.getEntity().getContent())), InGameInfo.class);
        }
        else {
        	HttpUriRequest summonerRequest = buildApiHttpRequest(nameOrId);
            HttpResponse summonerResponse = client.execute(summonerRequest);
        	return new Gson().fromJson(new JsonReader(new InputStreamReader(summonerResponse.getEntity().getContent())), new TypeToken<HashMap<String, SummonerInfo>>(){}.getType());
        }
    }
    
    public String getId(HashMap<String, SummonerInfo> entries, String summonerName) {
    	return entries.get(summonerName).getId();
    }

    public String getEncryptionKey(InGameInfo gameInfo) {
    	Arrays.asList(gameInfo.getParticipants()).forEach((InGameInfo.Participant participant) -> {
            listener.player(participant.getSummonerName());
        });
    	return gameInfo.getObservers().getEncryptionKey();
    }
    
    protected HttpUriRequest buildApiHttpRequest(String summonerName) throws UnsupportedEncodingException {
        String url = mergeWithApiKey(new StringBuilder()
                .append("https://kr.api.pvp.net/api/lol/kr/v1.4/summoner/by-name/")
                .append(URLEncoder.encode(summonerName, "UTF-8")))
                .toString();
        return new HttpGet(url);
    }

    protected HttpUriRequest buildObserverHttpRequest(String id) {
        String url = mergeWithApiKey(new StringBuilder()
                .append("https://kr.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/KR/")
                .append(id))
                .toString();
        return new HttpGet(url);
    }

    private StringBuilder mergeWithApiKey(StringBuilder builder) {
        return builder.append("?api_key=").append(apiKey);
    }
}
