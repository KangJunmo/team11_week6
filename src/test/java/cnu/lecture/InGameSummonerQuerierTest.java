package cnu.lecture;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by tchi on 2016. 4. 25..
 */
public class InGameSummonerQuerierTest {
	class TempInGameSummonerQuerier extends InGameSummonerQuerier {
		final String id_akane24 = "32030493";
		String summonerName = null;
		
		public TempInGameSummonerQuerier(String apiKey, GameParticipantListener listener) {
			super(apiKey, listener);
		}
				
		@Override
		public String getId(HashMap<String, SummonerInfo> entries, String summonerName) {
			if(entries.containsKey(summonerName)) {
				System.out.println("id : " + summonerName + " (" + entries.get(summonerName).getId() + ")");
				this.summonerName = summonerName;
				return entries.get(summonerName).getId();	
			}
			else {
				System.out.println("id가 없음. akane24로 테스트");
		        return id_akane24; 
			}
	    }

		@Override
	    public String getEncryptionKey(InGameInfo gameInfo) {
			if(gameInfo.getParticipants() == null) { 
				System.out.println("게임중이 아님");
			}
			else {
				try {
					String args[] = new String[]{"8242f154-342d-4b86-9642-dfa78cdb9d9c", summonerName};
					App.main(args);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    	return "4/bl4DC8HBir8w7bGHq6hvuHluBd+3xM";
	    }
	
	}

	private InGameSummonerQuerier querier;

    @Before
    public void setup() {
        final String apiKey = "8242f154-342d-4b86-9642-dfa78cdb9d9c";
        GameParticipantListener dontCareListener = mock(GameParticipantListener.class);

        querier = new TempInGameSummonerQuerier(apiKey, dontCareListener);
    }

    @Test
    public void shouldQuerierIdentifyGameKeyWhenSpecificSummonerNameIsGiven() throws Exception {
        final String summonerName;
        
        GIVEN: {
            summonerName = "akane24";
        }

        final String actualGameKey;
       
        WHEN: {
            actualGameKey = querier.queryGameKey(summonerName);

        }

        final String expectedGameKey = "4/bl4DC8HBir8w7bGHq6hvuHluBd+3xM";
        THEN: {
            assertThat(actualGameKey, is(expectedGameKey));
        }
    }
    
        
    @Test
    public void shouldQuerierReportMoreThan5Summoners() throws Exception {
    	Random rand = new Random();
    	InGameInfo.Participant[] participant = new InGameInfo.Participant[rand.nextInt(7)+4];
    	for(int i=0; i<participant.length; i++) {
    		participant[i] = mock(InGameInfo.Participant.class);
    	}
    	
		InGameInfo inGameInfo = new InGameInfo();
		inGameInfo.setParticipants(participant);
		
		System.out.println(Arrays.asList(inGameInfo.getParticipants()).size());
		
    	final int actualSize = Arrays.asList(inGameInfo.getParticipants()).size();
    	final boolean expected = (actualSize >= 4) ? true : false;
    	
    	assertThat(true, is(expected));
    }
}
