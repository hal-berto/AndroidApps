package it.androidapp.secretsanta.extraction;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.*;

public class ExtractionManager {

    private Integer eventId;
    private Context context;

    public ExtractionManager(Context cw, Integer eventId){
        this.eventId = eventId;
        this.context = cw;
    }

    public void performExtraction() throws ExtractionFailedException, ExtractionMapCreationException{
        Map<Integer, Set<Integer>> extractionMap = buildExtractionMap();
        if(extractionMap == null){
            //Si verifica nel caso un evento non abbia partecipanti
            throw new ExtractionMapCreationException();
        }

        //Ripete il ciclo per un numero di volte pari al numero di partecipanti
        for(int i = 0; i < extractionMap.size(); i++){
            //Estrae il prossimo partecipante a cui associare un destinatario
            Integer participantIdToLink = getNextParticipant(extractionMap);
            if(participantIdToLink == null){
                continue;
            }

            //Estrae randomicamente uno tra i possibili destinatari
            Integer recipientIdToLink = drawRecipient(extractionMap.get(participantIdToLink));

            //Associa il destinatario estratto rimuovendo gli altri
            Set<Integer> cleanedRecipientSet = new HashSet<Integer>();
            cleanedRecipientSet.add(recipientIdToLink);
            extractionMap.put(participantIdToLink, cleanedRecipientSet);

            //Elimina il destinatario estratto dai seti di possibili destinatari degli altri partecipanti
            cleanExtractionMap(recipientIdToLink, extractionMap);

            //Verifica se l'estrazione può essere conclusa in anticipo (ogni partecipante ha già uno e un solo destinatario)
            if(checkExtractionComplete(extractionMap)){
                break;
            }
        }

        //Alla fine del ciclo controlla se l'estrazione è stata eseguita correttamente
        if(!checkExtractionComplete(extractionMap)){
            throw new ExtractionFailedException();
        }

        //Salva il risultato dell'estrazione su DB
        storeExtractionResult(extractionMap);
    }

    //Persiste su DB i risultati dell'estrazione
    private void storeExtractionResult(Map<Integer, Set<Integer>> extractionMap){
        AppDatabase database = DatabaseHandler.getDatabase(context);
        for(Integer currParticipant : extractionMap.keySet()){
            EventResult eventResult = new EventResult();
            eventResult.setIdEvent(eventId);
            eventResult.setIdParticipantFrom(currParticipant);
            eventResult.setIdParticipantTo((Integer) extractionMap.get(currParticipant).toArray()[0]);
            database.eventResultDao().insertAll(eventResult);
        }
    }

    //Estrae dall'extractionMap il partecipante con il minor numero di possibili destinatari
    private Integer getNextParticipant(Map<Integer, Set<Integer>> extractionMap){
        Integer participantId = null;
        Integer numberOfRecipient = 0;

        for(Integer currParticipant : extractionMap.keySet()){
            if((extractionMap.get(currParticipant).size() < numberOfRecipient && extractionMap.get(currParticipant).size() > 1) || participantId == null){
                if(extractionMap.get(currParticipant).size() > 1) {
                    participantId = currParticipant;
                    numberOfRecipient = extractionMap.get(currParticipant).size();
                }
            }
        }

        return participantId;
    }

    //Verifica se ad ogni partecipante è associato uno ed un solo destinatario. In questo caso, l'estrazione è considerata conclusa
    private boolean checkExtractionComplete(Map<Integer, Set<Integer>> extractionMap){

        for(Integer currParticipant : extractionMap.keySet()){
            if(extractionMap.get(currParticipant).size() > 1 || extractionMap.get(currParticipant).size() <= 0){
                return false;
            }
        }

        return true;
    }

    //Estrae un ID casuale dalla Set dei possibili destinatari
    private Integer drawRecipient(Set<Integer> recipientSet){
        Random rand = new Random(System.currentTimeMillis());
        int index = rand.nextInt(recipientSet.size());
        Iterator<Integer> iter = recipientSet.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }

    //Rimuove dall'extractionMap gli Id dei destinatari già estratti
    private void cleanExtractionMap(Integer idToRemove, Map<Integer, Set<Integer>> extractionMap){
        for(Integer currParticipant : extractionMap.keySet()){
            if(extractionMap.get(currParticipant).size() > 1){
                extractionMap.get(currParticipant).remove(idToRemove);
            }
        }
    }

    //Costruisce una mappa che associa a tutti i partecipanti un set di possibili destinatari
    private Map<Integer, Set<Integer>> buildExtractionMap(){
        Map<Integer, Set<Integer>> extractionMap = new HashMap<Integer, Set<Integer>>();
        AppDatabase database = DatabaseHandler.getDatabase(context);
        List<ParticipantToEvent> participantList = database.participantToEventDao().getAllByEvent(eventId);
        if(participantList == null || participantList.size() <= 0){
            return null;
        }

        //Lista creata per estrarre randomicamente i partecipanti lasciando intatta la lista "participantList"
        List<ParticipantToEvent> participantListForExtraction = new ArrayList<ParticipantToEvent>();
        participantListForExtraction.addAll(participantList);

        Random rand = new Random();
        do {
            int randomIndex = rand.nextInt(participantListForExtraction.size());
            ParticipantToEvent randomElement = participantListForExtraction.get(randomIndex);
            List<ExclusionList> exclusionList = database.exclusionListDao().getAllByParticipantId(randomElement.getIdParticipant());
            Set<Integer> recipientList = buildRecipientList(randomElement, participantList, exclusionList);
            if(recipientList.size() <= 0){
                return null;
            }
            extractionMap.put(randomElement.getIdParticipant(), recipientList);
            participantListForExtraction.remove(randomIndex);
        } while(participantListForExtraction != null && participantListForExtraction.size() > 0);

        return extractionMap;
    }

    //Costruisce la lista dei possibili destinatari da associare ad ogni partecipante
    private Set<Integer> buildRecipientList(ParticipantToEvent participant,
                                             List<ParticipantToEvent> participantList,
                                             List<ExclusionList> exclusionList){
        Set<Integer> recipientList = new HashSet<Integer>();
        for(ParticipantToEvent currParticipant : participantList){
            //Inizia a riempire la lista dei possibili destinatari inserendo tutti i partecipanti
            recipientList.add(currParticipant.getIdParticipant());
        }

        //Rimuove dalla lista dei possibili destinatari il partecipante corrente e tutti quelli presenti nella tabella delle esclusioni
        recipientList.remove(participant.getIdParticipant());
        if(exclusionList != null && exclusionList.size() >= 0){
            for(ExclusionList currExclusion : exclusionList){
                recipientList.remove(currExclusion.getIdParticipantExcluded());
            }
        }

        return recipientList;
    }

}
